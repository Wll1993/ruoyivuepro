package cn.iocoder.yudao.module.system.controller.admin.sensitiveword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Schema(title = "管理后台 - 敏感词更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SensitiveWordUpdateReqVO extends SensitiveWordBaseVO {

    @Schema(title = "编号", required = true, example = "1")
    @NotNull(message = "编号不能为空")
    private Long id;

}
