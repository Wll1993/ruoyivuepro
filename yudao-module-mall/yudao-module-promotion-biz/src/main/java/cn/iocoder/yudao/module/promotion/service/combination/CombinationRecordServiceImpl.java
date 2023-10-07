package cn.iocoder.yudao.module.promotion.service.combination;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.core.KeyValue;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import cn.iocoder.yudao.module.product.api.sku.ProductSkuApi;
import cn.iocoder.yudao.module.product.api.sku.dto.ProductSkuRespDTO;
import cn.iocoder.yudao.module.product.api.spu.ProductSpuApi;
import cn.iocoder.yudao.module.product.api.spu.dto.ProductSpuRespDTO;
import cn.iocoder.yudao.module.promotion.api.combination.dto.CombinationRecordCreateReqDTO;
import cn.iocoder.yudao.module.promotion.api.combination.dto.CombinationValidateJoinRespDTO;
import cn.iocoder.yudao.module.promotion.controller.admin.combination.vo.recrod.CombinationRecordReqPageVO;
import cn.iocoder.yudao.module.promotion.convert.combination.CombinationActivityConvert;
import cn.iocoder.yudao.module.promotion.dal.dataobject.combination.CombinationActivityDO;
import cn.iocoder.yudao.module.promotion.dal.dataobject.combination.CombinationProductDO;
import cn.iocoder.yudao.module.promotion.dal.dataobject.combination.CombinationRecordDO;
import cn.iocoder.yudao.module.promotion.dal.mysql.combination.CombinationRecordMapper;
import cn.iocoder.yudao.module.promotion.enums.combination.CombinationRecordStatusEnum;
import cn.iocoder.yudao.module.trade.api.order.TradeOrderApi;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.afterNow;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.beforeNow;
import static cn.iocoder.yudao.module.promotion.enums.ErrorCodeConstants.*;

// TODO 芋艿：等拼团记录做完，完整 review 下

/**
 * 拼团记录 Service 实现类
 *
 * @author HUIHUI
 */
@Service
@Validated
public class CombinationRecordServiceImpl implements CombinationRecordService {

    @Resource
    @Lazy
    private CombinationActivityService combinationActivityService;
    @Resource
    private CombinationRecordMapper recordMapper;

    @Resource
    private MemberUserApi memberUserApi;
    @Resource
    @Lazy
    private ProductSpuApi productSpuApi;
    @Resource
    @Lazy
    private ProductSkuApi productSkuApi;
    @Resource
    private TradeOrderApi tradeOrderApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCombinationRecordStatusByUserIdAndOrderId(Integer status, Long userId, Long orderId) {
        // 校验拼团是否存在
        CombinationRecordDO record = validateCombinationRecord(userId, orderId);

        // 更新状态
        record.setStatus(status);
        recordMapper.updateById(record);
    }

