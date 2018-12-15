package com.xuexiang.xtcp.model;

import java.lang.reflect.Field;

/**
 * 存储协议的字段名信息
 *
 * @author xuexiang
 * @since 2018/12/12 下午3:52
 */
public final class ProtocolFieldInfo {

    /**
     * 存储使用@ProtocolField注解的字段信息
     */
    private Field[] mFields;

    public ProtocolFieldInfo(String className, String fields) {
        try {
            Class<?> cls = Class.forName(className);
            String[] fieldNames = fields.split(",");
            if (fieldNames.length > 0) {
                mFields = new Field[fieldNames.length];
                for (int i = 0; i < fieldNames.length; i++) {
                    mFields[i] = cls.getDeclaredField(fieldNames[i]);
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Field[] getFields() {
        return mFields;
    }

}
