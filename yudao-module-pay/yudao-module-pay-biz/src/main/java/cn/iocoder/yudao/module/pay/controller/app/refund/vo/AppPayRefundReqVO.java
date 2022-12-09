package cn.iocoder.yudao.module.pay.controller.app.refund.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(title = "用户 APP - 退款订单 Req VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppPayRefundReqVO {

    @Schema(title = "支付订单编号自增", required = true, example = "10")
    @NotNull(message = "支付订单编号自增")
    private Long payOrderId;

    @Schema(title = "退款金额", required = true, example = "1")
    @NotNull(message = "退款金额")
    private Long amount;

    @Schema(title = "退款原因", required = true, example = "不喜欢")
    @NotEmpty(message = "退款原因")
    private String reason;

    @Schema(title = "商户退款订单号", required = true, example = "MR202111180000000001")
    //TODO 测试暂时模拟生成
    //@NotEmpty(message = "商户退款订单号")
    private String merchantRefundId;

}
