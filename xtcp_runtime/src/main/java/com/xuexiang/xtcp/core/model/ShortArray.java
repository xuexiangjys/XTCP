package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

/**
 * short数组协议项[只是为了解决byte解析时，对应数组数据长度未知的问题]。所有的数组，只要是需要解析的，就必须要实现IArrayItem。
 *
 * @author xuexiang
 * @since 2018/12/13 下午6:25
 */
public class ShortArray extends AbstractArrayItem {

    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = DEFAULT_ARRAY_LENGTH_SIZE)
    private int mLength;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private short[] mData;

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public ShortArray() {

    }

    public ShortArray(@NonNull short[] data) {
        setData(data);
    }

    @Override
    public ShortArray setLength(short length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public ShortArray setData(@NonNull short[] data) {
        mData = data;
        mLength = data.length;
        return this;
    }

    public short[] getData() {
        return mData;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new short[getLength()];
        int dataFieldLength = getArrayFieldLength("mData", 2);
        if (bytes.length - index - tailLength < mLength * dataFieldLength) { //剩余数据不够解析
            return false;
        }
        for (int i = 0; i < mLength; i++) {
            mData[i] = ConvertUtils.bytesToShort(storageMode, bytes, index, dataFieldLength);
            index += dataFieldLength;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ShortArray{" +
                "mLength=" + mLength +
                ", mData=" + Arrays.toString(mData) +
                '}';
    }
}
