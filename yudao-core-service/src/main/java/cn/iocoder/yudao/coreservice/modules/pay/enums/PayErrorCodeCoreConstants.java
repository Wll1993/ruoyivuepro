package cn.iocoder.yudao.coreservice.modules.pay.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Pay 错误码 Core 枚举类
 *
 * pay 系统，使用 1-007-000-000 段
 */
public interface PayErrorCodeCoreConstants {

    // ========== APP 模块 1-007-000-000 ==========
    ErrorCode PAY_APP_NOT_FOUND = new ErrorCode(1007000000, "App 不存在");
    ErrorCode PAY_APP_IS_DISABLE = new ErrorCode(1007000002, "App 已经被禁用");

    // ========== CHANNEL 模块 1-007-001-000 ==========
    ErrorCode PAY_CHANNEL_NOT_FOUND = new ErrorCode(1007001000, "支付渠道的配置不存在");
    ErrorCode PAY_CHANNEL_IS_DISABLE = new ErrorCode(1007001001, "支付渠道已经禁用");
    ErrorCode PAY_CHANNEL_CLIENT_NOT_FOUND = new ErrorCode(1007001002, "支付渠道的客户端不存在");

    // ========== ORDER 模块 1-007-002-000 ==========
    ErrorCode PAY_ORDER_NOT_FOUND = new ErrorCode(1007002000, "支付订单不存在");
    ErrorCode PAY_ORDER_STATUS_IS_NOT_WAITING = new ErrorCode(1007002001, "支付订单不处于待支付");
    ErrorCode PAY_ORDER_STATUS_IS_NOT_SUCCESS = new ErrorCode(1007002002, "支付订单不处于已支付");
    ErrorCode PAY_ORDER_ERROR_USER = new ErrorCode(1007002003, "支付订单用户不正确");
    // ========== ORDER 模块(拓展单) 1-007-003-000 ==========
    ErrorCode PAY_ORDER_EXTENSION_NOT_FOUND = new ErrorCode(1007003000, "支付交易拓展单不存在");
    ErrorCode PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING = new ErrorCode(1007003001, "支付交易拓展单不处于待支付");
    ErrorCode PAY_ORDER_EXTENSION_STATUS_IS_NOT_SUCCESS = new ErrorCode(1007003002, "支付订单不处于已支付");

    /**
     * ========== 支付商户信息 1-007-004-000 ==========
     */
    ErrorCode MERCHANT_NOT_EXISTS = new ErrorCode(1007004000, "支付商户信息不存在");


    /**
     * ========== 支付应用信息 1-007-005-000 ==========
     */
    ErrorCode APP_NOT_EXISTS = new ErrorCode(1007005000, "支付应用信息不存在");


    /**
     * ========== 支付渠道 1-007-006-000 ==========
     */
    ErrorCode CHANNEL_NOT_EXISTS = new ErrorCode(1007006000, "支付渠道不存在");
    ErrorCode CHANNEL_KEY_READ_ERROR = new ErrorCode(1007006002, "支付渠道秘钥文件读取失败");
    ErrorCode EXIST_SAME_CHANNEL_ERROR = new ErrorCode(1007006003, "已存在相同的渠道");
}
