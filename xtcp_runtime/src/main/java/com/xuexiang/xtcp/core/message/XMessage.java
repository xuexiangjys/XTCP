package com.xuexiang.xtcp.core.message;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolItem;
import com.xuexiang.xtcp.utils.ConvertUtils;

/**
 * 默认提供的一套TCP传输协议消息模版
 * 格式如下：
 * 55AA      |   Len   |  OpCode  |   CheckSum  |  Ret    | ...data...  |  00FF
 * 帧头 (固定) |   帧长   |  命令码   |   校验和     |  结果码  |     数据     | 帧尾(固定)
 *
 * @author xuexiang
 * @since 2018/12/16 下午6:22
 */
public class XMessage implements IMessage {

    /**
     * 最小数据长度为10[data == null]
     */
    public static final int MIN_MESSAGE_LENGTH = 10;

    /**
     * 默认帧头
     */
    public static final byte[] DEFAULT_FRAME_HEAD = new byte[]{(byte) 0x55, (byte) 0xAA};
    /**
     * 默认帧尾
     */
    public static final byte[] DEFAULT_FRAME_END = new byte[]{(byte) 0x00, (byte) 0xFF};

    /**
     * 帧头(2个byte)
     * index:0
     */
    protected byte[] mFrameHead;
    /**
     * 帧长度(2个byte) -> short[-32768<---->32767]
     * index:2
     */
    private byte[] mFrameLength = new byte[2];
    /**
     * 功能码(一个byte)
     * index:4
     */
    private byte mFuncCode;
    /**
     * 是否需要校验
     */
    private boolean mIsCheck;
    /**
     * 校验和(二个byte)
     * index:5
     */
    private byte[] mCheckSum = new byte[2];
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
    protected byte[] mFrameEnd;

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

    public byte[] msg2Byte() {
        return msg2Byte(_XTCP.getDefaultStorageMode());
    }

