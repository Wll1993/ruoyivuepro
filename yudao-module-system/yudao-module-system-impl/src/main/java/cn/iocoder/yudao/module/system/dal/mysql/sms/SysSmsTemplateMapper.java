package cn.iocoder.yudao.module.system.dal.mysql.sms;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.sms.vo.template.SmsTemplateExportReqVO;
import cn.iocoder.yudao.module.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.sms.SysSmsTemplateDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysSmsTemplateMapper extends BaseMapperX<SysSmsTemplateDO> {

    @Select("SELECT id FROM sys_sms_template WHERE update_time > #{maxUpdateTime} LIMIT 1")
    Long selectExistsByUpdateTimeAfter(Date maxUpdateTime);

    default SysSmsTemplateDO selectByCode(String code) {
        return selectOne(SysSmsTemplateDO::getCode, code);
    }

    default PageResult<SysSmsTemplateDO> selectPage(SmsTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SysSmsTemplateDO>()
                .eqIfPresent(SysSmsTemplateDO::getType, reqVO.getType())
                .eqIfPresent(SysSmsTemplateDO::getStatus, reqVO.getStatus())
                .likeIfPresent(SysSmsTemplateDO::getCode, reqVO.getCode())
                .likeIfPresent(SysSmsTemplateDO::getContent, reqVO.getContent())
                .likeIfPresent(SysSmsTemplateDO::getApiTemplateId, reqVO.getApiTemplateId())
                .eqIfPresent(SysSmsTemplateDO::getChannelId, reqVO.getChannelId())
                .betweenIfPresent(SysSmsTemplateDO::getCreateTime, reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc(SysSmsTemplateDO::getId));
    }

    default List<SysSmsTemplateDO> selectList(SmsTemplateExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<SysSmsTemplateDO>()
                .eqIfPresent(SysSmsTemplateDO::getType, reqVO.getType())
                .eqIfPresent(SysSmsTemplateDO::getStatus, reqVO.getStatus())
                .likeIfPresent(SysSmsTemplateDO::getCode, reqVO.getCode())
                .likeIfPresent(SysSmsTemplateDO::getContent, reqVO.getContent())
                .likeIfPresent(SysSmsTemplateDO::getApiTemplateId, reqVO.getApiTemplateId())
                .eqIfPresent(SysSmsTemplateDO::getChannelId, reqVO.getChannelId())
                .betweenIfPresent(SysSmsTemplateDO::getCreateTime, reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc(SysSmsTemplateDO::getId));
    }

    default Integer selectCountByChannelId(Long channelId) {
        return selectCount(SysSmsTemplateDO::getChannelId, channelId);
    }

}
