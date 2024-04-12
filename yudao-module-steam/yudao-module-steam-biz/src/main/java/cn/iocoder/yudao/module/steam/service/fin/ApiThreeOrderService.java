package cn.iocoder.yudao.module.steam.service.fin;

import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.module.steam.controller.app.vo.OpenApiReqVo;
import cn.iocoder.yudao.module.steam.controller.app.vo.order.OrderCancelVo;
import cn.iocoder.yudao.module.steam.controller.app.vo.order.OrderInfoResp;
import cn.iocoder.yudao.module.steam.controller.app.vo.order.QueryOrderReqVo;
import cn.iocoder.yudao.module.steam.dal.dataobject.apiorder.ApiOrderDO;
import cn.iocoder.yudao.module.steam.dal.dataobject.youyouorder.YouyouOrderDO;
import cn.iocoder.yudao.module.steam.enums.PlatCodeEnum;
import cn.iocoder.yudao.module.steam.service.fin.vo.*;
import cn.iocoder.yudao.module.steam.service.uu.vo.CreateCommodityOrderReqVo;
import cn.iocoder.yudao.module.steam.service.uu.vo.notify.NotifyReq;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.Valid;

/**
 * 第三方订单系统
 *
 * @author 芋道源码
 */
public interface ApiThreeOrderService {
    default PlatCodeEnum getPlatCode(){
        return PlatCodeEnum.UNSUPPORTED;
    }
    /**
     * 商品价格查询
     *
     * @param loginUser   用户
     * @param createReqVO 创建信息
     * @return 编号
     */
    ApiCommodityRespVo query(LoginUser loginUser, @Valid ApiQueryCommodityReqVo createReqVO);

    /**
     * 商品下单
     * 下单并回写steam_api_order_ext
     * 失败时代码参考  OpenApiCode
     * @param loginUser 前端用户
     * @param createReqVO 购买信息
     */
    ApiBuyItemRespVo buyItem(LoginUser loginUser, @Valid ApiQueryCommodityReqVo createReqVO,Long orderId);
    /**
     * 查询订单状态
     * 主要用于更新 steam_api_order_ext，时自动更新相应状态
     * @param loginUser 前端用户
     * @param orderNo 第三方 订单号
     * @return 返回json格式
     */
    String queryOrderDetail(LoginUser loginUser, @Valid String orderNo,Long orderId);
    /**
     * 查询商品信息
     * @param loginUser 订单用户
     * @param orderNo  第三方 订单号
     * @param orderId  主订单ID
     * @return 买家的订单
     */
    String queryCommodityDetail(LoginUser loginUser, String orderNo,Long orderId);

    /**
     * 查询订单简状态
     * @param orderNo  第三方 订单号
     * @param orderId  主订单ID
     * @return  1,进行中，2完成，3作废
     */
    Integer getOrderSimpleStatus(LoginUser loginUser, String orderNo,Long orderId);
    /**
     * 订单取消
     * @param orderNo  第三方 订单号
     * @param orderId  主订单ID
     */
    ApiOrderCancelRespVo orderCancel(LoginUser loginUser, String orderNo, Long orderId);
    /**
     * 释放库存
     * 第三方平台不需要
     * @param orderNo  第三方 订单号
     * @param orderId  主订单ID
     */
   default ApiOrderCancelRespVo releaseIvn(LoginUser loginUser, String orderNo, Long orderId){
       return new ApiOrderCancelRespVo().setIsSuccess(true).setErrorCode(null);
   }
    /**
     * 释放库存
     * 第三方平台不需要
     * @param jsonData  第三方 订单号
     * @param msgNo  主订单ID
     * 返回主订单号
     */
    ApiProcessNotifyResp processNotify(String jsonData, String msgNo);
}
