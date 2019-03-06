package com.xuexiang.xtcp.core.model;

import android.support.annotation.Nullable;

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
     * 数据是否足够解析
     *
     * @param bytes       数据集合[整个消息体数据，包含头和尾]
     * @param index       起始字节
     * @param tailLength  消息尾的长度[和index一起决定了数据解析的范围]
     * @param parseLength 需要解析的长度
     * @return
     */
    protected boolean isDataEnoughToParse(byte[] bytes, int index, int tailLength, int parseLength) {
        return bytes.length - index - tailLength < parseLength;
    }

    /**
     * 获取数组协议字段的长度
     *
     * @param fieldName 字段名
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

    /**
     * 获取数组协议字段的类型
     *
     * @param fieldName 字段名
     * @return
     */
    @Nullable
    protected Class<?> getArrayFieldType(String fieldName) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            String arrTypeStr = field.getType().toString();
            // 通过对象数组类型获取对象类型
            return Class.forName(arrTypeStr.substring(arrTypeStr.indexOf('[') + 2, arrTypeStr.length() - 1));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
