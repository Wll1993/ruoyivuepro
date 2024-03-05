package cn.iocoder.yudao.module.steam.dal.mysql.selling;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.steam.dal.dataobject.selling.SellingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.steam.controller.admin.selling.vo.*;

/**
 * 在售饰品 Mapper
 *
 * @author 管理员
 */
@Mapper
public interface SellingMapper extends BaseMapperX<SellingDO> {

    default PageResult<SellingDO> selectPage(SellingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SellingDO>()
                .eqIfPresent(SellingDO::getAppid, reqVO.getAppid())
                .eqIfPresent(SellingDO::getAssetid, reqVO.getAssetid())
                .eqIfPresent(SellingDO::getClassid, reqVO.getClassid())
                .eqIfPresent(SellingDO::getInstanceid, reqVO.getInstanceid())
                .eqIfPresent(SellingDO::getAmount, reqVO.getAmount())
                .betweenIfPresent(SellingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SellingDO::getSteamId, reqVO.getSteamId())
                .eqIfPresent(SellingDO::getStatus, reqVO.getStatus())
                .eqIfPresent(SellingDO::getPrice, reqVO.getPrice())
                .eqIfPresent(SellingDO::getUserId, reqVO.getUserId())
                .eqIfPresent(SellingDO::getUserType, reqVO.getUserType())
                .eqIfPresent(SellingDO::getBindUserId, reqVO.getBindUserId())
                .eqIfPresent(SellingDO::getContextid, reqVO.getContextid())
                .eqIfPresent(SellingDO::getInvDescId, reqVO.getInvDescId())
                .eqIfPresent(SellingDO::getTransferStatus, reqVO.getTransferStatus())
                .eqIfPresent(SellingDO::getInvId, reqVO.getInvId())
                .orderByDesc(SellingDO::getId));
    }

}