package com.xuexiang.xtcp.model;

/**
 * 协议项
 *
 * @author xuexiang
 * @since 2018/12/11 下午3:13
 */
public interface IProtocolItem extends IProtocol {

    /**
     * @return 获取协议项的数据长度
     */
    int getProtocolLength();


}
