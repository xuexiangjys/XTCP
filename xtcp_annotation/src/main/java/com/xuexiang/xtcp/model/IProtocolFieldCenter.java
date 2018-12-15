package com.xuexiang.xtcp.model;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 协议字段管理中心
 *
 * @author xuexiang
 * @since 2018/12/12 下午4:19
 */
public interface IProtocolFieldCenter {

    /**
     * 根据类名获取协议字段名集合
     *
     * @param className
     * @return
     */
    Field[] getProtocolFields(final String className);

    /**
     * @return 获取 实体类 -> 协议字段名集合 的映射
     */
    Map<String, ProtocolFieldInfo> getClass2Fields();

}
