package com.xuexiang.xtcp.core.model;

import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.model.IProtocolItem;

import java.nio.charset.Charset;

/**
 * 固定长度的字符串
 * 例如：
 *  new FixedString(10)
 * @author XUE
 * @since 2019/3/6 14:28
 */
public class FixedString implements IProtocolItem {

    /**
     * 长度
     */
    private int mLength;
    /**
     * 定长字符串转成的数组，在内部使用，外部无法获取
     */
    private byte[] mFixedData;

    /**
     * 初始化的时候必须定义长度
     *
     * @param length
     */
    public FixedString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length should not be less than 0");
        }
        mLength = length;
        mFixedData = new byte[length];
    }

    /**
     * 设置定长字符串
     *
     * @param fixedString
     * @return
     */
    public FixedString setFixedString(String fixedString) {
        byte[] tmp = fixedString.getBytes();
        if (tmp.length > mLength) {
            throw new IndexOutOfBoundsException("The length of fixedString is too long");
        }
        System.arraycopy(tmp, 0, mFixedData, 0, tmp.length);
        return this;
    }

    /**
     * 设置定长字符串
     *
     * @param fixedString
     * @return
     */
    public FixedString setFixedString(String fixedString, String charset) {
        byte[] tmp = fixedString.getBytes(Charset.forName(charset));
        if (tmp.length > mLength) {
            throw new IndexOutOfBoundsException("The length of fixedString is too long");
        }
        System.arraycopy(tmp, 0, mFixedData, 0, tmp.length);
        return this;
    }

    @Override
    public int getProtocolLength() {
        return mLength;
    }

    @Override
    public byte[] proto2byte(StorageMode storageMode) {
        return mFixedData;
    }

    @Override
    public boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        if (bytes == null || mFixedData == null) {
            return false;
        }

        if (bytes.length - index - tailLength < mLength) { //剩余数据不够解析
            XTLog.d("[FixedString] 剩余数据不够解析，直接退出！");
            return false;
        }
        System.arraycopy(bytes, index, mFixedData, 0, mFixedData.length);
        return true;
    }

    public String getFixedString() {
        return new String(mFixedData);
    }

    public String getFixedString(String charset) {
        return new String(mFixedData, Charset.forName(charset));
    }
}
