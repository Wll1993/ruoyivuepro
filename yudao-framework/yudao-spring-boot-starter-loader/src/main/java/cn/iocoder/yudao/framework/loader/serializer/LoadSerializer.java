package cn.iocoder.yudao.framework.loader.serializer;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.thread.lock.LockUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.loader.annotation.Load;
import cn.iocoder.yudao.framework.loader.bo.AnnotationsResult;
import cn.iocoder.yudao.framework.loader.handler.params.ParamsHandler;
import cn.iocoder.yudao.framework.loader.handler.rsp.ResponseHandler;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

@Slf4j
public class LoadSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    /**
     * 值缓存时间
     */
    private static final int dataCacheMinutes = 5;

    /**
     * 锁缓存时间
     */
    private static final int lockCacheMinutes = 10;

    /**
     * 缓存检测周期，单位秒
     */
    private static final int cacheCheckScheduleTime = 10;

    /**
     * 成功翻译
     */
    private static final TimedCache<String, Object> success = new TimedCache(TimeUnit.MINUTES.toMillis(dataCacheMinutes), new ConcurrentHashMap<>());

    /**
     * 失败翻译
     */
    private static final TimedCache<String, Object> error = new TimedCache(TimeUnit.MINUTES.toMillis(dataCacheMinutes), new ConcurrentHashMap<>());

    /**
     * 锁避免同时请求同一ID
     */
    private static final TimedCache<String, StampedLock> lockMap = new TimedCache<>(TimeUnit.MINUTES.toMillis(lockCacheMinutes), new ConcurrentHashMap<>());

    /**
     * 远程调用服务原始calss
     */
    private String loadServiceSourceClassName;

    /**
     * 远程调用服务
     */
    private Object loadService;

    /**
     * 方法
     */
    private String method;

    /**
     * 缓存时间
     */
    private int cacheSecond;

    /**
     * 注解参数处理
     */
    private AnnotationsResult annotationsResult;

    /**
     * 返回结果处理类
     */
    private ParamsHandler paramsHandler;

    /**
     * 返回结果处理类
     */
    private ResponseHandler responseHandler;

    /**
     * 远程服务前缀
     */
    private String prefix;

    public LoadSerializer() {
        super();
        success.schedulePrune(TimeUnit.SECONDS.toMillis(cacheCheckScheduleTime));
        error.schedulePrune(TimeUnit.SECONDS.toMillis(cacheCheckScheduleTime));
        lockMap.schedulePrune(TimeUnit.SECONDS.toMillis(cacheCheckScheduleTime));
    }

    public LoadSerializer(String loadService, String method, int cacheSecond, AnnotationsResult annotationsResult, ParamsHandler paramsHandler, ResponseHandler otherResponseHandler) {
        this.loadServiceSourceClassName = loadService;
        this.loadService = SpringUtil.getBean(loadService);
        this.method = method;
        this.cacheSecond = cacheSecond;
        this.annotationsResult = annotationsResult;
        this.responseHandler = otherResponseHandler;
        this.paramsHandler = paramsHandler;
        prefix = loadServiceSourceClassName + "-";
    }

    @Override
    public void serialize(Object bindData, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (bindData == null || loadService == null) {
            gen.writeObject(null);
            return;
        }

        Object params = paramsHandler.handleVal(bindData);
        gen.writeObject(bindData);
        String writeField = annotationsResult.getWriteField();
        boolean custom = false;
        Class<?> writeClass = Object.class;
        if (writeField != null) {
            Field field = ReflectUtil.getField(gen.getCurrentValue().getClass(), annotationsResult.getWriteField());
            if (field != null) {
                writeClass = field.getType();
                custom = true;
            }
        }
        if (!custom) {
            writeField = '$' + gen.getOutputContext().getCurrentName();
        }
        gen.writeFieldName(writeField);

        // 有效ID，去查询
        Object[] args = annotationsResult.getRemoteParams();
        String cacheKey = prefix + method + "-" + paramsHandler.getCacheKey(bindData, args);
        Object result = getCacheInfo(cacheKey);
        if (result == null) {
            StampedLock lock = lockMap.get(cacheKey, true, LockUtil::createStampLock);
            Lock writeLock = lock.asWriteLock();
            try {
                // 获取锁成功后请求这个ID
                writeLock.lock();
                // 再次尝试拿缓存
                result = getCacheInfo(cacheKey);
                if (result == null) {
                    // 多参数组装
                    List<Object> objectParams = new ArrayList<>();
                    objectParams.add(params);
                    if (args != null && args.length > 0) {
                        Collections.addAll(objectParams, args);
                    }
                    Object r = ReflectUtil.invoke(loadService, method, objectParams.toArray());
                    if (r != null) {
                        result = this.responseHandler.handle(this.loadServiceSourceClassName, method, r, writeClass, objectParams.toArray());
                        if (cacheSecond > 0) {
                            success.put(cacheKey, result, TimeUnit.SECONDS.toMillis(cacheSecond));
                        }
                    } else {
                        log.error("【{}】 翻译失败，未找到：{}", prefix, params);
                        error.put(cacheKey, bindData);
                        result = null;
                    }
                }
            } catch (Exception e) {
                log.error("【{}】翻译服务异常：{}", prefix, e);
                error.put(cacheKey, bindData);
                result = bindData;
            } finally {
                writeLock.unlock();
            }
        }
        gen.writeObject(result);
    }

    /**
     * 获取厍信息
     *
     * @param cacheKey
     * @return
     */
    private Object getCacheInfo(String cacheKey) {
        Object result = success.get(cacheKey, false);
        if (result == null) {
            result = error.get(cacheKey, false);
        }
        return result;
    }

    @SneakyThrows
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            Load load = property.getAnnotation(Load.class);
            if (load == null) {
                throw new RuntimeException("未注解相关 @Load 注解");
            }
            String bean = load.bean();
            Class<? extends ParamsHandler> paramsHandlerClass = load.paramsHandler();
            Class<? extends ResponseHandler> responseHandlerClass = load.responseHandler();
            String method = load.method();
            ParamsHandler paramsHandler = paramsHandlerClass.getDeclaredConstructor().newInstance();
            ResponseHandler responseHandler = responseHandlerClass.getDeclaredConstructor().newInstance();
            int cacheSecond = load.cacheSecond();
            // 额外参数处理
            AnnotationsResult annotationsResult = paramsHandler.handleAnnotation(property);
            return new LoadSerializer(bean, method, cacheSecond, annotationsResult, paramsHandler, responseHandler);
        }
        return prov.findNullValueSerializer(property);
    }
}


