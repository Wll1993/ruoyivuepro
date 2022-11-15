package cn.iocoder.yudao.module.trade.dal.dataobject.aftersale;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.iocoder.yudao.module.trade.enums.aftersale.TradeAfterSaleStatusEnum;
import cn.iocoder.yudao.module.trade.enums.aftersale.TradeAfterSaleTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易售后，用于处理 {@link TradeOrderDO} 交易订单的退款退货流程
 *
 * @author 芋道源码
 */
@TableName(value = "trade_refund")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TradeAfterSaleDO extends BaseDO {

    /**
     * 售后编号，主键自增
     */
    private Long id;
    /**
     * 售后流水号
     *
     * 例如说，1146347329394184195
     */
    private String no;
    /**
     * 退款状态
     *
     * 枚举 {@link TradeAfterSaleStatusEnum}
     */
    private Integer status;
    /**
     * 售后类型
     *
     * 枚举 {@link TradeAfterSaleTypeEnum}
     */
    private Integer type;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 编号
     */
    private Long userId;
    /**
     * 申请原因
     *
     * 使用数据字典枚举，对应 trade_refund_apply_reason 类型
     */
    private Integer applyReason;
    /**
     * 补充描述
     */
    private String applyDescription;
    /**
     * 补充凭证图片
     *
     * 数组，以逗号分隔
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> applyPicUrls;

    // ========== 商家相关 ==========

    /**
     * 审批时间
     */
    private LocalDateTime auditTime;
    /**
     * 审批人
     *
     * 关联 AdminUserDO 的 id 编号
     */
    private Long auditUserId;
    /**
     * 审批备注
     */
    private String auditReason;

    // ========== 交易订单相关 ==========
    /**
     * 交易订单编号
     *
     * 关联 {@link TradeOrderDO#getId()}
     */
    private Long orderId;
    /**
     * 交易订单项编号
     *
     * 关联 {@link TradeOrderItemDO#getId()}
     */
    private Long orderItemId;
    /**
     * 商品 SPU 编号
     *
     * 关联 ProductSpuDO 的编号
     */
    private Long spuId;
    /**
     * 商品 SKU 编号
     *
     * 关联 ProductSkuDO 的编号
     */
    private Integer skuId;
    /**
     * 退货商品数量
     */
    private Integer count;

    // ========== 退款相关 ==========
    /**
     * 退款金额，单位：分。
     */
    private Integer refundPrice;
    /**
     * 支付退款编号 TODO
     *
     * 对接 pay-module-biz 支付服务的退款订单编号，即 PayRefundDO 的 id 编号
     */
    private Long payRefundId;
    // TODO 芋艿：看看是否有必要冗余，order_number、order_amount、flow_trade_no、out_refund_no、pay_type、return_money_sts、refund_time

    // ========== 退货相关 ==========
    /**
     * 退货物流公司编号 TODO
     *
     * 关联 ExpressDO 的 id 编号
     */
    private Long returnExpressId; // express_name
    /**
     * 退货物流单号 TODO
     */
    private String returnExpressNo; // express_no
    /**
     * 退货时间 TODO
     */
    private LocalDateTime deliveryTime; // ship_time
    /**
     * 收获备注 TODO
     */
    private String receiveMemo; // receive_message
    /**
     * 收货时间 TODO
     */
    private LocalDateTime receiveDate; // receive_time

}
