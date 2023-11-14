package cn.iocoder.yudao.module.crm.framework.core.annotations;

import cn.iocoder.yudao.module.crm.framework.enums.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.framework.enums.CrmPermissionLevelEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Crm 数据操作权限校验 AOP 注解
 *
 * @author HUIHUI
 */
@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrmPermission {

    /**
     * crm 类型
     */
    CrmBizTypeEnum bizType();

    /**
     * crm 类型扩展
     * 用于 CrmPermissionController 团队权限校验
     */
    String bizTypeValue() default "";

    /**
     * 数据编号，通过 spring el 表达式获取
     * TODO 数据权限完成后去除 default ""
     */
    String bizId() default "";

    /**
     * 操作所需权限级别
     */
    CrmPermissionLevelEnum level();

}
