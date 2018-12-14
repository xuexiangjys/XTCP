package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

import static com.xuexiang.xtcp.core.Constants.INT_MAX_LENGTH;
import static com.xuexiang.xtcp.core.Constants.MAX_ARRAY_LENGTH;

/**
 * int数组协议项<br>
 * <p>
 * mLength所占的byte位数为1，可表示的长度范围为【0～255】
 *
 * @author xuexiang
 * @since 2018/12/12 下午1:33
 */
public class IntArray extends AbstractArrayItem {
    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = DEFAULT_ARRAY_LENGTH_SIZE)
    private int mLength;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private int[] mData;

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public IntArray() {

    }

    public IntArray(@NonNull int[] data) {
        setData(data);
    }

    @Override
    public IntArray setLength(int length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public int[] getData() {
        return mData;
    }

    public IntArray setData(@NonNull int[] data) {
        mData = data;
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            mData = new int[MAX_ARRAY_LENGTH];
            System.arraycopy(data, 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new int[getLength()];
        int dataFieldLength = getArrayFieldLength(FIELD_NAME_DATA, INT_MAX_LENGTH);
        if (bytes.length - index - tailLength < mLength * dataFieldLength) { //剩余数据不够解析
            return false;
        }
        for (int i = 0; i < mLength; i++) {
            mData[i] = ConvertUtils.bytesToInt(storageMode, bytes, index, dataFieldLength);
            index += dataFieldLength;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IntArray{" +
                "mLength=" + mLength +
                ", mData=" + Arrays.toString(mData) +
                '}';
    }

}
