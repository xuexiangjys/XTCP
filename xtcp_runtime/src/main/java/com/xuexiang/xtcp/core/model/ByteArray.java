package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.utils.ConvertUtils;

import static com.xuexiang.xtcp.core.Constants.MAX_ARRAY_LENGTH;

/**
 * byte数组协议项 <br>
 * <p>
 * mLength所占的byte位数为1，可表示的长度范围为【0～255】
 *
 * @author xuexiang
 * @since 2018/12/13 下午11:47
 */
public class ByteArray extends AbstractArrayItem {

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
     * 获取Byte数组包装类
     *
     * @param data
     * @return
     */
    public static ByteArray wrap(@NonNull byte[] data) {
        return new ByteArray(data);
    }

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public ByteArray() {

    }

    public ByteArray(@NonNull byte[] data) {
        setData(data);
    }


    @Override
    public ByteArray setLength(int length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public byte[] getData() {
        return mData;
    }

    public ByteArray setData(@NonNull byte[] data) {
        mData = data;
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            XTLog.d("[ByteArray] 数组长度溢出，需要进行截取处理...");
            mData = new byte[MAX_ARRAY_LENGTH];
            System.arraycopy(data, 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new byte[getLength()];
        if (bytes.length - index - tailLength < mLength) { //剩余数据不够解析
            XTLog.d("[ByteArray] 剩余数据不够解析，直接退出！");
            return false;
        }
        System.arraycopy(bytes, index, mData, 0, mData.length);
        return true;
    }

    @Override
    public String toString() {
        return "ByteArray{" +
                "mLength=" + mLength +
                ", mData=" + ConvertUtils.bytesToHexString(mData) +
                '}';
    }
}
