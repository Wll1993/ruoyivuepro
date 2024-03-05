package cn.iocoder.yudao.module.steam.dal.dataobject.selexterior;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 外观选择 DO
 *
 * @author 芋道源码
 */
@TableName("steam_sel_exterior")
@KeySequence("steam_sel_exterior_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelExteriorDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 名字
     */
    private String internalName;
    /**
     * 中文名称
     */
    private String localizedTagName;
    /**
     * 字体颜色
     */
    private String color;

}