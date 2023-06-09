package cn.iocoder.yudao.module.product.service.comment;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import cn.iocoder.yudao.module.product.controller.admin.comment.vo.ProductCommentPageReqVO;
import cn.iocoder.yudao.module.product.controller.admin.comment.vo.ProductCommentReplyVO;
import cn.iocoder.yudao.module.product.controller.admin.comment.vo.ProductCommentRespVO;
import cn.iocoder.yudao.module.product.controller.admin.comment.vo.ProductCommentUpdateVisibleReqVO;
import cn.iocoder.yudao.module.product.controller.app.comment.vo.AppCommentAdditionalReqVO;
import cn.iocoder.yudao.module.product.controller.app.comment.vo.AppCommentPageReqVO;
import cn.iocoder.yudao.module.product.controller.app.comment.vo.AppCommentRespVO;
import cn.iocoder.yudao.module.product.convert.comment.ProductCommentConvert;
import cn.iocoder.yudao.module.product.dal.dataobject.comment.ProductCommentDO;
import cn.iocoder.yudao.module.product.dal.mysql.comment.ProductCommentMapper;
import cn.iocoder.yudao.module.product.enums.comment.ProductCommentScoresEnum;
import cn.iocoder.yudao.module.product.service.spu.ProductSpuService;
import cn.iocoder.yudao.module.trade.api.order.TradeOrderApi;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * {@link ProductCommentServiceImpl} 的单元测试类
 *
 * @author wangzhs
 */
