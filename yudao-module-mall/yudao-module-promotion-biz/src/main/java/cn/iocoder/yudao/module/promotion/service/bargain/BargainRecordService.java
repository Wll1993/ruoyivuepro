package cn.iocoder.yudao.module.promotion.service.bargain;


import cn.iocoder.yudao.module.promotion.api.bargain.dto.BargainValidateJoinRespDTO;
import cn.iocoder.yudao.module.promotion.controller.app.bargain.vo.record.AppBargainRecordCreateReqVO;
import cn.iocoder.yudao.module.promotion.dal.dataobject.bargain.BargainRecordDO;

/**
 * 砍价记录 service 接口
 *
 * @author HUIHUI
 */
public interface BargainRecordService {

    /**
     * 【会员】创建砍价记录（参与参加活动）
     *
     * @param userId 用户编号
     * @param reqVO 创建信息
     * @return 砍价记录编号
     */
    Long createBargainRecord(Long userId, AppBargainRecordCreateReqVO reqVO);

    /**
     * 更新砍价记录的砍价金额
     *
     * 如果满足砍价成功的条件，则更新砍价记录的状态为成功
     *
     * @param id 砍价记录编号
     * @param whereBargainPrice 当前的砍价金额
     * @param reducePrice 减少的砍价金额
     * @param success 是否砍价成功
     * @return 是否更新成功。注意，如果并发更新时，会更新失败
     */
    Boolean updateBargainRecordBargainPrice(Long id, Integer whereBargainPrice,
                                            Integer reducePrice, Boolean success);

    /**
     * 【下单前】校验是否参与砍价活动
     * <p>
     * 如果校验失败，则抛出业务异常
     *
     * @param userId          用户编号
     * @param bargainRecordId 砍价活动编号
     * @param skuId           SKU 编号
     * @return 砍价信息
     */
    BargainValidateJoinRespDTO validateJoinBargain(Long userId, Long bargainRecordId, Long skuId);

    /**
     * 获得砍价记录
     *
     * @param id 砍价记录编号
     * @return 砍价记录
     */
    BargainRecordDO getBargainRecord(Long id);

}
