package com.xuexiang.xtcp.core.entity;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

/**
 * 数组协议项[只是为了解决byte解析时，对应数组数据长度未知的问题]。所有的数组，只要是需要解析的，就必须要使用ArrayItem。
 *
 * @author xuexiang
 * @since 2018/12/12 下午1:33
 */
public class IntArray extends AbstractArrayItem {
    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = DEFAULT_ARRAY_LENGTH_SIZE)
    private int length;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private int[] data;

    public IntArray() {
    }

    public IntArray(@NonNull int[] data) {
        setData(data);
    }

    public AbstractArrayItem setLength(short length) {
        this.length = length;
        return this;
    }

    public int[] getData() {
        return data;
    }

    public IntArray setData(@NonNull int[] data) {
        this.data = data;
        this.length = data.length;
        return this;
    }

    @Override
    public String toString() {
        return "IntArray{" +
                "length=" + length +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        data = new int[length];
        int dataFieldLength = getArrayFieldLength("data", 4);
        if (bytes.length - index - tailLength < length * dataFieldLength) { //剩余数据不够解析
            return false;
        }
        for (int i = 0; i < length; i++) {
            data[i] = ConvertUtils.bytesToInt(storageMode, bytes, index, dataFieldLength);
            index += dataFieldLength;
        }
        return true;
    }


    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int fillArrayLength(byte[] bytes, int index, StorageMode storageMode) {
        length = ConvertUtils.bytesToInt(storageMode, bytes, index, DEFAULT_ARRAY_LENGTH_SIZE); //拿到长度
        return DEFAULT_ARRAY_LENGTH_SIZE;
    }

}
