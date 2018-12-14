package com.xuexiang.xtcp.model;

import com.xuexiang.xtcp.enums.StorageMode;

/**
 * 解析byte时为了获取数组的长度。
 *
 * @author xuexiang
 * @since 2018/12/12 下午4:35
 */
public interface IArrayItem extends IProtocolItem {

    /**
     * @return 获取数组数据的长度(length)
     */
    int getLength();

    /**
     * 填充数组的长度
     *
     * @param bytes
     * @param index
     * @param storageMode
     * @return 读取长度占的byte位数
     */
    int fillArrayLength(byte[] bytes, int index, StorageMode storageMode);


    /**
     * 默认数组长度所占的byte位数
     */
    int DEFAULT_ARRAY_LENGTH_SIZE = 1;

    /**
     * 大数组长度所占的byte位数
     */
    int LARGE_ARRAY_LENGTH_SIZE = 2;


}
