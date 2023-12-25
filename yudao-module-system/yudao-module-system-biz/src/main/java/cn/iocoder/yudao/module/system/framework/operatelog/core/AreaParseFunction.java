package cn.iocoder.yudao.module.system.framework.operatelog.core;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
import com.mzt.logapi.service.IParseFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 自定义函数-通过区域编号获取区域信息
 *
 * @author HUIHUI
 */
@Slf4j
@Component
public class AreaParseFunction implements IParseFunction {

    @Override
    public boolean executeBefore() {
        return true; // 先转换值后对比
    }

    @Override
    public String functionName() {
        return "getAreaById";
    }

    @Override
    public String apply(Object value) {
        if (ObjUtil.isEmpty(value)) {
            return "";
        }
        if (StrUtil.isEmpty(value.toString())) {
            return "";
        }

        return AreaUtils.format(Integer.parseInt(value.toString()));
    }

}