package com.xuexiang.xtcp.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.core.message.IMessage;
import com.xuexiang.xtcp.enums.StorageMode;

import java.util.ArrayList;
import java.util.List;

import static com.xuexiang.xtcp.core.message.MessageConstants.DEFAULT_FRAME_END;
import static com.xuexiang.xtcp.core.message.MessageConstants.DEFAULT_FRAME_HEAD;

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

    //==============分包==================//
    /**
     * 解析消息包数据
     *
     * @param cls  消息体类
     * @param data 数据
     * @return 已解析的消息集合
     */
    public static List<IMessage> parseMessageBytes(Class<? extends IMessage> cls, @NonNull byte[] data) {
        try {
            return parseMessageBytes(cls, data, _XTCP.getDefaultStorageMode());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析消息包数据
     *
     * @param cls  消息体类
     * @param data 数据
     * @param mode 存储方式
     * @return 已解析的消息集合
     */
    public static List<IMessage> parseMessageBytes(Class<? extends IMessage> cls, @NonNull byte[] data, StorageMode mode) throws IllegalAccessException, InstantiationException {
        IMessage message = cls.newInstance();
        List<byte[]> packages = getSubPackage(data, message.getMinMessageLength());
        if (packages == null || packages.size() == 0) {
            return null;
        }

        List<IMessage> result = new ArrayList<>();
        for (int i = 0; i < packages.size(); i++) {
            message = cls.newInstance();
            if (message.byte2Msg(packages.get(i), mode)) {
                result.add(message);
            }
        }
        return result;
    }


    /**
     * 获取默认消息模版的分包
     *
     * @param data
     * @param minMessageLength 最小消息长度
     * @return
     */
    @Nullable
    public static List<byte[]> getSubPackage(@NonNull byte[] data, int minMessageLength) {
        if (data.length < minMessageLength) {
            return null;
        }

        //头索引
        List<Integer> headIndex = new ArrayList<>();
        //尾索引
        List<Integer> endIndex = new ArrayList<>();

        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] == DEFAULT_FRAME_HEAD[0] && data[i + 1] == DEFAULT_FRAME_HEAD[1]) {
                headIndex.add(i);
            } else if (data[i] == DEFAULT_FRAME_END[0] && data[i + 1] == DEFAULT_FRAME_END[1]) {
                endIndex.add(i);
            }
        }

        int size = Math.min(headIndex.size(), endIndex.size());
        if (size == 0) {
            return null;
        }

        List<byte[]> packages = new ArrayList<>();
        byte[] tmp;
        for (int i = 0; i < size; i++) {
            tmp = new byte[endIndex.get(i) - headIndex.get(i) + 2];
            System.arraycopy(data, headIndex.get(i), tmp, 0, tmp.length);
            packages.add(tmp);
        }
        return packages;
    }

    //==============校验==================//

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
            checkSum += ConvertUtils.byteToUnSigned(dataItem);
        }
        return checkSum;
    }

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
            checkSum += ConvertUtils.byteToUnSigned(messageData[index]);
        }
        return checkSum;
    }
}
