package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

/**
 * long数组协议项[只是为了解决byte解析时，对应数组数据长度未知的问题]。所有的数组，只要是需要解析的，就必须要实现IArrayItem。
 *
 * @author xuexiang
 * @since 2018/12/14 上午12:05
 */
public class LongArray extends AbstractArrayItem {

    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = DEFAULT_ARRAY_LENGTH_SIZE)
    private int mLength;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private long[] mData;

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public LongArray() {

    }

    public LongArray(@NonNull long[] data) {
        setData(data);
    }

    @Override
    public LongArray setLength(short length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public long[] getData() {
        return mData;
    }

    public LongArray setData(@NonNull long[] data) {
        mData = data;
        mLength = data.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new long[getLength()];
        int dataFieldLength = getArrayFieldLength("mData", 8);
        if (bytes.length - index - tailLength < mLength * dataFieldLength) { //剩余数据不够解析
            return false;
        }
        for (int i = 0; i < mLength; i++) {
            mData[i] = ConvertUtils.bytesToLong(storageMode, bytes, index, dataFieldLength);
            index += dataFieldLength;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LongArray{" +
                "mLength=" + mLength +
                ", mData=" + Arrays.toString(mData) +
                '}';
    }
}
