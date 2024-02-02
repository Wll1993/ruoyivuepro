package cn.iocoder.yudao.module.steam.dal.mysql.devaccount;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.steam.dal.dataobject.devaccount.DevAccountDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.steam.controller.admin.devaccount.vo.*;

/**
 * 开放平台用户 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DevAccountMapper extends BaseMapperX<DevAccountDO> {

    default PageResult<DevAccountDO> selectPage(DevAccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DevAccountDO>()
                .likeIfPresent(DevAccountDO::getUserName, reqVO.getUserName())
                .eqIfPresent(DevAccountDO::getSteamId, reqVO.getSteamId())
                .eqIfPresent(DevAccountDO::getStatus, reqVO.getStatus())
                .orderByDesc(DevAccountDO::getId));
    }

}