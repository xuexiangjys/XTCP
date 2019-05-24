package com.xuexiang.xtcp.core.message.template;

import android.support.annotation.Nullable;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.core.message.IMessage;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolItem;
import com.xuexiang.xtcp.utils.ConvertUtils;
import com.xuexiang.xtcp.utils.MessageUtils;

import java.util.Arrays;

import static com.xuexiang.xtcp.core.message.MessageConstants.DEFAULT_FRAME_END;
import static com.xuexiang.xtcp.core.message.MessageConstants.DEFAULT_FRAME_HEAD;

/**
 * 提供的一套TCP传输协议消息模版（无消息序号ID，无序的）<br>
 * <p>
 * 格式如下：<br>
 * <p>
 * 55AA      |   Len   |  OpCode  |   CheckSum  |  Ret    | ...data...  |  00FF  <br>
 * 帧头 (固定) |   帧长   |  命令码   |   校验和     |  结果码  |     数据     | 帧尾(固定)
 *
 * @author xuexiang
 * @since 2018/12/16 下午6:22
 */
public class XMessage implements IMessage {

    /**
     * 最小数据长度为10[data == null]
     */
    private static final int MIN_MESSAGE_LENGTH = 10;

    /**
     * 帧头(2个byte)
     * index:0
     */
    private byte[] mFrameHead;
    /**
     * 帧长度(2个byte) -> short[0～65535]
     * index:2
     */
    private short mFrameLength;
    /**
     * 功能码(一个byte)
     * index:4
     */
    private byte mOpCode;
    /**
     * 是否需要校验
     */
    private boolean mIsCheck;
    /**
     * 校验和(二个byte)
     * index:5
     */
    private short mCheckSum;
    /**
     * 结果码(一个byte)
     * index:7
     */
    private byte mRetCode;
    /**
     * 数据项(不定长)
     * index:8
     */
    private IProtocolItem mIProtocolItem;
    /**
     * 帧尾(2个byte)
     */
    private byte[] mFrameEnd;

    /**
     * 构造方法
     */
    public XMessage() {
        this(false);
    }

    /**
     * 构造方法
     *
     * @param isCheck 是否需要校验和
     */
    public XMessage(boolean isCheck) {
        mIsCheck = isCheck;
    }

    /**
     * 包装协议
     *
     * @param protocolItem
     * @return
     */
    public static XMessage wrap(IProtocolItem protocolItem) {
        return new XMessage().setIProtocolItem(protocolItem);
    }

    /**
     * 解析协议
     *
     * @param messageData
     * @return
     */
    @Nullable
    public static XMessage parse(byte[] messageData) {
        XMessage message = new XMessage();
        return message.byte2Msg(messageData) ? message : null;
    }

    public byte[] msg2Byte() {
        return msg2Byte(_XTCP.getDefaultStorageMode());
    }

    @Override
    public byte[] msg2Byte(StorageMode storageMode) {
        byte[] dataBytes = mIProtocolItem != null ? mIProtocolItem.proto2byte(storageMode) : null;

        //计算长度
        mFrameLength = calculateFrameLength(dataBytes);

        //计算校验和
        if (mIsCheck) {
            mCheckSum = MessageUtils.calculateChecksum(dataBytes);
        } else {
            mCheckSum = 0; //没有校验和就为0
        }

        //获取OpCode
        if (mOpCode == 0 && mIProtocolItem != null) {
            mOpCode = XProtocolCenter.getInstance().getOpCodeByClassName(mIProtocolItem.getClass().getCanonicalName());
        }

        byte[] messageBytes = new byte[mFrameLength + 4];  //帧头帧尾加起来长度为4
        System.arraycopy(mFrameHead, 0, messageBytes, 0, mFrameHead.length); //帧头
        ConvertUtils.fillShortToBytes(storageMode, mFrameLength, messageBytes, 2);  //帧的长度
        messageBytes[4] = mOpCode; //命令码

        ConvertUtils.fillShortToBytes(storageMode, mCheckSum, messageBytes, 5); //校验和
        messageBytes[7] = mRetCode; //结果码

        if (dataBytes != null && dataBytes.length > 0) {
            System.arraycopy(dataBytes, 0, messageBytes, 8, dataBytes.length); //填充数据
        }
        System.arraycopy(mFrameEnd, 0, messageBytes, messageBytes.length - 2, mFrameEnd.length);//帧尾
        return messageBytes;
    }

