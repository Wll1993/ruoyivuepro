package cn.iocoder.yudao.module.steam.controller.app.InventorySearch;


import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.steam.controller.admin.inv.vo.InvPageReqVO;
import cn.iocoder.yudao.module.steam.dal.dataobject.binduser.BindUserDO;
import cn.iocoder.yudao.module.steam.dal.dataobject.inv.InvDO;
import cn.iocoder.yudao.module.steam.dal.mysql.binduser.BindUserMapper;
import cn.iocoder.yudao.module.steam.dal.mysql.inv.InvMapper;
import cn.iocoder.yudao.module.steam.dal.mysql.invdesc.InvDescMapper;
import cn.iocoder.yudao.module.steam.service.SteamInvService;
import cn.iocoder.yudao.module.steam.service.binduser.BindUserService;
import cn.iocoder.yudao.module.steam.service.inv.InvService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @description: 库存查询
 */
@RestController
@RequestMapping("/steam-app/inventory")
@Validated
@Slf4j
public class AppInventorySearchController {

    @Resource
    private SteamInvService steamInvService;
    @Resource
    private InvService invService;
    @Resource
    private BindUserMapper bindUserMapper;
    @Resource
    private InvDescMapper invDescMapper;
    @Resource
    private InvMapper invMapper;


    /**
     * 用户手动查询自己的 steam_inv 库存（从数据库中获取数据）
     * @param invPageReqVO steamid
     * @return
     */
    @GetMapping("/after_SearchInDB")
    @Operation(summary = "从数据库中查询数据")
    public CommonResult<PageResult<InvDO>> SearchInDB(@Valid InvPageReqVO invPageReqVO) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        List<BindUserDO> collect = bindUserMapper.selectList()
                .stream()
                .filter(o -> o.getSteamId().equals(invPageReqVO.getSteamId()))
                .collect(Collectors.toList());
        assert loginUser != null;
        if(!(loginUser.getId()).equals(collect.get(0).getUserId())){
            throw new ServiceException(-1,"您没有权限获取该用户的库存信息");
        }
        PageResult<InvDO> invPage = invService.getInvPage(invPageReqVO);
        return success(invPage);
    }

//
//    /**
//     * 用户手动查询自己的 steam_inv 库存（从线上获取数据）
//     * @param invPageReqVO steamid
//     * @return
//     */
//    @GetMapping("/after_SearchFromSteam")
//    @Operation(summary = "查询数据库的库存数据")
//    public CommonResult<PageResult<InvDO>> SearchFromSteam(@Valid InvPageReqVO invPageReqVO) {
//        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
//        List<BindUserDO> collect = bindUserMapper.selectList()
//                .stream()
//                .filter(o -> o.getSteamId().equals(invPageReqVO.getSteamId()))
//                .collect(Collectors.toList());
//        assert loginUser != null;
//        if((loginUser.getId()).equals(collect.get(0).getUserId())){
//            invPageReqVO.setUserId(loginUser.getId());
//        } else {
//            throw new ServiceException(-1,"您没有权限获取该用户的库存信息");
//        }
//
//        return success(invService.getInvPage(invPageReqVO));
//    }


}
