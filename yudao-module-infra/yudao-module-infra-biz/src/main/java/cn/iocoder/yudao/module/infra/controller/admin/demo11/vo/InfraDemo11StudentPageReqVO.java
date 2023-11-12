package cn.iocoder.yudao.module.infra.controller.admin.demo11.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 学生分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InfraDemo11StudentPageReqVO extends PageParam {

    @Schema(description = "名字", example = "芋头")
    private String name;

    @Schema(description = "出生日期")
    private LocalDateTime birthday;

    @Schema(description = "性别", example = "1")
    private Integer sex;

    @Schema(description = "是否有效", example = "true")
    private Boolean enabled;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}