    public boolean byte2Msg(byte[] messageData) {
        return byte2Msg(messageData, _XTCP.getDefaultStorageMode());
    }

    @Override
    public boolean byte2Msg(byte[] messageData, StorageMode storageMode) {
        if (!MessageUtils.verifyMessage(getMinMessageLength(), 5, 8, messageData, storageMode)) {
            return false;
        }

        mFrameHead = new byte[2];
        System.arraycopy(messageData, 0, mFrameHead, 0, mFrameHead.length);
        mFrameLength = ConvertUtils.bytesToShort(storageMode, messageData, 2);  //帧的长度
        mOpCode = messageData[4];
        mCheckSum = ConvertUtils.bytesToShort(storageMode, messageData, 5);//校验和
        mRetCode = messageData[7];

        if (messageData.length - getMinMessageLength() > 0) {
            String className = XProtocolCenter.getInstance().getClassNameByOpCode(mOpCode);
            try {
                Class<?> clazz = Class.forName(className);
                mIProtocolItem = (IProtocolItem) clazz.newInstance();
                mIProtocolItem.byte2proto(messageData, 8, 2, storageMode);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        mFrameEnd = new byte[2];
        System.arraycopy(messageData, messageData.length - 2, mFrameEnd, 0, mFrameEnd.length);
        return true;
    }

    @Override
    public int getMinMessageLength() {
        return MIN_MESSAGE_LENGTH;
    }

    /**
     * 计算帧的长度<br>
     * LEN =  (Len(2) + OpCode(1) + CheckSum(2) + Ret(1)) + DATA
     *
     * @param dataBytes 数据内容
     * @return 获取帧的长度
     */
    private short calculateFrameLength(byte[] dataBytes) {
        return (short) (dataBytes != null ? (dataBytes.length + 6) : 6);
    }

    /**
     * 设置协议项
     *
     * @param protocolItem 协议项
     * @return
     */
    @Override
    public XMessage setIProtocolItem(IProtocolItem protocolItem) {
        mIProtocolItem = protocolItem;
        mFrameHead = DEFAULT_FRAME_HEAD;
        mFrameEnd = DEFAULT_FRAME_END;
        return this;
    }

    @Override
    public IProtocolItem getProtocolItem() {
        return mIProtocolItem;
    }

    //========set===========//

    public XMessage setFrameHead(byte[] frameHead) {
        mFrameHead = frameHead;
        return this;
    }

    public XMessage setOpCode(byte opCode) {
        mOpCode = opCode;
        return this;
    }

    public XMessage setIsCheck(boolean isCheck) {
        mIsCheck = isCheck;
        return this;
    }

    public XMessage setCheckSum(short checkSum) {
        mCheckSum = checkSum;
        return this;
    }

    public XMessage setRetCode(byte retCode) {
        mRetCode = retCode;
        return this;
    }

    public XMessage setFrameEnd(byte[] frameEnd) {
        mFrameEnd = frameEnd;
        return this;
    }

    //========get===========//

    public byte[] getFrameHead() {
        return mFrameHead;
    }

    public short getFrameLength() {
        return (short) (mIProtocolItem != null ? (mIProtocolItem.getProtocolLength() + 6) : 6);
    }

    public byte getOpCode() {
        return mOpCode;
    }

    public boolean isCheck() {
        return mIsCheck;
    }

    public short getCheckSum() {
        return mCheckSum;
    }

    public byte getRetCode() {
        return mRetCode;
    }

    public byte[] getFrameEnd() {
        return mFrameEnd;
    }

    @Override
    public String toString() {
        return "XMessage{" +
                "mFrameHead=" + ConvertUtils.bytesToHex(mFrameHead) +
                ", mFrameLength=" + mFrameLength +
                ", mOpCode=" + mOpCode +
                ", mIsCheck=" + mIsCheck +
                ", mCheckSum=" + mCheckSum +
                ", mRetCode=" + mRetCode +
                ", mIProtocolItem=" + mIProtocolItem +
                ", mFrameEnd=" + ConvertUtils.bytesToHex(mFrameEnd) +
                '}';
    }
}
