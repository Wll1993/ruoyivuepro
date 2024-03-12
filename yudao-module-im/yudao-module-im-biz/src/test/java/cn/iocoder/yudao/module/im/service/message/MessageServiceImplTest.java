package cn.iocoder.yudao.module.im.service.message;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.annotation.Resource;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;

import cn.iocoder.yudao.module.im.controller.admin.message.vo.*;
import cn.iocoder.yudao.module.im.dal.dataobject.message.MessageDO;
import cn.iocoder.yudao.module.im.dal.mysql.message.MessageMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Import;
import java.util.*;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.*;
import static cn.iocoder.yudao.module.im.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.*;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.*;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link MessageServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MessageServiceImpl.class)
public class MessageServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MessageServiceImpl messageService;

    @Resource
    private MessageMapper messageMapper;

    @Test
    public void testCreateMessage_success() {
        // 准备参数
        MessageSaveReqVO createReqVO = randomPojo(MessageSaveReqVO.class).setId(null);

        // 调用
        Long messageId = messageService.createMessage(createReqVO);
        // 断言
        assertNotNull(messageId);
        // 校验记录的属性是否正确
        MessageDO message = messageMapper.selectById(messageId);
        assertPojoEquals(createReqVO, message, "id");
    }

    @Test
    public void testUpdateMessage_success() {
        // mock 数据
        MessageDO dbMessage = randomPojo(MessageDO.class);
        messageMapper.insert(dbMessage);// @Sql: 先插入出一条存在的数据
        // 准备参数
        MessageSaveReqVO updateReqVO = randomPojo(MessageSaveReqVO.class, o -> {
            o.setId(dbMessage.getId()); // 设置更新的 ID
        });

        // 调用
        messageService.updateMessage(updateReqVO);
        // 校验是否更新正确
        MessageDO message = messageMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, message);
    }

    @Test
    public void testUpdateMessage_notExists() {
        // 准备参数
        MessageSaveReqVO updateReqVO = randomPojo(MessageSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> messageService.updateMessage(updateReqVO), MESSAGE_NOT_EXISTS);
    }

    @Test
    public void testDeleteMessage_success() {
        // mock 数据
        MessageDO dbMessage = randomPojo(MessageDO.class);
        messageMapper.insert(dbMessage);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbMessage.getId();

        // 调用
        messageService.deleteMessage(id);
       // 校验数据不存在了
       assertNull(messageMapper.selectById(id));
    }

    @Test
    public void testDeleteMessage_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> messageService.deleteMessage(id), MESSAGE_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetMessagePage() {
       // mock 数据
       MessageDO dbMessage = randomPojo(MessageDO.class, o -> { // 等会查询到
           o.setClientMessageId(null);
           o.setSenderId(null);
           o.setReceiverId(null);
           o.setSenderNickname(null);
           o.setSenderAvatar(null);
           o.setConversationType(null);
           o.setConversationNo(null);
           o.setContentType(null);
           o.setContent(null);
           o.setSendTime(null);
           o.setSendFrom(null);
           o.setCreateTime(null);
       });
       messageMapper.insert(dbMessage);
       // 测试 clientMessageId 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setClientMessageId(null)));
       // 测试 senderId 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setSenderId(null)));
       // 测试 receiverId 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setReceiverId(null)));
       // 测试 senderNickname 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setSenderNickname(null)));
       // 测试 senderAvatar 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setSenderAvatar(null)));
       // 测试 conversationType 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setConversationType(null)));
       // 测试 conversationNo 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setConversationNo(null)));
       // 测试 contentType 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setContentType(null)));
       // 测试 content 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setContent(null)));
       // 测试 sendTime 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setSendTime(null)));
       // 测试 sendFrom 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setSendFrom(null)));
       // 测试 createTime 不匹配
       messageMapper.insert(cloneIgnoreId(dbMessage, o -> o.setCreateTime(null)));
       // 准备参数
       MessagePageReqVO reqVO = new MessagePageReqVO();
       reqVO.setClientMessageId(null);
       reqVO.setSenderId(null);
       reqVO.setReceiverId(null);
       reqVO.setSenderNickname(null);
       reqVO.setSenderAvatar(null);
       reqVO.setConversationType(null);
       reqVO.setConversationNo(null);
       reqVO.setContentType(null);
       reqVO.setContent(null);
       reqVO.setSendTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setSendFrom(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<MessageDO> pageResult = messageService.getMessagePage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbMessage, pageResult.getList().get(0));
    }

}