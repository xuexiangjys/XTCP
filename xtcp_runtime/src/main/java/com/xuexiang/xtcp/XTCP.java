package com.xuexiang.xtcp;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.core.parser.IProtocolParser;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.core.parser.impl.DefaultProtocolParser;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.ILogger;
import com.xuexiang.xtcp.logs.XTLog;
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
        //默认使用大端的存储方式
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

    //=================初始化设置=========================//
    /**
     * 设置协议中心
     *
     * @param iProtocolCenters
     * @return
     */
    public XTCP addIProtocolCenter(IProtocolCenter... iProtocolCenters) {
        XProtocolCenter.getInstance().addIProtocolCenter(iProtocolCenters);
        return this;
    }

    /**
     * 设置协议字段中心
     *
     * @param iProtocolFieldCenters
     * @return
     */
    public XTCP addIProtocolFieldCenter(IProtocolFieldCenter... iProtocolFieldCenters) {
        XProtocolCenter.getInstance().addIProtocolFieldCenter(iProtocolFieldCenters);
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

    //=================日志=========================//
    /**
     * 设置是否是debug模式
     *
     * @param isDebug
     * @return
     */
    public XTCP debug(boolean isDebug) {
        XTLog.debug(isDebug);
        return this;
    }

    /**
     * 设置日志打印接口
     *
     * @param logger
     * @return
     */
    public XTCP setILogger(@NonNull ILogger logger) {
        XTLog.setLogger(logger);
        return this;
    }


}
