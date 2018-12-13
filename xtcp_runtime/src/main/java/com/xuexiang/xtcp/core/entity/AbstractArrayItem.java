package com.xuexiang.xtcp.core.entity;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IArrayItem;

import java.lang.reflect.Field;

/**
 * 抽象的数组类
 *
 * @author xuexiang
 * @since 2018/12/13 上午11:28
 */
public abstract class AbstractArrayItem implements IArrayItem {

    @Override
    public int getProtocolLength() {
        return _XTCP.getIProtocolParser().getProtocolLength(this);
    }

    @Override
    public byte[] proto2byte(StorageMode storageMode) {
        return _XTCP.getIProtocolParser().protoBody2Byte(this, storageMode);
    }

    /**
     * 获取数组协议字段的长度
     *
     * @param maxLength     最大的长度
     * @return
     */
    protected int getArrayFieldLength(String fieldName, int maxLength) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            ProtocolField protocolField = field.getAnnotation(ProtocolField.class);
            return protocolField.length() > maxLength || protocolField.length() < 1 ? maxLength : protocolField.length();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return maxLength;
    }

}
