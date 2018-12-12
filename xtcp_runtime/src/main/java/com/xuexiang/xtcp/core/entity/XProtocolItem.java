package com.xuexiang.xtcp.core.entity;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolItem;

/**
 * 默认提供的协议项
 *
 * @author xuexiang
 * @since 2018/12/12 下午1:39
 */
public class XProtocolItem implements IProtocolItem {

    @Override
    public int getProtocolLength() {
        return _XTCP.getIProtocolParser().getProtocolLength(this);
    }

    @Override
    public byte[] proto2byte(StorageMode storageMode) {
        return _XTCP.getIProtocolParser().protoBody2Byte(this, storageMode);
    }

    @Override
    public void byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        _XTCP.getIProtocolParser().byte2ProtoBody(this, bytes, index, tailLength, storageMode);
    }
}
