package com.xuexiang.xtcp.core.message;

/**
 * 消息常量
 *
 * @author xuexiang
 * @since 2018/12/17 上午9:58
 */
public final class MessageConstants {

    /**
     * 默认帧头
     */
    public static final byte[] DEFAULT_FRAME_HEAD = new byte[]{(byte) 0x55, (byte) 0xAA};
    /**
     * 默认帧尾
     */
    public static final byte[] DEFAULT_FRAME_END = new byte[]{(byte) 0x00, (byte) 0xFF};

}
