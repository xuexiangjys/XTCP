package com.xuexiang.xtcp.annotation;

import com.xuexiang.xtcp.enums.StorageMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 协议实体的注解
 *
 * @author xuexiang
 * @since 2018/12/10 下午4:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Protocol {

    /**
     * @return 协议的名称
     */
    String name() default "";

    /**
     * @return 协议命令码，协议命令的唯一号
     */
    byte opcode();

    /**
     * @return 协议响应码（结果码）
     */
    byte resCode() default -1;

    /**
     * @return 存储方式
     */
    StorageMode mode() default StorageMode.Default;


    /**
     * @return 描述信息
     */
    String desc() default "";



}
