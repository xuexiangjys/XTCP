package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;

/**
 * byte数组协议项[只是为了解决byte解析时，对应数组数据长度未知的问题]。所有的数组，只要是需要解析的，就必须要实现IArrayItem。
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
     * 空的构造方法不能去除，用于反射构造
     */
    public ByteArray() {

    }

    public ByteArray(@NonNull byte[] data) {
        setData(data);
    }


    @Override
    public ByteArray setLength(short length) {
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
        mLength = data.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new byte[getLength()];
        if (bytes.length - index - tailLength < mLength) { //剩余数据不够解析
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
