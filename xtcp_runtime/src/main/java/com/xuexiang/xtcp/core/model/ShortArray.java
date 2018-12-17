package com.xuexiang.xtcp.core.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

import static com.xuexiang.xtcp.core.XTCPConstants.MAX_ARRAY_LENGTH;
import static com.xuexiang.xtcp.core.XTCPConstants.SHORT_MAX_LENGTH;

/**
 * short数组协议项<br>
 * <p>
 * mLength所占的byte位数为1，可表示的长度范围为【0～255】
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
     * 获取short数组包装类
     *
     * @param data
     * @return
     */
    public static ShortArray wrap(@NonNull short[] data) {
        return new ShortArray(data);
    }

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public ShortArray() {

    }

    public ShortArray(@NonNull short[] data) {
        setData(data);
    }

    @Override
    public ShortArray setLength(int length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public ShortArray setData(@NonNull short[] data) {
        mData = data;
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            XTLog.d("[ShortArray] 数组长度溢出，需要进行截取处理...");

            mData = new short[MAX_ARRAY_LENGTH];
            System.arraycopy(data, 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    public short[] getData() {
        return mData;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new short[getLength()];
        int dataFieldLength = getArrayFieldLength(FIELD_NAME_DATA, SHORT_MAX_LENGTH);
        if (bytes.length - index - tailLength < mLength * dataFieldLength) { //剩余数据不够解析
            XTLog.d("[ShortArray] 剩余数据不够解析，直接退出！");
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
