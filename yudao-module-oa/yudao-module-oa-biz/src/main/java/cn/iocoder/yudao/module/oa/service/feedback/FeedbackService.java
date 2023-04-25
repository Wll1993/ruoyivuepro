package cn.iocoder.yudao.module.oa.service.feedback;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.feedback.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.feedback.FeedbackDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 产品反馈 Service 接口
 *
 * @author 管理员
 */
public interface FeedbackService {

    /**
     * 创建产品反馈
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFeedback(@Valid FeedbackCreateReqVO createReqVO);

    /**
     * 更新产品反馈
     *
     * @param updateReqVO 更新信息
     */
    void updateFeedback(@Valid FeedbackUpdateReqVO updateReqVO);

    /**
     * 删除产品反馈
     *
     * @param id 编号
     */
    void deleteFeedback(Long id);

    /**
     * 获得产品反馈
     *
     * @param id 编号
     * @return 产品反馈
     */
    FeedbackDO getFeedback(Long id);

    /**
     * 获得产品反馈列表
     *
     * @param ids 编号
     * @return 产品反馈列表
     */
    List<FeedbackDO> getFeedbackList(Collection<Long> ids);

    /**
     * 获得产品反馈分页
     *
     * @param pageReqVO 分页查询
     * @return 产品反馈分页
     */
    PageResult<FeedbackDO> getFeedbackPage(FeedbackPageReqVO pageReqVO);

    /**
     * 获得产品反馈列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 产品反馈列表
     */
    List<FeedbackDO> getFeedbackList(FeedbackExportReqVO exportReqVO);

}