    private CombinationRecordDO validateCombinationRecord(Long userId, Long orderId) {
        // 校验拼团是否存在
        CombinationRecordDO recordDO = recordMapper.selectByUserIdAndOrderId(userId, orderId);
        if (recordDO == null) {
            throw exception(COMBINATION_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

    // TODO @芋艿：在详细预览下；
    @Override
    public KeyValue<CombinationActivityDO, CombinationProductDO> validateCombinationRecord(
            Long userId, Long activityId, Long headId, Long skuId, Integer count) {
        // 1 校验拼团活动是否存在
        CombinationActivityDO activity = combinationActivityService.validateCombinationActivityExists(activityId);
        // 1.1 校验活动是否开启
        if (ObjUtil.equal(activity.getStatus(), CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(COMBINATION_ACTIVITY_STATUS_DISABLE);
        }
        // 1.2、校验活动开始时间
        if (afterNow(activity.getStartTime())) {
            throw exception(COMBINATION_RECORD_FAILED_TIME_NOT_START);
        }
        // 1.3 校验是否超出单次限购数量
        if (count > activity.getSingleLimitCount()) {
            throw exception(COMBINATION_RECORD_FAILED_SINGLE_LIMIT_COUNT_EXCEED);
        }

        // 2、父拼团是否存在,是否已经满了
        if (headId != null) {
            // 2.1、查询进行中的父拼团
            CombinationRecordDO record = recordMapper.selectOneByHeadId(headId, CombinationRecordStatusEnum.IN_PROGRESS.getStatus());
            if (record == null) {
                throw exception(COMBINATION_RECORD_HEAD_NOT_EXISTS);
            }
            // 2.2、校验拼团是否满足要求
            if (ObjUtil.equal(record.getUserCount(), record.getUserSize())) {
                throw exception(COMBINATION_RECORD_USER_FULL);
            }
            // 2.3、校验拼团是否过期（有父拼团的时候只校验父拼团的过期时间）
            if (beforeNow(record.getExpireTime())) {
                throw exception(COMBINATION_RECORD_FAILED_TIME_END);
            }
        } else {
            // 3、校验当前活动是否结束(自己是父拼团的时候才校验活动是否结束)
            if (beforeNow(activity.getEndTime())) {
                throw exception(COMBINATION_RECORD_FAILED_TIME_END);
            }
        }

        // 4、校验活动商品是否存在
        CombinationProductDO product = combinationActivityService.selectByActivityIdAndSkuId(activityId, skuId);
        if (product == null) {
            throw exception(COMBINATION_JOIN_ACTIVITY_PRODUCT_NOT_EXISTS);
        }

        // 5、校验 sku 是否存在
        ProductSkuRespDTO sku = productSkuApi.getSku(skuId);
        if (sku == null) {
            throw exception(COMBINATION_JOIN_ACTIVITY_PRODUCT_NOT_EXISTS);
        }
        // 5.1、校验库存是否充足
        if (count > sku.getStock()) {
            throw exception(COMBINATION_ACTIVITY_UPDATE_STOCK_FAIL);
        }

        // 6、校验是否有拼团记录
        List<CombinationRecordDO> recordList = getCombinationRecordListByUserIdAndActivityId(userId, activityId);
        if (CollUtil.isEmpty(recordList)) {
            return new KeyValue<>(activity, product);
        }
        // 6.1、校验用户是否有该活动正在进行的拼团
        List<CombinationRecordDO> filtered = filterList(recordList, record -> CombinationRecordStatusEnum.isInProgress(record.getStatus()));
        if (CollUtil.isNotEmpty(filtered)) {
            throw exception(COMBINATION_RECORD_FAILED_HAVE_JOINED);
        }
        // 6.2、校验是否超出总限购数量
        Integer sumValue = getSumValue(convertList(recordList, CombinationRecordDO::getCount,
                item -> CombinationRecordStatusEnum.isSuccess(item.getStatus())), i -> i, Integer::sum);
        if ((sumValue + count) > activity.getTotalLimitCount()) {
            throw exception(COMBINATION_RECORD_FAILED_TOTAL_LIMIT_COUNT_EXCEED);
        }

        return new KeyValue<>(activity, product);
    }

    // TODO 芋艿：在详细 review 下；
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCombinationRecord(CombinationRecordCreateReqDTO reqDTO) {
        // 1、校验拼团活动
        KeyValue<CombinationActivityDO, CombinationProductDO> keyValue = validateCombinationRecord(reqDTO.getUserId(),
                reqDTO.getActivityId(), reqDTO.getHeadId(), reqDTO.getSkuId(), reqDTO.getCount());

        // 2. 组合数据创建拼团记录
        MemberUserRespDTO user = memberUserApi.getUser(reqDTO.getUserId());
        ProductSpuRespDTO spu = productSpuApi.getSpu(reqDTO.getSpuId());
        ProductSkuRespDTO sku = productSkuApi.getSku(reqDTO.getSkuId());
        CombinationRecordDO recordDO = CombinationActivityConvert.INSTANCE.convert(reqDTO, keyValue.getKey(), user, spu, sku);
        recordMapper.insert(recordDO);

        // 3、如果是团长需要设置 headId 为 CombinationRecordDO#HEAD_ID_GROUP
        if (reqDTO.getHeadId() == null) {
            recordMapper.updateById(new CombinationRecordDO().setId(recordDO.getId()).setHeadId(CombinationRecordDO.HEAD_ID_GROUP));
            return;
        }

        // TODO 这里要不要弄成异步的
        // 4、更新拼团相关信息到订单
        updateOrderCombinationInfo(recordDO.getOrderId(), recordDO.getActivityId(), recordDO.getId(), recordDO.getHeadId());
        // 4、更新拼团记录
        updateCombinationRecords(keyValue.getKey(), reqDTO.getHeadId());

    }

    /**
     * 更新拼团相关信息到订单
     *
     * @param orderId             订单编号
     * @param activityId          拼团活动编号
     * @param combinationRecordId 拼团记录编号
     * @param headId              团长编号
     */
    private void updateOrderCombinationInfo(Long orderId, Long activityId, Long combinationRecordId, Long headId) {
        tradeOrderApi.updateOrderCombinationInfo(orderId, activityId, combinationRecordId, headId);
    }

    /**
     * 更新拼团记录
     *
     * @param activity 活动
     * @param headId   团长编号
     */
    private void updateCombinationRecords(CombinationActivityDO activity, Long headId) {
        List<CombinationRecordDO> records = recordMapper.selectList(CombinationRecordDO::getHeadId, headId);
        List<CombinationRecordDO> updateRecords = new ArrayList<>();

        if (CollUtil.isEmpty(records)) {
            return;
        }

        boolean isEqual = ObjUtil.equal(records.size(), activity.getUserSize());
        records.forEach(item -> {
            CombinationRecordDO recordDO = new CombinationRecordDO();
            recordDO.setId(item.getId());
            recordDO.setUserCount(records.size());
            // 校验拼团是否满足要求
            if (isEqual) {
                recordDO.setStatus(CombinationRecordStatusEnum.SUCCESS.getStatus());
            }
            updateRecords.add(recordDO);
        });

        recordMapper.updateBatch(updateRecords);
    }

    @Override
    public CombinationRecordDO getCombinationRecord(Long userId, Long orderId) {
        return recordMapper.selectByUserIdAndOrderId(userId, orderId);
    }

    @Override
    public List<CombinationRecordDO> getCombinationRecordListByUserIdAndActivityId(Long userId, Long activityId) {
        return recordMapper.selectListByUserIdAndActivityId(userId, activityId);
    }

    @Override
    public CombinationValidateJoinRespDTO validateJoinCombination(Long userId, Long activityId, Long headId, Long skuId, Integer count) {
        KeyValue<CombinationActivityDO, CombinationProductDO> keyValue = validateCombinationRecord(userId, activityId, headId, skuId, count);
        return new CombinationValidateJoinRespDTO()
                .setActivityId(keyValue.getKey().getId())
                .setName(keyValue.getKey().getName())
                .setCombinationPrice(keyValue.getValue().getCombinationPrice());
    }

    @Override
    public Long getCombinationRecordCount() {
        return recordMapper.selectCount();
    }

    @Override
    public Long getCombinationRecordsSuccessCount() {
        return recordMapper.selectCount(CombinationRecordDO::getStatus, CombinationRecordStatusEnum.SUCCESS.getStatus());
    }

    @Override
    public Long getRecordsVirtualGroupCount() {
        return recordMapper.selectCount(CombinationRecordDO::getVirtualGroup, true);
    }

    @Override
    public Long getCombinationRecordsCountByDateType(Integer dateType) {
        return recordMapper.selectCount(dateType);
    }

    @Override
    public List<CombinationRecordDO> getLatestCombinationRecordList(int count) {
        return recordMapper.selectLatestList(count);
    }

    @Override
    public List<CombinationRecordDO> getHeadCombinationRecordList(Long activityId, Integer status, Integer count) {
        return recordMapper.selectListByActivityIdAndStatusAndHeadId(activityId, status,
                CombinationRecordDO.HEAD_ID_GROUP, count);
    }

    @Override
    public CombinationRecordDO getCombinationRecordById(Long id) {
        return recordMapper.selectById(id);
    }

    @Override
    public List<CombinationRecordDO> getCombinationRecordListByHeadId(Long headId) {
        return recordMapper.selectList(CombinationRecordDO::getHeadId, headId);
    }

    @Override
    public PageResult<CombinationRecordDO> getCombinationRecordPage(CombinationRecordReqPageVO pageVO) {
        return recordMapper.selectPage(pageVO);
    }

    @Override
    public Map<Long, Integer> getCombinationRecordCountMapByActivity(Collection<Long> activityIds,
                                                                     @Nullable Integer status, @Nullable Long headId) {
        return recordMapper.selectCombinationRecordCountMapByActivityIdAndStatusAndHeadId(activityIds, status, headId);
    }

}
