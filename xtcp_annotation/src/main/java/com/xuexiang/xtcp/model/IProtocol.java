package com.xuexiang.xtcp.model;

import com.xuexiang.xtcp.enums.StorageMode;

/**
 * 协议实现接口
 *
 * @author xuexiang
 * @since 2018/12/11 上午9:11
 */
public interface IProtocol {

    /**
     * 将协议实体转化为byte数组
     *
     * @param storageMode 存储形式
     * @return
     */
    byte[] proto2byte(StorageMode storageMode);

    /**
     * 将byte数组数据转化为协议实体
     *
     * @param bytes       byte数组数据
     * @param index       起始字节
     * @param tailLength  消息尾的长度[和index一起决定了数据解析的范围]
     * @param storageMode 存储形式
     * @return
     */
    void byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode);

}
