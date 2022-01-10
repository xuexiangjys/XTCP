package com.xuexiang.xtcp.core.model;

import androidx.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.util.Arrays;

import static com.xuexiang.xtcp.core.XTCPConstants.LONG_MAX_LENGTH;
import static com.xuexiang.xtcp.core.XTCPConstants.MAX_ARRAY_LENGTH;

/**
 * long数组协议项【无符号】<br>
 * <p>
 * mLength所占的byte位数为1，可表示的长度范围为【0～255】
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
     * 获取long数组包装类
     *
     * @param data
     * @return
     */
    public static LongArray wrap(@NonNull long[] data) {
        return new LongArray(data);
    }

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public LongArray() {

    }

    public LongArray(@NonNull long[] data) {
        setData(data);
    }

    @Override
    public LongArray setLength(int length) {
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
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            XTLog.d("[LongArray] 数组长度溢出，需要进行截取处理...");

            mData = new long[MAX_ARRAY_LENGTH];
            System.arraycopy(data, 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new long[getLength()];
        int dataFieldLength = getArrayFieldLength(FIELD_NAME_DATA, LONG_MAX_LENGTH);
        if (isDataEnoughToParse(bytes, index, tailLength, mLength * dataFieldLength)) { //剩余数据不够解析
            XTLog.d("[LongArray] 剩余数据不够解析，直接退出！");
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