    @Override
    public byte[] msg2Byte(StorageMode storageMode) {
        byte[] dataBytes = mIProtocolItem != null ? mIProtocolItem.proto2byte(storageMode) : null;

        short frameLength = calculateFrameLength(dataBytes);

        //计算长度
        ConvertUtils.fillShortToBytes(storageMode, frameLength, mFrameLength, 0);

        //计算校验和
        if (mIsCheck) {
            short checkSum = calculateChecksum(dataBytes);
            ConvertUtils.fillShortToBytes(storageMode, checkSum, mCheckSum, 0);
        } else {
            mCheckSum = new byte[2]; //没有校验和就为0
        }

        if (mFuncCode == 0 && mIProtocolItem != null) {
            mFuncCode = XProtocolCenter.getInstance().getOpCodeByClassName(mIProtocolItem.getClass().getCanonicalName());
        }

        byte[] messageBytes = new byte[frameLength + 4];  //帧头帧尾加起来长度为4
        System.arraycopy(mFrameHead, 0, messageBytes, 0, mFrameHead.length); //帧头
        System.arraycopy(mFrameLength, 0, messageBytes, 2, mFrameLength.length); //帧的长度
        messageBytes[4] = mFuncCode; //功能码

        System.arraycopy(mCheckSum, 0, messageBytes, 5, mCheckSum.length); //校验和
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
        if (!verifyMessage(messageData, storageMode)) {
            return false;
        }

        mFrameHead = new byte[2];
        System.arraycopy(messageData, 0, mFrameHead, 0, mFrameHead.length);
        mFrameLength = new byte[2];
        System.arraycopy(messageData, 2, mFrameLength, 0, mFrameLength.length);

        mFuncCode = messageData[4];

        mCheckSum = new byte[2];
        System.arraycopy(messageData, 5, mCheckSum, 0, mCheckSum.length);

        mRetCode = messageData[7];

        if (messageData.length - MIN_MESSAGE_LENGTH > 0) {
            String className = XProtocolCenter.getInstance().getClassNameByOpCode(mFuncCode);
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

    /**
     * 计算帧的长度<br>
     * LEN =  (Len(2) + Func(1) + CheckSum(2 + Ret(1)) + DATA
     *
     * @param dataBytes 数据内容
     * @return 获取帧的长度
     */
    private short calculateFrameLength(byte[] dataBytes) {
        return (short) (dataBytes != null ? (dataBytes.length + 6) : 6);
    }

    /**
     * 计算校验和【简单地对ContentData内容进行相加】
     *
     * @param dataBytes 数据内容
     * @return
     */
    private short calculateChecksum(byte[] dataBytes) {
        if (dataBytes == null || dataBytes.length == 0) return 0;

        short checkSum = 0;
        for (byte dataItem : dataBytes) {
            checkSum += (short) ConvertUtils.byteToIntUnSigned(dataItem);
        }
        return checkSum;
    }

    //==============校验==================//

    /**
     * 校验消息是否正确
     *
     * @param messageData
     * @param storageMode 存储方式
     * @return
     */
    public boolean verifyMessage(byte[] messageData, StorageMode storageMode) {
        return verifyMessageLength(messageData, storageMode) && verifyChecksum(messageData, storageMode);
    }

    /**
     * 验证消息长度
     *
     * @param messageData 整个消息体
     * @param storageMode 存储方式
     * @return 消息长度是否正确
     */
    private static boolean verifyMessageLength(byte[] messageData, StorageMode storageMode) {
        //头尾占4位byte，第三位是帧长度
        return messageData.length >= MIN_MESSAGE_LENGTH && (messageData.length - 4) == ConvertUtils.bytesToShort(storageMode, messageData, 2);
    }

    /**
     * 检查校验和
     *
     * @param messageData 整个消息体
     * @param storageMode 存储方式
     * @return 校验和是否通过
     */
    private static boolean verifyChecksum(byte[] messageData, StorageMode storageMode) {
        if (messageData.length < MIN_MESSAGE_LENGTH) return false;

        short checkSum = ConvertUtils.bytesToShort(storageMode, messageData, 5); //读取校验和
        //默认约定，读取到的校验和为0的话，视为不对校验和进行校验
        return checkSum == 0 || checkSum == calculatePackageMessageChecksum(messageData);
    }


    /**
     * 计算消息包的校验和
     *
     * @param messageData 整个消息体
     * @return 校验和
     */
    private static short calculatePackageMessageChecksum(@NonNull byte[] messageData) {
        if (messageData.length <= MIN_MESSAGE_LENGTH) return 0;

        short checkSum = 0;
        for (int index = 8; index < messageData.length - 2; index++) {
            checkSum += (short) ConvertUtils.byteToIntUnSigned(messageData[index]);
        }
        return checkSum;
    }

    //========set===========//

    /**
     * 设置协议项
     *
     * @param protocolItem 协议项
     * @return
     */
    public XMessage setIProtocolItem(IProtocolItem protocolItem) {
        mIProtocolItem = protocolItem;
        mFrameHead = DEFAULT_FRAME_HEAD;
        mFrameEnd = DEFAULT_FRAME_END;
        return this;
    }

    public XMessage setFrameHead(byte[] frameHead) {
        mFrameHead = frameHead;
        return this;
    }

    public XMessage setFuncCode(byte funcCode) {
        mFuncCode = funcCode;
        return this;
    }

    public XMessage setIsCheck(boolean isCheck) {
        mIsCheck = isCheck;
        return this;
    }

    public XMessage setCheckSum(byte[] checkSum) {
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

    public byte getFuncCode() {
        return mFuncCode;
    }

    public boolean isCheck() {
        return mIsCheck;
    }

    public byte[] getCheckSum() {
        return mCheckSum;
    }

    public byte getRetCode() {
        return mRetCode;
    }

    public IProtocolItem getProtocolItem() {
        return mIProtocolItem;
    }

    public byte[] getFrameEnd() {
        return mFrameEnd;
    }


}
