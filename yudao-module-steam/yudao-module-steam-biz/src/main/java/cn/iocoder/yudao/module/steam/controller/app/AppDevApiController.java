package cn.iocoder.yudao.module.steam.controller.app;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import cn.iocoder.yudao.module.pay.controller.admin.demo.vo.order.PayDemoOrderCreateReqVO;
import cn.iocoder.yudao.module.steam.controller.admin.selexterior.vo.SelExteriorPageReqVO;
import cn.iocoder.yudao.module.steam.controller.admin.selitemset.vo.SelItemsetListReqVO;
import cn.iocoder.yudao.module.steam.controller.admin.selquality.vo.SelQualityPageReqVO;
import cn.iocoder.yudao.module.steam.controller.admin.selrarity.vo.SelRarityPageReqVO;
import cn.iocoder.yudao.module.steam.controller.admin.seltype.vo.SelTypePageReqVO;
import cn.iocoder.yudao.module.steam.controller.app.droplist.vo.AppDropListRespVO;
import cn.iocoder.yudao.module.steam.controller.app.vo.OpenApiReqVo;
import cn.iocoder.yudao.module.steam.controller.app.vo.PaySteamOrderCreateReqVO;
import cn.iocoder.yudao.module.steam.dal.mysql.seltype.SelWeaponMapper;
import cn.iocoder.yudao.module.steam.service.OpenApiService;
import cn.iocoder.yudao.module.steam.service.SteamService;
import cn.iocoder.yudao.module.steam.service.fin.PaySteamOrderService;
import cn.iocoder.yudao.module.steam.service.selexterior.SelExteriorService;
import cn.iocoder.yudao.module.steam.service.selitemset.SelItemsetService;
import cn.iocoder.yudao.module.steam.service.selquality.SelQualityService;
import cn.iocoder.yudao.module.steam.service.selrarity.SelRarityService;
import cn.iocoder.yudao.module.steam.service.seltype.SelTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "steam后台 - devApi")
@RestController
@RequestMapping("devapi")
@Validated
public class AppDevApiController {
    @Resource
    private SelExteriorService selExteriorService;
    @Resource
    private SelItemsetService selItemsetService;
    @Resource
    private SelQualityService selQualityService;
    @Resource
    private SelTypeService selTypeService;
    @Resource
    private SelRarityService selRarityService;
    @Resource
    private OpenApiService openApiService;
    @Resource
    private SelWeaponMapper selWeaponMapper;
    @Resource
    private SteamService steamService;

    @Resource
    private PaySteamOrderService payDemoOrderService;

    /**
     * 类别选择
     */
    @PostMapping("/openapi")
    @Operation(summary = "")
    public CommonResult<String> openApi(@RequestBody @Validated OpenApiReqVo openApi) {
        try {
            String despatch = openApiService.despatch(openApi);
            return CommonResult.success(despatch);
        } catch (ServiceException e) {
            return CommonResult.error(new ErrorCode(01, "接口出错原因:" + e.getMessage()));
        }
    }

    /**
     * 类别选择
     */
    @GetMapping("/drop_list")
    @PermitAll
    @Operation(summary = "获取类别选择下拉信息")
    public CommonResult<SelQualityPageReqVO> getQuality() {
        AppDropListRespVO appDropListRespVO = new AppDropListRespVO();
        SelQualityPageReqVO quality = new SelQualityPageReqVO();
        quality.setPageSize(200);
        quality.setPageNo(1);
        appDropListRespVO.setQuality(selQualityService.getSelQualityPage(quality).getList());
        return CommonResult.success(quality);
    }

    /**
     * 收藏品选择
     */
    @GetMapping("/drop_list_itemSet")
    @PermitAll
    @Operation(summary = "获取收藏品选择下拉信息")
    public CommonResult<AppDropListRespVO> getItemSet() {
        AppDropListRespVO appDropListRespVO = new AppDropListRespVO();
        SelItemsetListReqVO itemset = new SelItemsetListReqVO();
        appDropListRespVO.setItemset(selItemsetService.getSelItemsetList(itemset));
        return CommonResult.success(appDropListRespVO);
    }

    /**
     * 类型选择
     */
    @GetMapping("/drop_list_type")
    @PermitAll
    @Operation(summary = "获取类型选择下拉信息")
    public CommonResult<AppDropListRespVO> getType() {
        AppDropListRespVO appDropListRespVO = new AppDropListRespVO();
        SelTypePageReqVO type = new SelTypePageReqVO();
        type.setPageSize(200);
        type.setPageNo(1);
        appDropListRespVO.setType(selTypeService.getSelTypePage(type).getList());
        return CommonResult.success(appDropListRespVO);
    }

