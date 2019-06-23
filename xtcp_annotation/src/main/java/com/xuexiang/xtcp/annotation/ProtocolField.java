package com.xuexiang.xtcp.annotation;

import com.xuexiang.xtcp.enums.StorageMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 协议字段的注解
 *
 * @author xuexiang
 * @since 2018/12/10 下午3:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProtocolField {

    /**
     * @return 字段的顺序索引
     */
    int index();

    /**
     * @return 是否为协议解析字段
     */
    boolean isField() default true;

    /**
     * @return 协议字段的长度, 不设置的话，默认自动识别
     */
    int length() default -1;

    /**
     * @return 是否是无符号数，默认是true
     */
    boolean unsigned() default true;

    /**
     * @return 存储方式
     */
    StorageMode mode() default StorageMode.Default;

    /**
     * @return 字符集（只对String有效）
     */
    String charset() default "UTF-8";

}
