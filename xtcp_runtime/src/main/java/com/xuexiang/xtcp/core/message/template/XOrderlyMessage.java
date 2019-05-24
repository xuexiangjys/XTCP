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
 * 提供的一套TCP传输协议消息模版（有消息序号ID，有序的）<br>
 * <p>
 * 格式如下：<br>
 * <p>
 * 55AA      |   Len   |   OpCode  |    ID    |   CheckSum  | ...data...  |  00FF <br>
 * 帧头 (固定) |   帧长   |   命令码   |  消息ID   |   校验和     |    数据      | 帧尾(固定)
 *
 * @author xuexiang
 * @since 2018/12/17 上午9:49
 */
public class XOrderlyMessage implements IMessage {

    /**
     * 最小数据长度为11(2 + 2 + 1 + 2 + 2 + 2)[data == null]
     */
    private static final int MIN_MESSAGE_LENGTH = 11;

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
     * 消息序号(2个byte) -> short[0～65535]
     * index:5
     */
    private short mMsgID;
    /**
     * 是否需要校验
     */
    private boolean mIsCheck;
    /**
     * 校验和(二个byte)
     * index:7
     */
    private short mCheckSum;

    /**
     * 数据项(不定长)
     * index:9
     */
    private IProtocolItem mIProtocolItem;
    /**
     * 帧尾(2个byte)
     */
    private byte[] mFrameEnd;

    /**
     * 构造方法
     */
    public XOrderlyMessage() {
        this(false);
    }

    /**
     * 构造方法
     *
     * @param isCheck 是否需要校验和
     */
    public XOrderlyMessage(boolean isCheck) {
        mIsCheck = isCheck;
    }

    /**
     * 包装协议
     *
     * @param protocolItem
     * @param msgID
     * @return
     */
    public static XOrderlyMessage wrap(IProtocolItem protocolItem, int msgID) {
        return new XOrderlyMessage().setIProtocolItem(protocolItem).setMsgID(msgID);
    }

    /**
     * 解析协议
     *
     * @param messageData
     * @return
     */
    @Nullable
    public static XOrderlyMessage parse(byte[] messageData) {
        XOrderlyMessage message = new XOrderlyMessage();
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
        ConvertUtils.fillShortToBytes(storageMode, mMsgID, messageBytes, 5);  //消息序号
        ConvertUtils.fillShortToBytes(storageMode, mCheckSum, messageBytes, 7); //校验和

        if (dataBytes != null && dataBytes.length > 0) {
            System.arraycopy(dataBytes, 0, messageBytes, 9, dataBytes.length); //填充数据
        }
        System.arraycopy(mFrameEnd, 0, messageBytes, messageBytes.length - 2, mFrameEnd.length);//帧尾
        return messageBytes;
    }

    public boolean byte2Msg(byte[] messageData) {
        return byte2Msg(messageData, _XTCP.getDefaultStorageMode());
    }

    @Override
    public boolean byte2Msg(byte[] messageData, StorageMode storageMode) {
        if (!MessageUtils.verifyMessage(getMinMessageLength(), 7, 9, messageData, storageMode)) {
            return false;
        }

        mFrameHead = new byte[2];  //帧头
        System.arraycopy(messageData, 0, mFrameHead, 0, mFrameHead.length);
        mFrameLength = ConvertUtils.bytesToShort(storageMode, messageData, 2);  //帧的长度
        mOpCode = messageData[4]; //命令码
        mMsgID = ConvertUtils.bytesToShort(storageMode, messageData, 5); //消息序号
        mCheckSum = ConvertUtils.bytesToShort(storageMode, messageData, 7);//校验和

        if (messageData.length - getMinMessageLength() > 0) {
            String className = XProtocolCenter.getInstance().getClassNameByOpCode(mOpCode);
            try {
                Class<?> clazz = Class.forName(className);
                mIProtocolItem = (IProtocolItem) clazz.newInstance();
                mIProtocolItem.byte2proto(messageData, 9, 2, storageMode);

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
     * LEN =  (Len(2) + OpCode(1) + ID(2) + CheckSum(2)) + DATA
     *
     * @param dataBytes 数据内容
     * @return 获取帧的长度
     */
    private short calculateFrameLength(byte[] dataBytes) {
        return (short) (dataBytes != null ? (dataBytes.length + 7) : 7);
    }

    /**
     * 设置协议项
     *
     * @param protocolItem 协议项
     * @return
     */
    @Override
    public XOrderlyMessage setIProtocolItem(IProtocolItem protocolItem) {
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


    public XOrderlyMessage setFrameHead(byte[] frameHead) {
        mFrameHead = frameHead;
        return this;
    }

    public XOrderlyMessage setOpCode(byte opCode) {
        mOpCode = opCode;
        return this;
    }

    public XOrderlyMessage setIsCheck(boolean isCheck) {
        mIsCheck = isCheck;
        return this;
    }

    public XOrderlyMessage setCheckSum(short checkSum) {
        mCheckSum = checkSum;
        return this;
    }

    public XOrderlyMessage setMsgID(int msgID) {
        mMsgID = (short) msgID;
        return this;
    }

    public XOrderlyMessage setMsgID(short msgID) {
        mMsgID = msgID;
        return this;
    }

    public XOrderlyMessage setFrameEnd(byte[] frameEnd) {
        mFrameEnd = frameEnd;
        return this;
    }

    //========get===========//

    public byte[] getFrameHead() {
        return mFrameHead;
    }

    public short getFrameLength() {
        return (short) (mIProtocolItem != null ? (mIProtocolItem.getProtocolLength() + 7) : 7);
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

    public short getMsgID() {
        return mMsgID;
    }

    public byte[] getFrameEnd() {
        return mFrameEnd;
    }

    @Override
    public String toString() {
        return "XOrderlyMessage{" +
                "mFrameHead=" + ConvertUtils.bytesToHex(mFrameHead) +
                ", mFrameLength=" + mFrameLength +
                ", mOpCode=" + mOpCode +
                ", mMsgID=" + mMsgID +
                ", mIsCheck=" + mIsCheck +
                ", mCheckSum=" + mCheckSum +
                ", mIProtocolItem=" + mIProtocolItem +
                ", mFrameEnd=" + ConvertUtils.bytesToHex(mFrameEnd) +
                '}';
    }
}
