package com.xuexiang.xtcp;

import com.xuexiang.xtcp.core.IProtocolParser;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.core.impl.DefaultProtocolParser;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolCenter;
import com.xuexiang.xtcp.model.IProtocolFieldCenter;

/**
 * XTCP API中心
 *
 * @author xuexiang
 * @since 2018/12/11 下午1:27
 */
public class XTCP {

    private static volatile XTCP sInstance = null;

    private XTCP() {
        _XTCP.setIProtocolParser(new DefaultProtocolParser());
        _XTCP.setDefaultStorageMode(StorageMode.BigEndian);
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static XTCP getInstance() {
        if (sInstance == null) {
            synchronized (XTCP.class) {
                if (sInstance == null) {
                    sInstance = new XTCP();
                }
            }
        }
        return sInstance;
    }

    /**
     * 设置协议中心
     *
     * @param iProtocolCenters
     * @return
     */
    public XTCP setIProtocolCenter(IProtocolCenter... iProtocolCenters) {
        XProtocolCenter.getInstance().setIProtocolCenter(iProtocolCenters);
        return this;
    }

    /**
     * 设置协议字段中心
     *
     * @param iProtocolFieldCenters
     * @return
     */
    public XTCP setIProtocolFieldCenter(IProtocolFieldCenter... iProtocolFieldCenters) {
        XProtocolCenter.getInstance().setIProtocolFieldCenter(iProtocolFieldCenters);
        return this;
    }

    /**
     * 设置协议解析器
     *
     * @param iProtocolParser
     * @return
     */
    public XTCP setIProtocolParser(IProtocolParser iProtocolParser) {
        _XTCP.setIProtocolParser(iProtocolParser);
        return this;
    }

    /**
     * 设置默认的数据存储方式
     *
     * @param sStorageMode
     * @return
     */
    public XTCP setDefaultStorageMode(StorageMode sStorageMode) {
        _XTCP.setDefaultStorageMode(sStorageMode);
        return this;
    }


}
