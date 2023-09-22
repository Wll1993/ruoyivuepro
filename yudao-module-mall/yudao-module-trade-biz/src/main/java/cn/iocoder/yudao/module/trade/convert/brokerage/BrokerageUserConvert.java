package cn.iocoder.yudao.module.trade.convert.brokerage;

import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import cn.iocoder.yudao.module.trade.controller.admin.brokerage.vo.user.BrokerageUserRespVO;
import cn.iocoder.yudao.module.trade.controller.app.brokerage.vo.user.AppBrokerageUserRankByUserCountRespVO;
import cn.iocoder.yudao.module.trade.dal.dataobject.brokerage.BrokerageUserDO;
import cn.iocoder.yudao.module.trade.service.brokerage.bo.UserBrokerageSummaryBO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 分销用户 Convert
 *
 * @author owen
 */
@Mapper
public interface BrokerageUserConvert {

    BrokerageUserConvert INSTANCE = Mappers.getMapper(BrokerageUserConvert.class);

    BrokerageUserRespVO convert(BrokerageUserDO bean);

    List<BrokerageUserRespVO> convertList(List<BrokerageUserDO> list);

    PageResult<BrokerageUserRespVO> convertPage(PageResult<BrokerageUserDO> page);

    default PageResult<BrokerageUserRespVO> convertPage(PageResult<BrokerageUserDO> pageResult,
                                                        Map<Long, MemberUserRespDTO> userMap,
                                                        Map<Long, Long> brokerageUserCountMap,
                                                        Map<Long, UserBrokerageSummaryBO> userOrderSummaryMap) {
        PageResult<BrokerageUserRespVO> result = convertPage(pageResult);
        for (BrokerageUserRespVO userVO : result.getList()) {
            // 用户信息
            copyTo(userMap.get(userVO.getId()), userVO);

            // 推广用户数量
            userVO.setBrokerageUserCount(MapUtil.getInt(brokerageUserCountMap, userVO.getId(), 0));
            // 推广订单数量、推广订单金额
            Optional<UserBrokerageSummaryBO> orderSummaryOptional = Optional.ofNullable(userOrderSummaryMap.get(userVO.getId()));
            userVO.setBrokerageOrderCount(orderSummaryOptional.map(UserBrokerageSummaryBO::getCount).orElse(0))
                    .setBrokerageOrderPrice(orderSummaryOptional.map(UserBrokerageSummaryBO::getPrice).orElse(0));
            // todo 已提现次数、已提现金额
            userVO.setWithdrawCount(0).setWithdrawPrice(0);
        }
        return result;
    }

    default BrokerageUserRespVO copyTo(MemberUserRespDTO source, BrokerageUserRespVO target) {
        Optional.ofNullable(source).ifPresent(
                user -> target.setNickname(user.getNickname()).setAvatar(user.getAvatar()));
        return target;
    }

    default PageResult<AppBrokerageUserRankByUserCountRespVO> convertPage03(PageResult<AppBrokerageUserRankByUserCountRespVO> pageResult, Map<Long, MemberUserRespDTO> userMap) {
        for (AppBrokerageUserRankByUserCountRespVO vo : pageResult.getList()) {
            copyTo(userMap.get(vo.getId()), vo);
        }
        return pageResult;
    }

    void copyTo(MemberUserRespDTO from, @MappingTarget AppBrokerageUserRankByUserCountRespVO to);
}
