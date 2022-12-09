package cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Schema(title = "管理后台 - 流程模型的创建 Request VO")
@Data
public class BpmModelCreateReqVO {

    @Schema(title = "流程标识", required = true, example = "process_yudao")
    @NotEmpty(message = "流程标识不能为空")
    private String key;

    @Schema(title = "流程名称", required = true, example = "芋道")
    @NotEmpty(message = "流程名称不能为空")
    private String name;

    @Schema(title = "流程描述", example = "我是描述")
    private String description;

}
