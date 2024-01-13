package cn.iocoder.yudao.module.crm.dal.dataobject.followup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.contact.CrmContactDO;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// TODO @puhui999：界面：做成一个 list 列表，字段是 id、跟进人、跟进方式、跟进时间、跟进内容、下次联系时间、关联联系人、关联商机
// TODO @puhui999：界面：记录时，弹窗，表单字段是跟进方式、跟进内容、下次联系时间、关联联系人、关联商机；其中关联联系人、关联商机，要做成对应的组件列。
/**
 * 跟进记录 DO
 *
 * 用于记录客户、联系人的每一次跟进
 *
 * @author 芋道源码
 */
@TableName(value = "crm_follow_up_record", autoResultMap = true)
@KeySequence("crm_follow_up_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmFollowUpRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 数据类型
     *
     * 枚举 {@link CrmBizTypeEnum}
     */
    private Integer bizType;
    /**
     * 数据编号
     *
     * 关联 {@link CrmBizTypeEnum} 对应模块 DO 的 id 字段
     */
    private Long bizId;

    /**
     * 跟进类型
     *
     * TODO @puhui999：可以搞个数据字典，打电话、发短信、上门拜访、微信、邮箱、QQ
     */
    private Integer type;
    /**
     * 跟进内容
     */
    private String content;
    /**
     * 下次联系时间
     */
    private LocalDateTime nextTime;

    /**
     * 关联的商机编号数组
     *
     * 关联 {@link CrmBusinessDO#getId()}
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> businessIds;
    /**
     * 关联的联系人编号数组
     *
     * 关联 {@link CrmContactDO#getId()}
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> contactIds;

}
