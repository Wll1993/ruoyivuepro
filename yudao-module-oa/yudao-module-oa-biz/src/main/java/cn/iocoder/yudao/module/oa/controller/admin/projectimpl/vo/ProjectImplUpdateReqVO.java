package cn.iocoder.yudao.module.oa.controller.admin.projectimpl.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 工程实施列更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectImplUpdateReqVO extends ProjectImplBaseVO {

    @Schema(description = "id", required = true, example = "25477")
    @NotNull(message = "id不能为空")
    private Long id;

}
