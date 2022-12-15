package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(title = "管理后台 - 流程任务的更新负责人的 Request VO")
@Data
public class BpmTaskUpdateAssigneeReqVO {

    @Schema(title = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "任务编号不能为空")
    private String id;

    @Schema(title = "新审批人的用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "新审批人的用户编号不能为空")
    private Long assigneeUserId;

}
