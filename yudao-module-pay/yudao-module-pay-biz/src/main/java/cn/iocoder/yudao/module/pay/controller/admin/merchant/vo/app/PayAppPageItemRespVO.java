package cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(title = "管理后台 - 支付应用信息分页查询 Response VO", description = "相比于支付信息，还会多出应用渠道的开关信息")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayAppPageItemRespVO extends PayAppBaseVO {

    @Schema(title = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(title = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    /**
     * 所属商户
     */
    private PayMerchant payMerchant;

    @Schema(title = "商户")
    @Data
    public static class PayMerchant {

        @Schema(title = "商户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(title = "商户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发部")
        private String name;

    }

    @Schema(title = "渠道编码集合", requiredMode = Schema.RequiredMode.REQUIRED, example = "alipay_pc,alipay_wap...")
    private Set<String> channelCodes;


}
