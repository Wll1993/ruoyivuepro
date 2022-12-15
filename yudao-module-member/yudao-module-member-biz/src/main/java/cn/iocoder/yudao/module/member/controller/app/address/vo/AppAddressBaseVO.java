package cn.iocoder.yudao.module.member.controller.app.address.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
* 用户收件地址 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class AppAddressBaseVO {

    @Schema(title = "收件人名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收件人名称不能为空")
    private String name;

    @Schema(title = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema(title = "地区编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "地区编号不能为空")
    private Long areaId;

    @Schema(title = "邮编", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "邮编不能为空")
    private String postCode;

    @Schema(title = "收件详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收件详细地址不能为空")
    private String detailAddress;

    @Schema(title = "是否默认地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认地址不能为空")
    private Boolean defaulted;

}
