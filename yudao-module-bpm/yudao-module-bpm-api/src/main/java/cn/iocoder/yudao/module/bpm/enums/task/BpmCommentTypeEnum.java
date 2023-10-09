package cn.iocoder.yudao.module.bpm.enums.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程任务 -- comment类型枚举
 */
@Getter
@AllArgsConstructor
public enum BpmCommentTypeEnum {

    APPROVE(1, "通过"),
    REJECT(2, "不通过"),
    CANCEL(3, "已取消"),

    // ========== 流程任务独有的状态 ==========

    BACK(4, "退回"), // 退回
    DELEGATE(5, "委派"),
    ADD_SIGN(6, "加签"),
    SUB_SIGN(7,"减签"),
    ;

    /**
     * 结果
     */
    private final Integer result;
    /**
     * 描述
     */
    private final String desc;
}
