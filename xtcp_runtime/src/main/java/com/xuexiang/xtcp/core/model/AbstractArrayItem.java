package com.xuexiang.xtcp.core.model;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IArrayItem;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.lang.reflect.Field;

/**
 * 抽象的数组类[只是为了解决byte解析时，对应数组数据长度未知的问题]。所有的数组，只要是需要解析的，就必须要实现IArrayItem。
 *
 * @author xuexiang
 * @since 2018/12/13 上午11:28
 */
public abstract class AbstractArrayItem implements IArrayItem {

    /**
     * 数组数据的字段名
     */
    public static final String FIELD_NAME_DATA = "mData";

    @Override
    public int getProtocolLength() {
        return _XTCP.getIProtocolParser().getProtocolLength(this);
    }

    @Override
    public byte[] proto2byte(StorageMode storageMode) {
        return _XTCP.getIProtocolParser().protoBody2Byte(this, storageMode);
    }

    /**
     * 设置数组的长度
     *
     * @param length
     * @param <T>
     * @return
     */
    public abstract <T extends AbstractArrayItem> T setLength(int length);

    @Override
    public int fillArrayLength(byte[] bytes, int index, StorageMode storageMode) {
        setLength(ConvertUtils.bytesToInt(storageMode, bytes, index, DEFAULT_ARRAY_LENGTH_SIZE)); //拿到长度
        return DEFAULT_ARRAY_LENGTH_SIZE;
    }

    /**
     * 获取数组协议字段的长度
     *
     * @param maxLength 最大的长度
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