@Import(ProductCommentServiceImpl.class)
public class ProductCommentServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ProductCommentMapper productCommentMapper;

    @Resource
    @Lazy
    private ProductCommentServiceImpl productCommentService;

    @MockBean
    private TradeOrderApi tradeOrderApi;
    @MockBean
    private ProductSpuService productSpuService;

    public String generateNo() {
        return DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomInt(100000, 999999);
    }

    public Long generateId() {
        return RandomUtil.randomLong(100000, 999999);
    }

    @Test
    public void testCreateCommentAndGet_success() {
        // mock 测试
        ProductCommentDO productComment = randomPojo(ProductCommentDO.class);
        productCommentMapper.insert(productComment);

        // 断言
        // 校验记录的属性是否正确
        ProductCommentDO comment = productCommentMapper.selectById(productComment.getId());
        assertPojoEquals(productComment, comment);
    }

    @Test
    public void testGetCommentPage_success() {
        // 准备参数
        ProductCommentDO productComment = randomPojo(ProductCommentDO.class, o -> {
            o.setUserNickname("王二狗");
            o.setSpuName("感冒药");
            o.setScores(ProductCommentScoresEnum.FOUR.getScores());
            o.setReplied(Boolean.TRUE);
            o.setVisible(Boolean.TRUE);
            o.setId(generateId());
            o.setUserId(generateId());
            o.setAnonymous(Boolean.TRUE);
            o.setOrderId(generateId());
            o.setOrderItemId(generateId());
            o.setSpuId(generateId());
            o.setSkuId(generateId());
            o.setDescriptionScores(ProductCommentScoresEnum.FOUR.getScores());
            o.setBenefitScores(ProductCommentScoresEnum.FOUR.getScores());
            o.setDeliveryScores(ProductCommentScoresEnum.FOUR.getScores());
            o.setContent("真好吃");
            o.setReplyUserId(generateId());
            o.setReplyContent("确实");
            o.setReplyTime(LocalDateTime.now());
            o.setAdditionalTime(LocalDateTime.now());
            o.setCreateTime(LocalDateTime.now());
            o.setUpdateTime(LocalDateTime.now());
        });
        productCommentMapper.insert(productComment);

        Long orderId = productComment.getOrderId();
        Long spuId = productComment.getSpuId();

        // 测试 userNickname 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setUserNickname("王三").setScores(ProductCommentScoresEnum.ONE.getScores())));
        // 测试 orderId 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setOrderId(generateId())));
        // 测试 spuId 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setSpuId(generateId())));
        // 测试 spuName 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setSpuName("感康")));
        // 测试 scores 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setScores(ProductCommentScoresEnum.ONE.getScores())));
        // 测试 replied 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setReplied(Boolean.FALSE)));
        // 测试 visible 不匹配
        productCommentMapper.insert(cloneIgnoreId(productComment, o -> o.setVisible(Boolean.FALSE)));

        // 调用
        ProductCommentPageReqVO productCommentPageReqVO = new ProductCommentPageReqVO();
        productCommentPageReqVO.setUserNickname("王二");
        productCommentPageReqVO.setOrderId(orderId);
        productCommentPageReqVO.setSpuId(spuId);
        productCommentPageReqVO.setSpuName("感冒药");
        productCommentPageReqVO.setScores(ProductCommentScoresEnum.FOUR.getScores());
        productCommentPageReqVO.setReplied(Boolean.TRUE);

        PageResult<ProductCommentDO> commentPage = productCommentService.getCommentPage(productCommentPageReqVO);
        PageResult<ProductCommentRespVO> result = ProductCommentConvert.INSTANCE.convertPage(productCommentMapper.selectPage(productCommentPageReqVO));
        assertEquals(result.getTotal(), commentPage.getTotal());

        PageResult<ProductCommentDO> all = productCommentService.getCommentPage(new ProductCommentPageReqVO());
        assertEquals(8, all.getTotal());

        // 测试获取所有商品分页评论数据
        PageResult<AppCommentRespVO> result1 = productCommentService.getCommentPage(new AppCommentPageReqVO(), Boolean.TRUE);
        assertEquals(7, result1.getTotal());

        // 测试获取所有商品分页中评数据
        PageResult<AppCommentRespVO> result2 = productCommentService.getCommentPage(new AppCommentPageReqVO().setType(AppCommentPageReqVO.MEDIOCRE_COMMENT), Boolean.TRUE);
        assertEquals(2, result2.getTotal());

        // 测试获取指定 spuId 商品分页中评数据
        PageResult<AppCommentRespVO> result3 = productCommentService.getCommentPage(new AppCommentPageReqVO().setSpuId(spuId).setType(AppCommentPageReqVO.MEDIOCRE_COMMENT), Boolean.TRUE);
        assertEquals(2, result3.getTotal());

        // 测试分页 tab count
        Map<String, Long> tabsCount = productCommentService.getCommentPageTabsCount(spuId, Boolean.TRUE);
        assertEquals(6, tabsCount.get(AppCommentPageReqVO.ALL_COUNT));
        assertEquals(4, tabsCount.get(AppCommentPageReqVO.FAVOURABLE_COMMENT_COUNT));
        assertEquals(2, tabsCount.get(AppCommentPageReqVO.MEDIOCRE_COMMENT_COUNT));
        assertEquals(0, tabsCount.get(AppCommentPageReqVO.NEGATIVE_COMMENT_COUNT));

    }

    @Test
    public void testUpdateCommentVisible_success() {
        // mock 测试
        ProductCommentDO productComment = randomPojo(ProductCommentDO.class, o -> {
            o.setVisible(Boolean.TRUE);
        });
        productCommentMapper.insert(productComment);

        Long productCommentId = productComment.getId();

        ProductCommentUpdateVisibleReqVO updateReqVO = new ProductCommentUpdateVisibleReqVO();
        updateReqVO.setId(productCommentId);
        updateReqVO.setVisible(Boolean.FALSE);
        productCommentService.updateCommentVisible(updateReqVO);

        ProductCommentDO productCommentDO = productCommentMapper.selectById(productCommentId);
        assertFalse(productCommentDO.getVisible());
    }


    @Test
    public void testCommentReply_success() {
        // mock 测试
        ProductCommentDO productComment = randomPojo(ProductCommentDO.class);
        productCommentMapper.insert(productComment);

        Long productCommentId = productComment.getId();

        ProductCommentReplyVO replyVO = new ProductCommentReplyVO();
        replyVO.setId(productCommentId);
        replyVO.setReplyContent("测试");
        productCommentService.commentReply(replyVO, 1L);

        ProductCommentDO productCommentDO = productCommentMapper.selectById(productCommentId);
        assertEquals("测试", productCommentDO.getReplyContent());
    }

    @Test
    public void testCreateComment_success() {
        // mock 测试
        ProductCommentDO productComment = randomPojo(ProductCommentDO.class, o -> {
            o.setAdditionalContent("");
        });

        productCommentService.createComment(productComment, Boolean.TRUE);

        MemberUserRespDTO user = new MemberUserRespDTO();
        user.setId(productComment.getUserId());

        AppCommentAdditionalReqVO createReqVO = new AppCommentAdditionalReqVO();
        createReqVO.setId(productComment.getId());
        createReqVO.setAdditionalContent("追加");
        createReqVO.setAdditionalPicUrls(productComment.getAdditionalPicUrls());

        productCommentService.additionalComment(user, createReqVO);
        ProductCommentDO exist = productCommentMapper.selectById(productComment.getId());

        assertEquals("追加", exist.getAdditionalContent());
    }

}
