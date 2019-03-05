package com.xuexiang.xtcp.core.model;

import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolItem;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * BCD编码
 * <p>
 * new BCD(Date.class, "yy-MM-dd HH-mm");
 * new BCD(Date.class, "yyyy-MM-dd HH:mm:ss")
 * new BCD(Date.class, "yyMMddHHmmss");
 * new BCD(int.class, "XXXX");
 * new BCD(float.class, "xxxx.xx");
 * new BCD(float.class, "xxxxx.x");
 *
 * @author xuexiang
 * @since 2019/3/5 下午11:46
 */
public class BCD<T> implements IProtocolItem {

    private byte[] mData;
    private T mValue;
    private String mFormat;

    private Type mType;

    public BCD(Type type, String format) {
        mType = type;
        mFormat = format;
        int len = calculateLength(format);
        mData = new byte[len];
    }

    public void setValue(T value) {
        mValue = value;
    }

    /**
     * @return 获取协议项的数据长度
     */
    @Override
    public int getProtocolLength() {
        return mData != null ? mData.length : 0;
    }

    /**
     * 将协议实体转化为byte数组
     *
     * @param storageMode 存储形式
     * @return
     */
    @Override
    public byte[] proto2byte(StorageMode storageMode) {
        if (mType == null || mFormat == null || mValue == null || mData == null) {
            return null;
        }

        if ((int.class.equals(mType)) || (Integer.class.equals(mType))) {
            mData = TBCDUtil.int2bcd((Integer) value, mFormat);
        } else if ((double.class.equals(mType)) || (Double.class.equals(mType)) || (float.class.equals(mType))
                || (Float.class.equals(mType))) {
            mData = TBCDUtil.double2bcd((Double) value, mFormat);
        } else if (String.class.equals(mType)) {
            mData = TBCDUtil.str2bcd((String) value);
        } else if (Date.class.equals(mType)) {
            mData = TBCDUtil.date2bcd((Date) value, mFormat);
        }
        return mData;
    }

    /**
     * 将byte数组数据转化为协议实体
     *
     * @param bytes       byte数组数据
     * @param index       起始字节
     * @param tailLength  消息尾的长度[和index一起决定了数据解析的范围]
     * @param storageMode 存储形式
     * @return 是否解析成功
     */
    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        return false;
    }

    /**
     * 根据格式计算长度
     *
     * @param format
     * @return
     */
    private int calculateLength(String format) {
        return (format.replaceAll("[^a-zA-Z0-9]*", "").length() + 1) / 2;
    }

    //=======get========//

    public byte[] getData() {
        return mData;
    }

    public T getValue() {
        return mValue;
    }

    public String getFormat() {
        return mFormat;
    }

    public Type getType() {
        return mType;
    }
}
