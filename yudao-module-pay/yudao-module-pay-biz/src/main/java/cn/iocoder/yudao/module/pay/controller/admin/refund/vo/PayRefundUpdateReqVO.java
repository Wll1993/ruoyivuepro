package cn.iocoder.yudao.module.pay.controller.admin.refund.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(title = "管理后台 - 退款订单更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayRefundUpdateReqVO extends PayRefundBaseVO {

    @Schema(title = "支付退款编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付退款编号不能为空")
    private Long id;

}
