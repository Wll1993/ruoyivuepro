package cn.iocoder.yudao.module.system.controller.admin.logger.vo.loginlog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(title = "管理后台 - 登录日志 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoginLogRespVO extends LoginLogBaseVO {

    @Schema(title = "日志编号", required = true, example = "1024")
    private Long id;

    @Schema(title = "用户编号", example = "666")
    private Long userId;

    @Schema(title = "用户类型", required = true, example = "2", description = "参见 UserTypeEnum 枚举")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(title = "登录时间", required = true)
    private LocalDateTime createTime;

}
