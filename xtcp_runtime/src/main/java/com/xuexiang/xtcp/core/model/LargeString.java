package com.xuexiang.xtcp.core.model;

import androidx.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.utils.ConvertUtils;

import java.nio.charset.Charset;

import static com.xuexiang.xtcp.core.XTCPConstants.MAX_LARGE_ARRAY_LENGTH;

/**
 * 长String协议项<br>
 * <p>
 * mLength所占的byte位数为2，可表示的长度范围为【0～65535】
 *
 * @author xuexiang
 * @since 2018/12/14 下午11:57
 */
public class LargeString extends AbstractArrayItem {
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
     * 获取长String数组包装类
     *
     * @param data
     * @return
     */
    public static LargeString wrap(@NonNull String data) {
        return new LargeString(data);
    }

    /**
     * 获取长String数组包装类
     *
     * @param data
     * @return
     */
    public static LargeString wrap(@NonNull String data, String charset) {
        return new LargeString(data, charset);
    }

    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public LargeString() {

    }

    public LargeString(@NonNull String data) {
        setData(data);
    }

    public LargeString(@NonNull String data, String charset) {
        setData(data, charset);
    }

    public LargeString setData(@NonNull String data) {
        mData = data.getBytes();
        if (mData.length > MAX_LARGE_ARRAY_LENGTH) { //长度不能超过65535
            XTLog.d("[LargeString] 数组长度溢出，需要进行截取处理...");

            mData = new byte[MAX_LARGE_ARRAY_LENGTH];
            System.arraycopy(data.getBytes(), 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    public LargeString setData(@NonNull String data, String charset) {
        mData = data.getBytes(Charset.forName(charset));
        if (mData.length > MAX_LARGE_ARRAY_LENGTH) { //长度不能超过65535
            XTLog.d("[LargeString] 数组长度溢出，需要进行截取处理...");

            mData = new byte[MAX_LARGE_ARRAY_LENGTH];
            System.arraycopy(data.getBytes(Charset.forName(charset)), 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    public String getData() {
        return new String(mData);
    }

    public String getData(String charset) {
        return new String(mData, Charset.forName(charset));
    }

    @Override
    public LargeString setLength(int length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new byte[getLength()];
        if (isDataEnoughToParse(bytes, index, tailLength, mLength)) { //剩余数据不够解析
            XTLog.d("[LargeString] 剩余数据不够解析，直接退出！");
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
        return "LargeString{" +
                "mLength=" + mLength +
                ", mData=" + getData() +
                '}';
    }
}
