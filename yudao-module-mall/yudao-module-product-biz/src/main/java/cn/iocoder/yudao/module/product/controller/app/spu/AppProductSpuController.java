package cn.iocoder.yudao.module.product.controller.app.spu;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.product.controller.app.spu.vo.AppSpuPageReqVO;
import cn.iocoder.yudao.module.product.controller.app.spu.vo.AppSpuPageRespVO;
import cn.iocoder.yudao.module.product.controller.app.spu.vo.AppSpuRespVO;
import cn.iocoder.yudao.module.product.service.spu.ProductSpuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP -  商品spu")
@RestController
@RequestMapping("/product/spu")
@Validated
public class AppProductSpuController {

    @Resource
    private ProductSpuService spuService;

    @GetMapping("/page")
    @Operation(summary = "获得商品spu分页")
    public CommonResult<PageResult<AppSpuPageRespVO>> getSpuPage(@Valid AppSpuPageReqVO pageVO) {
        return success(spuService.getSpuPage(pageVO));
    }

    @GetMapping("/")
    @Operation(summary = "获取商品 - 通过商品id")
    public CommonResult<AppSpuRespVO> getSpu(@RequestParam("spuId") Long spuId) {
        AppSpuRespVO appSpuRespVO = BeanUtil.toBean(spuService.getSpu(spuId), AppSpuRespVO.class);
        return success(appSpuRespVO);
    }
}
