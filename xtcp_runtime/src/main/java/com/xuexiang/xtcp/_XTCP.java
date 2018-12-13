package com.xuexiang.xtcp;

import com.xuexiang.xtcp.core.IProtocolParser;
import com.xuexiang.xtcp.enums.StorageMode;

/**
 * @author xuexiang
 * @since 2018/12/11 下午2:08
 */
public class _XTCP {

    /**
     * 协议解析器
     */
    private static IProtocolParser sIProtocolParser;

    /**
     * 默认数据存储方式
     */
    private static StorageMode sStorageMode;

    /**
     * 设置协议解析器
     *
     * @param iProtocolParser 协议解析器
     * @return
     */
    public static void setIProtocolParser(IProtocolParser iProtocolParser) {
        _XTCP.sIProtocolParser = iProtocolParser;
    }

    public static IProtocolParser getIProtocolParser() {
        return sIProtocolParser;
    }

    /**
     * 设置默认存储方式
     *
     * @param sStorageMode
     */
    public static void setDefaultStorageMode(StorageMode sStorageMode) {
        _XTCP.sStorageMode = sStorageMode;
    }

    public static StorageMode getDefaultStorageMode() {
        return sStorageMode;
    }


}
