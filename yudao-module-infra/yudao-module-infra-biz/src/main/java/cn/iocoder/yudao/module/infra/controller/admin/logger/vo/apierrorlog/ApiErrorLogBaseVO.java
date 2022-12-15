package cn.iocoder.yudao.module.infra.controller.admin.logger.vo.apierrorlog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* API 错误日志 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ApiErrorLogBaseVO {

    @Schema(title = "链路追踪编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @NotNull(message = "链路追踪编号不能为空")
    private String traceId;

    @Schema(title = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @NotNull(message = "用户编号不能为空")
    private Integer userId;

    @Schema(title = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(title = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @NotNull(message = "应用名不能为空")
    private String applicationName;

    @Schema(title = "请求方法名", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotNull(message = "请求方法名不能为空")
    private String requestMethod;

    @Schema(title = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xx/yy")
    @NotNull(message = "请求地址不能为空")
    private String requestUrl;

    @Schema(title = "请求参数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请求参数不能为空")
    private String requestParams;

    @Schema(title = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @NotNull(message = "用户 IP不能为空")
    private String userIp;

    @Schema(title = "浏览器 UA", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @NotNull(message = "浏览器 UA不能为空")
    private String userAgent;

    @Schema(title = "异常发生时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常发生时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime exceptionTime;

    @Schema(title = "异常名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常名不能为空")
    private String exceptionName;

    @Schema(title = "异常导致的消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常导致的消息不能为空")
    private String exceptionMessage;

    @Schema(title = "异常导致的根消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常导致的根消息不能为空")
    private String exceptionRootCauseMessage;

    @Schema(title = "异常的栈轨迹", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常的栈轨迹不能为空")
    private String exceptionStackTrace;

    @Schema(title = "异常发生的类全名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常发生的类全名不能为空")
    private String exceptionClassName;

    @Schema(title = "异常发生的类文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常发生的类文件不能为空")
    private String exceptionFileName;

    @Schema(title = "异常发生的方法名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常发生的方法名不能为空")
    private String exceptionMethodName;

    @Schema(title = "异常发生的方法所在行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "异常发生的方法所在行不能为空")
    private Integer exceptionLineNumber;

    @Schema(title = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "处理状态不能为空")
    private Integer processStatus;

}
