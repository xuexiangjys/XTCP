package com.xuexiang.xtcp.core.impl;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.core.IProtocolParser;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.utils.ParserUtils;

/**
 * 默认的协议解析器
 *
 * @author xuexiang
 * @since 2018/12/11 下午2:20
 */
public class DefaultProtocolParser implements IProtocolParser {

    private static final String TAG = "DefaultProtocolParser";

    @Override
    public int getProtocolLength(@NonNull Object obj) {
        try {
            return ParserUtils.calculateProtocolLength(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        }
        return 0;
    }

    @Override
    public byte[] protoBody2Byte(@NonNull Object obj, StorageMode storageMode) {
        try {
            return ParserUtils.protoBody2Byte(obj, storageMode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        }
        return new byte[0];
    }

    @Override
    public boolean byte2ProtoBody(@NonNull Object obj, byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        try {
            return ParserUtils.byte2ProtoBody(obj, bytes, index, tailLength, storageMode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            XTLog.eTag(TAG, e);
        }
        return false;
    }


}
