package com.xuexiang.xtcp.core.entity;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IArrayItem;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

/**
 * 数组协议项[只是为了解决byte解析时，对应数组数据长度未知的问题]
 *
 * @author xuexiang
 * @since 2018/12/12 下午1:33
 */
public class ArrayItem<T> implements IArrayItem {

    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = 1)
    public int length;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    public T[] data;

    public ArrayItem() {
    }

    public ArrayItem(@NonNull T[] data) {
       setData(data);
    }

    @Override
    public int getLength() {
        return length;
    }

    public ArrayItem<T> setLength(short length) {
        this.length = length;
        return this;
    }

    public T[] getData() {
        return data;
    }

    public ArrayItem<T> setData(@NonNull T[] data) {
        this.data = data;
        this.length = data.length;
        return this;
    }

    @Override
    public String toString() {
        return "ArrayItem{" +
                "length=" + length +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public int getProtocolLength() {
        return _XTCP.getIProtocolParser().getProtocolLength(this);
    }

    @Override
    public byte[] proto2byte(StorageMode storageMode) {
        return _XTCP.getIProtocolParser().protoBody2Byte(this, storageMode);
    }

    @Override
    public void byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        length = ConvertUtils.bytesToShort(storageMode, bytes, index, 1); //拿到长度


    }
}
