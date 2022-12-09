package cn.iocoder.yudao.module.member.controller.app.user.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Schema(title = "用户 APP - 修改手机 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserUpdateMobileReqVO {

    @Schema(title = "手机验证码", required = true, example = "1024")
    @NotEmpty(message = "手机验证码不能为空")
    @Length(min = 4, max = 6, message = "手机验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "手机验证码必须都是数字")
    private String code;

    @Schema(title = "手机号",required = true,example = "15823654487")
    @NotBlank(message = "手机号不能为空")
    @Length(min = 8, max = 11, message = "手机号码长度为 8-11 位")
    @Mobile
    private String mobile;

    @Schema(title = "原手机验证码", required = true, example = "1024")
    @NotEmpty(message = "原手机验证码不能为空")
    @Length(min = 4, max = 6, message = "手机验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "手机验证码必须都是数字")
    private String oldCode;

    // TODO @芋艿：oldMobile 应该不用传递

    @Schema(title = "原手机号",required = true,example = "15823654487")
    @NotBlank(message = "手机号不能为空")
    @Length(min = 8, max = 11, message = "手机号码长度为 8-11 位")
    @Mobile
    private String oldMobile;

}
