package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;

import java.nio.charset.Charset;

import static com.xuexiang.xtcp.core.XTCPConstants.MAX_ARRAY_LENGTH;

/**
 * String协议项<br>
 * <p>
 * mLength所占的byte位数为1，可表示的长度范围为【0～255】
 *
 * @author xuexiang
 * @since 2018/12/14 下午6:37
 */
public class StringField extends AbstractArrayItem {

    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = DEFAULT_ARRAY_LENGTH_SIZE)
    private int mLength;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private byte[] mData;

    /**
     * 获取String数组包装类
     *
     * @param data
     * @return
     */
    public static StringField wrap(@NonNull String data) {
        return new StringField(data);
    }

    /**
     * 获取String数组包装类
     *
     * @param data
     * @return
     */
    public static StringField wrap(@NonNull String data, String charset) {
        return new StringField(data, charset);
    }

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public StringField() {

    }

    public StringField(@NonNull String data) {
        setData(data);
    }

    public StringField(@NonNull String data, String charset) {
        setData(data, charset);
    }

    public StringField setData(@NonNull String data) {
        mData = data.getBytes();
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            XTLog.d("[StringField] 数组长度溢出，需要进行截取处理...");

            mData = new byte[MAX_ARRAY_LENGTH];
            System.arraycopy(data.getBytes(), 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    public StringField setData(@NonNull String data, String charset) {
        mData = data.getBytes(Charset.forName(charset));
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            XTLog.d("[StringField] 数组长度溢出，需要进行截取处理...");

            mData = new byte[MAX_ARRAY_LENGTH];
            System.arraycopy(data.getBytes(Charset.forName(charset)), 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    public String getData() {
        return new String(mData);
    }

    public String getData(String charset) {
        return new String(mData, Charset.forName(charset));
    }

    @Override
    public StringField setLength(int length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new byte[getLength()];
        if (bytes.length - index - tailLength < mLength) { //剩余数据不够解析
            XTLog.d("[StringField] 剩余数据不够解析，直接退出！");
            return false;
        }
        System.arraycopy(bytes, index, mData, 0, mData.length);
        return true;
    }

    @Override
    public String toString() {
        return "StringField{" +
                "mLength=" + mLength +
                ", mData=" + getData() +
                '}';
    }
}
