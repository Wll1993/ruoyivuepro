package cn.iocoder.yudao.module.trade.service.brokerage.user;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.trade.controller.admin.brokerage.user.vo.TradeBrokerageUserPageReqVO;
import cn.iocoder.yudao.module.trade.dal.dataobject.brokerage.user.TradeBrokerageUserDO;
import cn.iocoder.yudao.module.trade.dal.mysql.brokerage.user.TradeBrokerageUserMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TradeBrokerageUserServiceImpl} 的单元测试类
 *
 * @author owen
 */
@Import(TradeBrokerageUserServiceImpl.class)
public class TradeBrokerageUserServiceImplTest extends BaseDbUnitTest {

    @Resource
    private TradeBrokerageUserServiceImpl brokerageUserService;

    @Resource
    private TradeBrokerageUserMapper brokerageUserMapper;

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetBrokerageUserPage() {
        // mock 数据
        TradeBrokerageUserDO dbBrokerageUser = randomPojo(TradeBrokerageUserDO.class, o -> { // 等会查询到
            o.setBrokerageUserId(null);
            o.setBrokerageEnabled(null);
            o.setCreateTime(null);
        });
        brokerageUserMapper.insert(dbBrokerageUser);
        // 测试 brokerageUserId 不匹配
        brokerageUserMapper.insert(cloneIgnoreId(dbBrokerageUser, o -> o.setBrokerageUserId(null)));
        // 测试 brokerageEnabled 不匹配
        brokerageUserMapper.insert(cloneIgnoreId(dbBrokerageUser, o -> o.setBrokerageEnabled(null)));
        // 测试 createTime 不匹配
        brokerageUserMapper.insert(cloneIgnoreId(dbBrokerageUser, o -> o.setCreateTime(null)));
        // 准备参数
        TradeBrokerageUserPageReqVO reqVO = new TradeBrokerageUserPageReqVO();
        reqVO.setBrokerageUserId(null);
        reqVO.setBrokerageEnabled(null);
        reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

        // 调用
        PageResult<TradeBrokerageUserDO> pageResult = brokerageUserService.getBrokerageUserPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbBrokerageUser, pageResult.getList().get(0));
    }

}
