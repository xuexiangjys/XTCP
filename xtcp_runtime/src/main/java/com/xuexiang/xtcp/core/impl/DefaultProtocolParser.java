package com.xuexiang.xtcp.core.impl;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.core.IProtocolParser;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ParserUtils;

import java.io.UnsupportedEncodingException;

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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public byte[] protoBody2Byte(@NonNull Object obj, StorageMode storageMode) {
        try {
            return ParserUtils.protoBody2Byte(obj, storageMode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public void byte2ProtoBody(@NonNull Object obj, byte[] bytes, int index, int tailLength, StorageMode storageMode) {
        try {
            ParserUtils.byte2ProtoBody(obj, bytes, index, tailLength, storageMode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
