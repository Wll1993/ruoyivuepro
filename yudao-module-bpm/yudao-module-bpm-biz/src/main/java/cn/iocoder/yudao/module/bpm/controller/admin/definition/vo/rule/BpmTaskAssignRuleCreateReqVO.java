package cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotEmpty;

@Schema(title = "管理后台 - 流程任务分配规则的创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BpmTaskAssignRuleCreateReqVO extends BpmTaskAssignRuleBaseVO {

    @Schema(title = "流程模型的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "流程模型的编号不能为空")
    private String modelId;

    @Schema(title = "流程任务定义的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotEmpty(message = "流程任务定义的编号不能为空")
    private String taskDefinitionKey;

}
