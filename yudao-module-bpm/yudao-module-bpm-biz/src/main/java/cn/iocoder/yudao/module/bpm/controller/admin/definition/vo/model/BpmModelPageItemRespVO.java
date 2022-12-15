package cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(title = "管理后台 - 流程模型的分页的每一项 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BpmModelPageItemRespVO extends BpmModelBaseVO {

    @Schema(title = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String id;

    @Schema(title = "表单名字", example = "请假表单")
    private String formName;

    @Schema(title = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    /**
     * 最新部署的流程定义
     */
    private ProcessDefinition processDefinition;

    @Schema(title = "流程定义")
    @Data
    public static class ProcessDefinition {

        @Schema(title = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private String id;

        @Schema(title = "版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer version;

        @Schema(title = "部署时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime deploymentTime;

        @Schema(title = "中断状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 SuspensionState 枚举")
        private Integer suspensionState;

    }

}
