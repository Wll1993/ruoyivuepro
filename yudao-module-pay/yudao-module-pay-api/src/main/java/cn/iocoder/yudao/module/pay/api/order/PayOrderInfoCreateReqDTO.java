package cn.iocoder.yudao.module.pay.api.order;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

// TODO @LeeYan9: 1) 新建一个 dto 包, 然后挪进去哈; 2) 在 order下; Info 可以去掉;
/**
 * 支付单创建 Request DTO
 *
 * @author LeeYan9
 */
@Data
public class PayOrderInfoCreateReqDTO implements Serializable {

    /**
     * 应用编号
     */
    @NotNull(message = "应用编号不能为空")
    private Long appId;
    /**
     * 用户 IP
     */
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;

    // ========== 商户相关字段 ==========

    /**
     * 商户订单编号
     */
    @NotEmpty(message = "商户订单编号不能为空")
    private String merchantOrderId;
    /**
     * 商品标题
     */
    @NotEmpty(message = "商品标题不能为空")
    @Length(max = 32, message = "商品标题不能超过 32")
    private String subject;
    /**
     * 商品描述
     */
//    @NotEmpty(message = "商品描述信息不能为空") // 允许空
    @Length(max = 128, message = "商品描述信息长度不能超过128")
    private String body;

    // ========== 订单相关字段 ==========

    /**
     * 支付金额，单位：分
     */
    @NotNull(message = "支付金额不能为空")
    @Min(value = 1, message = "支付金额必须大于零")
    private Integer amount;

    /**
     * 支付过期时间
     */
    @NotNull(message = "支付过期时间不能为空")
    private LocalDateTime expireTime;

}
