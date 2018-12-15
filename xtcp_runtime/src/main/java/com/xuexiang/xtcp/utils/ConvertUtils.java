/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xtcp.utils;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.enums.StorageMode;

import static com.xuexiang.xtcp.core.Constants.INT_MAX_LENGTH;
import static com.xuexiang.xtcp.core.Constants.LONG_MAX_LENGTH;
import static com.xuexiang.xtcp.core.Constants.SHORT_MAX_LENGTH;

/**
 * 转换相关工具类
 * (【小端】低位在前，高位在后)
 * (【大端】高位在前，低位在后) 符合我们正常的阅读习惯，在默认情况下，一般都是大端存储。
 *
 * @author xuexiang
 * @since 2018/12/11 下午3:30
 */
public final class ConvertUtils {

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /***
     * byte[] 转16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * hexString转byteArr
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    // ======================【通用方法】=====================================//

    /**
     * 按照指定的存储方式来依次读取byte数据
     *
     * @param mode   存储方式
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return
     */
    private static int readBytes(StorageMode mode, @NonNull byte[] src, int offset, int length) {
        int value = 0;
        //从低位开始读
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                value |= (src[offset + i] & 0xFF) << (8 * i);
            }
        } else {
            for (int i = 0; i < length; i++) {
                value |= (src[offset + length - i - 1] & 0xFF) << (8 * i);
            }
        }
        return value;
    }

    /**
     * 填充数值到byte数组中
     *
     * @param mode   存储方式
     * @param value  数值
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 数值的长度
     */
    private static void fillValueToBytes(StorageMode mode, int value, @NonNull byte[] src, int offset, int length) {
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                src[offset + i] = (byte) ((value >> (i * 8)) & 0xFF);
            }
        } else {
            for (int i = 0; i < length; i++) {
                src[offset + length - i - 1] = (byte) ((value >> (i * 8)) & 0xFF);
            }
        }
    }

    // ======================【byte数组<-->（无符号）short】=====================================//

    /**
     * byte数组中取short数值
     *
     * @param mode   存储方式
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return short数值
     */
    public static short bytesToShort(StorageMode mode, @NonNull byte[] src, int offset, int length) {
        if (length < 1 || length > SHORT_MAX_LENGTH) { //纠正错误的长度
            length = SHORT_MAX_LENGTH;
        }
        return (short) readBytes(mode, src, offset, length);
    }

    /**
     * 将short填充至byte数组中
     *
     * @param mode   存储方式
     * @param value  数值
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return short数值
     */
    public static int fillShortToBytes(StorageMode mode, short value, @NonNull byte[] src, int offset, int length) {
        if (length < 1 || length > SHORT_MAX_LENGTH) { //纠正错误的长度
            length = SHORT_MAX_LENGTH;
        }
        fillValueToBytes(mode, value, src, offset, length);
        return offset + length;
    }

    // ======================【byte数组<-->（无符号）int】=====================================//

    /**
     * byte数组中取int数值
     *
     * @param mode   存储方式
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return short数值
     */
    public static int bytesToInt(StorageMode mode, @NonNull byte[] src, int offset, int length) {
        if (length < 1 || length > INT_MAX_LENGTH) {  //纠正错误的长度
            length = INT_MAX_LENGTH;
        }
        return readBytes(mode, src, offset, length);
    }

    /**
     * 将Int填充至byte数组中
     *
     * @param mode   存储方式
     * @param value  数值
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return short数值
     */
    public static int fillIntToBytes(StorageMode mode, int value, @NonNull byte[] src, int offset, int length) {
        if (length < 1 || length > INT_MAX_LENGTH) { //纠正错误的长度
            length = INT_MAX_LENGTH;
        }
        fillValueToBytes(mode, value, src, offset, length);
        return offset + length;
    }

    // ======================【byte数组<-->（无符号）long】=====================================//

    /**
     * byte数组中取long数值
     *
     * @param mode   存储方式
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return short数值
     */
    public static long bytesToLong(StorageMode mode, @NonNull byte[] src, int offset, int length) {
        if (length < 1 || length > LONG_MAX_LENGTH) {  //纠正错误的长度
            length = LONG_MAX_LENGTH;
        }

        long value = 0;
        //从低位开始读
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                value |= ((long) src[offset + i] & 0xFF) << (8 * i);
            }
        } else {
            for (int i = 0; i < length; i++) {
                value |= ((long) src[offset + length - i - 1] & 0xFF) << (8 * i);
            }
        }
        return value;
    }


    /**
     * 将long填充至byte数组中
     *
     * @param mode   存储方式
     * @param value  数值
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @param length 长度
     * @return short数值
     */
    public static int fillLongToBytes(StorageMode mode, long value, @NonNull byte[] src, int offset, int length) {
        if (length < 1 || length > LONG_MAX_LENGTH) { //纠正错误的长度
            length = LONG_MAX_LENGTH;
        }

        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                src[offset + i] = (byte) ((value >> (i * 8)) & 0xFF);
            }
        } else {
            for (int i = 0; i < length; i++) {
                src[offset + length - i - 1] = (byte) ((value >> (i * 8)) & 0xFF);
            }
        }
        return offset + length;
    }


}
