package com.xuexiang.xtcp.model;

import java.util.Map;

/**
 * 协议中心
 *
 * @author xuexiang
 * @since 2018/12/11 下午1:52
 */
public interface IProtocolCenter {

    /**
     * 根据协议的类名获取协议的详细信息
     *
     * @param className
     * @return
     */
    ProtocolInfo getProtocol(final String className);

    /**
     * 根据opcode获取协议的详细信息
     *
     * @param opcode
     * @return
     */
    ProtocolInfo getProtocol(final byte opcode);

    /**
     * 根据协议的类名获取对应的OpCode
     *
     * @param className
     * @return
     */
    byte getOpCodeByClassName(final String className);


    /**
     * @return 获取 协议类名 -> 协议信息 的映射
     */
    Map<String, ProtocolInfo> getClass2Info();

    /**
     * @return 获取 opcode -> 协议信息 的映射
     */
    Map<Byte, ProtocolInfo> getOpCode2Info();



}
