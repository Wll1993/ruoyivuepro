package cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

/**
* 用户组 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class BpmUserGroupBaseVO {

    @Schema(title = "组名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    @NotNull(message = "组名不能为空")
    private String name;

    @Schema(title = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道源码")
    @NotNull(message = "描述不能为空")
    private String description;

    @Schema(title = "成员编号数组", requiredMode = Schema.RequiredMode.REQUIRED, example = "1,2,3")
    @NotNull(message = "成员编号数组不能为空")
    private Set<Long> memberUserIds;

    @Schema(title = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

}