    /**
     * 品质选择
     */
    @GetMapping("/drop_list_rarity")
    @PermitAll
    @Operation(summary = "获取品质选择下拉信息")
    public CommonResult<AppDropListRespVO> getRarity() {
        AppDropListRespVO appDropListRespVO = new AppDropListRespVO();
        SelRarityPageReqVO rarity = new SelRarityPageReqVO();
        rarity.setPageSize(200);
        rarity.setPageNo(1);
        appDropListRespVO.setRarity(selRarityService.getSelRarityPage(rarity).getList());
        return CommonResult.success(appDropListRespVO);
    }

    /**
     * 武器选择
     */
    @GetMapping("/drop_list_weapon")
    @PermitAll
    @Operation(summary = "获取武器选择下拉信息")
    public CommonResult<AppDropListRespVO> getWeapon() {
        AppDropListRespVO appDropListRespVO = new AppDropListRespVO();
        SelTypePageReqVO type_weapon = new SelTypePageReqVO();
        type_weapon.setPageSize(200);
        type_weapon.setPageNo(1);
//        appDropListRespVO.setWeapon(selTypeService.getSelWeaponPage(type_weapon,typeId).getList());
//        selWeaponMapper.selectList();
        appDropListRespVO.setWeapon(selWeaponMapper.selectList());
        return CommonResult.success(appDropListRespVO);

    }

    /**
     * 外观选择
     */
    @GetMapping("/drop_list_exterior")
    @PermitAll
    @Operation(summary = "获取外观选择下拉信息")
    public CommonResult<AppDropListRespVO> getExterior() {
        AppDropListRespVO appDropListRespVO = new AppDropListRespVO();
        SelExteriorPageReqVO exterior = new SelExteriorPageReqVO();
        exterior.setPageSize(200);
        exterior.setPageNo(1);
        appDropListRespVO.setExterior(selExteriorService.getSelExteriorPage(exterior).getList());
        return CommonResult.success(appDropListRespVO);
    }


    @GetMapping("/test")

    public CommonResult<String> test() {
        TenantUtils.execute(1l,()->{
            steamService.fetchInventory("76561198316318254","730");
        });

        return success("test");
    }
    @PostMapping("/testOrder")
    @Operation(summary = "创建示例订单")
    @PermitAll
    public CommonResult<Long> createDemoOrder(@Valid @RequestBody PayDemoOrderCreateReqVO createReqVO) {
        TenantUtils.execute(1l,()->{
            PaySteamOrderCreateReqVO paySteamOrderCreateReqVO=new PaySteamOrderCreateReqVO();
            paySteamOrderCreateReqVO.setPrice(200);
            paySteamOrderCreateReqVO.setAssetId("35644141857");
            paySteamOrderCreateReqVO.setClassId("3035569977");
            paySteamOrderCreateReqVO.setInstanceId("302028390");
            paySteamOrderCreateReqVO.setName("测试商品");
            Long demoOrder = payDemoOrderService.createDemoOrder(1l, paySteamOrderCreateReqVO);
            return CommonResult.success(demoOrder);
        });
        return CommonResult.error(-1,"出错");
    }
    @PostMapping("/update-paid")
    @Operation(summary = "更新示例订单为已支付") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
    @PermitAll // 无需登录，安全由 PayDemoOrderService 内部校验实现
    @OperateLog(enable = false) // 禁用操作日志，因为没有操作人
    public CommonResult<Boolean> updateDemoOrderPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        payDemoOrderService.updateDemoOrderPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
                notifyReqDTO.getPayOrderId());
        return success(true);
    }
    @PostMapping("/update-refunded")
    @Operation(summary = "更新示例订单为已退款") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
    @PermitAll // 无需登录，安全由 PayDemoOrderService 内部校验实现
    @OperateLog(enable = false) // 禁用操作日志，因为没有操作人
    public CommonResult<Boolean> updateDemoOrderRefunded(@RequestBody PayRefundNotifyReqDTO notifyReqDTO) {
        payDemoOrderService.updateDemoOrderRefunded(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
                notifyReqDTO.getPayRefundId());
        return success(true);
    }
    @PostMapping("/refundDemoOrder")
    @Operation(summary = "更新示例订单为已支付") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
    @PermitAll // 无需登录，安全由 PayDemoOrderService 内部校验实现
    @OperateLog(enable = false) // 禁用操作日志，因为没有操作人
    public CommonResult<Boolean> refundDemoOrder(@RequestParam("id") Long id) {
        payDemoOrderService.refundDemoOrder(id, ServletUtils.getClientIP());
        return success(true);
    }
}
