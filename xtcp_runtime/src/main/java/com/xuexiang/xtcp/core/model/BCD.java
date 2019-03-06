package com.xuexiang.xtcp.core.model;

import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.model.IProtocolItem;
import com.xuexiang.xtcp.utils.BCDUtils;

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

    /**
     * byte数据
     */
    private byte[] mData;
    /**
     * 数值
     */
    private Object mValue;
    /**
     * 数值的类型
     */
    private Type mType;
    /**
     * BCD编码的格式
     */
    private String mFormat;

    public BCD(Type type, String format) {
        mType = type;
        mFormat = format;
        int len = calculateLength(format);
        mData = new byte[len];
    }

    public BCD(Type type, T value, String format) {
        mType = type;
        mValue = value;
        mFormat = format;
        int len = calculateLength(format);
        mData = new byte[len];
    }

    /**
     * 设置值
     * @param value
     */
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

        if (int.class.equals(mType) || Integer.class.equals(mType)) {
            mData = BCDUtils.int2Bcd((Integer) mValue, mFormat);
        } else if (double.class.equals(mType) || Double.class.equals(mType) || float.class.equals(mType)
                || Float.class.equals(mType)) {
            mData = BCDUtils.double2Bcd((Double) mValue, mFormat);
        } else if (String.class.equals(mType)) {
            mData = BCDUtils.string2Bcd((String) mValue);
        } else if (Date.class.equals(mType)) {
            mData = BCDUtils.date2Bcd((Date) mValue, mFormat);
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
        if (bytes == null || mData == null) {
            return false;
        }

        if (bytes.length - index - tailLength < mData.length) { //剩余数据不够解析
            XTLog.d("[BCD] 剩余数据不够解析，直接退出！");
            return false;
        }

        System.arraycopy(bytes, index, mData, 0, mData.length);
        if (mType == null || mFormat == null || mData == null) {
            return false;
        }
        if (int.class.equals(mType) || Integer.class.equals(mType) || byte.class.equals(mType)
                || Byte.class.equals(mType) || short.class.equals(mType) || Short.class.equals(mType)
                || long.class.equals(mType) || Long.class.equals(mType)) {
            mValue = BCDUtils.bcd2Int(mData);
        } else if (double.class.equals(mType) || Double.class.equals(mType) || float.class.equals(mType)
                || Float.class.equals(mType)) {
            mValue = BCDUtils.bcd2Float(mData, mFormat);
        } else if (String.class.equals(mType)) {
            mValue = BCDUtils.bcd2String(mData);
        } else if (Date.class.equals(mType)) {
            mValue = BCDUtils.bcd2Date(mData, mFormat);
        }
        return true;
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
        return (T) mValue;
    }

    /**
     * @return 获取格式化的值
     */
    public String getFormatValue() {
        if (mType == null || mFormat == null || mValue == null) {
            return "";
        }
        if (String.class.equals(mType)) {
            return (String) mValue;
        } else if (Date.class.equals(mType)) {
            return BCDUtils.date2String((Date) mValue, mFormat);
        } else {
            return String.valueOf(mValue);
        }
    }

    public String getFormat() {
        return mFormat;
    }

    public Type getType() {
        return mType;
    }
}
