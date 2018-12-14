package com.xuexiang.xtcp.core.impl;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.core.IProtocolParser;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ParserUtils;

/**
 * 默认的协议解析器
 *
 * @author xuexiang
 * @since 2018/12/11 下午2:20
 */
public class DefaultProtocolParser implements IProtocolParser {


    @Override
    public int getProtocolLength(@NonNull Object obj) {
        try {
            return ParserUtils.calculateProtocolLength(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public byte[] protoBody2Byte(@NonNull Object obj, StorageMode storageMode) {
        try {
            return ParserUtils.protoBody2Byte(obj, storageMode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean byte2ProtoBody(@NonNull Object obj, byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        try {
            return ParserUtils.byte2ProtoBody(obj, bytes, index, tailLength, storageMode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return false;
    }


}
