package com.xuexiang.xtcp.core.model;

import androidx.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.utils.ConvertUtils;

import static com.xuexiang.xtcp.core.XTCPConstants.MAX_LARGE_ARRAY_LENGTH;

/**
 * 长byte数组协议项<br>
 * <p>
 * 和ByteArray相比，LargeByteArray中mLength所占的byte位数为2，可表示的长度范围为【0～65535】
 *
 * @author xuexiang
 * @since 2018/12/14 下午6:44
 */
public class LargeByteArray extends AbstractArrayItem {

    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = LARGE_ARRAY_LENGTH_SIZE)
    private int mLength;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private byte[] mData;

    /**
     * 获取长Byte数组包装类
     *
     * @param data
     * @return
     */
    public static LargeByteArray wrap(@NonNull byte[] data) {
        return new LargeByteArray(data);
    }

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public LargeByteArray() {

    }

    public LargeByteArray(@NonNull byte[] data) {
        setData(data);
    }


    @Override
    public LargeByteArray setLength(int length) {
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

    public LargeByteArray setData(@NonNull byte[] data) {
        mData = data;
        if (mData.length > MAX_LARGE_ARRAY_LENGTH) { //长度不能超过65535
            XTLog.d("[LargeByteArray] 数组长度溢出，需要进行截取处理...");

            mData = new byte[MAX_LARGE_ARRAY_LENGTH];
            System.arraycopy(data, 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new byte[getLength()];
        if (isDataEnoughToParse(bytes, index, tailLength, mLength)) { //剩余数据不够解析
            XTLog.d("[LargeByteArray] 剩余数据不够解析，直接退出！");
            return false;
        }
        System.arraycopy(bytes, index, mData, 0, mData.length);
        return true;
    }

    @Override
    public int fillArrayLength(byte[] bytes, int index, StorageMode storageMode) {
        setLength(ConvertUtils.bytesToInt(storageMode, bytes, index, LARGE_ARRAY_LENGTH_SIZE)); //拿到长度
        return LARGE_ARRAY_LENGTH_SIZE;
    }

    @Override
    public String toString() {
        return "LargeByteArray{" +
                "mLength=" + mLength +
                ", mData=" + ConvertUtils.bytesToHexString(mData) +
                '}';
    }
}
