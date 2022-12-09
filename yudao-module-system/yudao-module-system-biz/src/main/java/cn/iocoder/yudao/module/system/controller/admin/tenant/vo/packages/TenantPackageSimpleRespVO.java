package cn.iocoder.yudao.module.system.controller.admin.tenant.vo.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(title = "管理后台 - 租户套餐精简 Response VO")
@Data
public class TenantPackageSimpleRespVO {

    @Schema(title = "套餐编号", required = true, example = "1024")
    @NotNull(message = "套餐编号不能为空")
    private Long id;

    @Schema(title = "套餐名", required = true, example = "VIP")
    @NotNull(message = "套餐名不能为空")
    private String name;

}
