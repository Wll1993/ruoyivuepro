package cn.iocoder.yudao.module.oa.convert.user;

import cn.iocoder.yudao.module.oa.api.user.dto.AppUserRespDTO;
import cn.iocoder.yudao.module.oa.controller.app.user.vo.AppUserInfoRespVO;

import cn.iocoder.yudao.module.oa.controller.app.user.vo.profile.AppUserProfileRespVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.PostDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    AppUserProfileRespVO convert03(AdminUserDO bean);

    AppUserInfoRespVO convert(AdminUserDO bean);

    AppUserRespDTO convert2(AdminUserDO bean);

    AppUserProfileRespVO.Dept convert02(DeptDO bean);

    List<AppUserRespDTO> convertList2(List<AdminUserDO> list);

    List<AppUserProfileRespVO.Role> convertList(List<RoleDO> list);
    List<AppUserProfileRespVO.Post> convertList02(List<PostDO> list);
    List<AppUserProfileRespVO.SocialUser> convertList03(List<SocialUserDO> list);
}
