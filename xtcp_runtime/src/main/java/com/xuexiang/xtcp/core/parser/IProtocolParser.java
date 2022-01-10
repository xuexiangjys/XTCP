package com.xuexiang.xtcp.core.parser;

import androidx.annotation.NonNull;

import com.xuexiang.xtcp.enums.StorageMode;

/**
 * 协议解析器
 *
 * @author xuexiang
 * @since 2018/12/11 下午1:30
 */
public interface IProtocolParser {

    /**
     * 解析协议体获取协议的数据长度
     *
     * @param obj
     * @return
     */
    int getProtocolLength(@NonNull Object obj);

    /**
     * 将协议体转为传输的Byte数组
     *
     * @param obj         消息对象, 类需要被@Protocol修饰
     * @param storageMode 数据的存储形式
     * @return
     */
    byte[] protoBody2Byte(@NonNull Object obj, StorageMode storageMode);

    /**
     * 将传输的Byte数组解析为协议体
     *
     * @param obj         消息对象, 类需要被@Protocol修饰
     * @param bytes       数据集合[整个消息体数据，包含头和尾]
     * @param index       需要开始解析的索引
     * @param tailLength  消息尾的长度[和index一起决定了数据解析的范围]
     * @param storageMode 数据的存储形式
     */
    boolean byte2ProtoBody(@NonNull Object obj, byte[] bytes, int index, int tailLength, StorageMode storageMode);


}
