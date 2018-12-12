package com.xuexiang.xtcp.model;

/**
 * 解析byte时为了获取数组的长度
 *
 * @author xuexiang
 * @since 2018/12/12 下午4:35
 */
public interface IArrayItem extends IProtocolItem {

    /**
     * @return 获取数组数据的长度(length)
     */
    int getLength();
}
