package com.xuexiang.xtcp.utils;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.enums.StorageMode;

import static com.xuexiang.xtcp.core.XTCPConstants.SHORT_MAX_LENGTH;

/**
 * 消息工具类
 *
 * @author xuexiang
 * @since 2018/12/17 上午10:11
 */
public final class MessageUtils {

    private MessageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 计算校验和【简单地对数据内容进行相加】
     *
     * @param dataBytes 数据内容
     * @return
     */
    public static short calculateChecksum(byte[] dataBytes) {
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
     * @param minMessageLength 数据包的最小长度
     * @param checksumIndex    校验和所在的索引
     * @param dataIndex        数据所在的索引
     * @param messageData      整个消息体数据
     * @param storageMode      存储方式
     * @return
     */
    public static boolean verifyMessage(int minMessageLength, int checksumIndex, int dataIndex, byte[] messageData, StorageMode storageMode) {
        return verifyMessageLength(minMessageLength, messageData, storageMode)
                && verifyChecksum(minMessageLength, checksumIndex, dataIndex, messageData, storageMode);
    }

    /**
     * 验证消息长度
     *
     * @param minMessageLength 数据包的最小长度
     * @param messageData      整个消息体
     * @param storageMode      存储方式
     * @return 消息长度是否正确
     */
    public static boolean verifyMessageLength(int minMessageLength, byte[] messageData, StorageMode storageMode) {
        //头尾占4位byte，第三位是帧长度
        return messageData.length >= minMessageLength
                && (messageData.length - 4) == ConvertUtils.bytesToShort(storageMode, messageData, 2);
    }

    /**
     * 检查校验和
     *
     * @param minMessageLength 数据包的最小长度
     * @param checksumIndex    校验和所在的索引
     * @param dataIndex        数据所在的索引
     * @param messageData      整个消息体
     * @param storageMode      存储方式
     * @return 校验和是否通过
     */
    public static boolean verifyChecksum(int minMessageLength, int checksumIndex, int dataIndex, byte[] messageData, StorageMode storageMode) {
        if (messageData.length < minMessageLength) return false;

        short checkSum = ConvertUtils.bytesToShort(storageMode, messageData, checksumIndex); //读取校验和
        //默认约定，读取到的校验和为0的话，视为不对校验和进行校验
        return checkSum == 0 || checkSum == calculatePackageMessageChecksum(minMessageLength, dataIndex, messageData);
    }


    /**
     * 计算消息包的校验和
     *
     * @param minMessageLength 数据包的最小长度
     * @param dataIndex        数据所在的索引
     * @param messageData      整个消息体
     * @return 校验和
     */
    public static short calculatePackageMessageChecksum(int minMessageLength, int dataIndex, @NonNull byte[] messageData) {
        if (messageData.length <= minMessageLength) return 0;

        short checkSum = 0;
        for (int index = dataIndex; index < messageData.length - 2; index++) {
            checkSum += (short) ConvertUtils.byteToIntUnSigned(messageData[index]);
        }
        return checkSum;
    }
}
