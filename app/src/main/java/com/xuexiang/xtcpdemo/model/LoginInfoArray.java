package com.xuexiang.xtcpdemo.model;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.AbstractArrayItem;
import com.xuexiang.xtcp.enums.StorageMode;

import java.util.Arrays;

import static com.xuexiang.xtcp.core.XTCPConstants.MAX_ARRAY_LENGTH;

/**
 * 自定义协议数组【由于自定义的协议，其数组的类型不可知，故无法封装，只能自定义协议包装体】
 *
 * @author xuexiang
 * @since 2018/12/15 下午10:02
 */
public class LoginInfoArray extends AbstractArrayItem {

    /**
     * 集合数组的长度
     */
    @ProtocolField(index = 0, length = DEFAULT_ARRAY_LENGTH_SIZE)
    private int mLength;

    /**
     * 集合数据
     */
    @ProtocolField(index = 1)
    private LoginInfo[] mData;

    /**
     * 获取数组包装类
     *
     * @param data
     * @return
     */
    public static LoginInfoArray wrap(@NonNull LoginInfo[] data) {
        return new LoginInfoArray(data);
    }


    /**
     * 空的构造方法不能去除，用于反射构造
     */
    public LoginInfoArray() {

    }

    public LoginInfoArray(@NonNull LoginInfo[] data) {
        setData(data);
    }


    @Override
    public LoginInfoArray setLength(int length) {
        mLength = length;
        return this;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public LoginInfo[] getData() {
        return mData;
    }

    public LoginInfoArray setData(@NonNull LoginInfo[] data) {
        mData = data;
        if (mData.length > MAX_ARRAY_LENGTH) { //长度不能超过255
            mData = new LoginInfo[MAX_ARRAY_LENGTH];
            System.arraycopy(data, 0, mData, 0, mData.length);
        }
        mLength = mData.length;
        return this;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        mData = new LoginInfo[getLength()];
        Class<?> type = getArrayFieldType(FIELD_NAME_DATA);
        if (type == null) {
            return false;
        }

        LoginInfo item;
        for (int i = 0; i < mData.length; i++) {
            if (bytes.length - index - tailLength <= 0) { //剩余长度已不足以读取，直接结束
                return false;
            }

            item = new LoginInfo();
            item.byte2proto(bytes, index, tailLength, storageMode);
            index += item.getProtocolLength();
            mData[i] = item;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LoginInfoArray{" +
                "mLength=" + mLength +
                ", mData=" + Arrays.toString(mData) +
                '}';
    }
}
