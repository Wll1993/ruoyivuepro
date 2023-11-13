package cn.iocoder.yudao.module.infra.controller.admin.demo12.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 学生创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InfraDemo12StudentCreateReqVO extends InfraDemo12StudentBaseVO {

}