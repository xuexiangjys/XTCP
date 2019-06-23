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

import static com.xuexiang.xtcp.core.XTCPConstants.INT_MAX_LENGTH;
import static com.xuexiang.xtcp.core.XTCPConstants.LONG_MAX_LENGTH;
import static com.xuexiang.xtcp.core.XTCPConstants.SHORT_MAX_LENGTH;

/**
 * 转换相关工具类
 * (【小端】低位在前，高位在后)
 * (【大端】高位在前，低位在后) 符合我们正常的阅读习惯，在默认情况下，一般都是大端存储。
 *
 * 1位byte表示数范围 ：
 *      无符号 : 0～256
 *      有符号 : -128 ~ 127
 * 2位byte（short）表示数范围 ：
 *      无符号 : 0～65535
 *      有符号 : -32768（-2的15次方） ~ 32767（2的15次方-1）
 * 4位byte（int）表示数范围 ：
 *      无符号 : 0～4294967295
 *      有符号 : -2147483648（-2的31次方） ~ 2147483647（2的31次方-1）
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

    /***
     * byte[] 转16进制字符串[空格隔开]
     * @param src
     * @return
     */
    public static String bytesToHex(byte[] src) {
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
            stringBuilder.append(hv).append(" ");
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

    /**
     * 一位byte转int【无符号】
     *
     * @param b
     * @return 【0 ~ 255】
     */
    public static int byteToIntUnSigned(byte b) {
        return b & 0xFF;
    }

    // ======================【通用方法】=====================================//
    /**
     * 按照指定的存储方式来依次读取byte数据
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @return
     */
    private static int readBytes(StorageMode mode, @NonNull byte[] src, int offset, int length) {
        return readBytes(mode, src, offset, length, true);
    }


    /**
     * 按照指定的存储方式来依次读取byte数据
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return
     */
    private static int readBytes(StorageMode mode, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        int value = 0;
        //从低位开始读
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                if (unsigned || i != length -1) { //最高位为符号位，不能与0xFF
                    value |= (src[offset + i] & 0xFF) << (8 * i);
                } else {
                    value |= (src[offset + i]) << (8 * i);
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (unsigned || i != length -1) { //最高位为符号位，不能与0xFF
                    value |= (src[offset + length - i - 1] & 0xFF) << (8 * i);
                } else {
                    value |= (src[offset + length - i - 1]) << (8 * i);
                }
            }
        }
        return value;
    }

    /**
     * 填充数值到byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   数值的长度
     */
    private static void fillValueToBytes(StorageMode mode, int value, @NonNull byte[] src, int offset, int length) {
        fillValueToBytes(mode, value, src, offset, length, true);
    }

    /**
     * 填充数值到byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   数值的长度
     * @param unsigned 是否是无符号数
     */
    private static void fillValueToBytes(StorageMode mode, int value, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                if (unsigned) {
                    src[offset + i] = (byte) ((value >> (i * 8)) & 0xFF);
                } else {
                    src[offset + i] = (byte) (value >> (i * 8));
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (unsigned) {
                    src[offset + length - i - 1] = (byte) ((value >> (i * 8)) & 0xFF);
                } else {
                    src[offset + length - i - 1] = (byte) (value >> (i * 8));
                }
            }
        }
    }

    /**
     * 将值转化为byte数组
     *
     * @param mode     存储方式
     * @param value    数值
     * @param length   数值的长度
     */
    private static byte[] valueToBytes(StorageMode mode, int value, int length) {
        return valueToBytes(mode, value, length, true);
    }

    /**
     * 将值转化为byte数组
     *
     * @param mode     存储方式
     * @param value    数值
     * @param length   数值的长度
     * @param unsigned 是否是无符号数
     */
    private static byte[] valueToBytes(StorageMode mode, int value, int length, boolean unsigned) {
        byte[] bytes = new byte[length];
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                if (unsigned) {
                    bytes[i] = (byte) ((value >> (i * 8)) & 0xFF);
                } else {
                    bytes[i] = (byte) (value >> (i * 8));
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (unsigned) {
                    bytes[length - i - 1] = (byte) ((value >> (i * 8)) & 0xFF);
                } else {
                    bytes[length - i - 1] = (byte) (value >> (i * 8));
                }
            }
        }
        return bytes;
    }

    // ======================【byte数组<-->（无符号）short】=====================================//
    /**
     * byte数组中取short数值
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @return short数值
     */
    public static short bytesToShort(StorageMode mode, @NonNull byte[] src, int offset, int length) {
        return bytesToShort(mode, src, offset, length, true);
    }

    /**
     * byte数组中取short数值
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static short bytesToShort(StorageMode mode, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (length < 1 || length > SHORT_MAX_LENGTH) { //纠正错误的长度
            length = SHORT_MAX_LENGTH;
        }
        return (short) readBytes(mode, src, offset, length, unsigned);
    }

    /**
     * byte数组中取short数值
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @return short数值
     */
    public static short bytesToShort(StorageMode mode, @NonNull byte[] src, int offset) {
        return (short) readBytes(mode, src, offset, SHORT_MAX_LENGTH, true);
    }

    /**
     * byte数组中取short数值
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static short bytesToShort(StorageMode mode, @NonNull byte[] src, int offset, boolean unsigned) {
        return (short) readBytes(mode, src, offset, SHORT_MAX_LENGTH, unsigned);
    }

    /**
     * 将short填充至byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @return short数值
     */
    public static int fillShortToBytes(StorageMode mode, short value, @NonNull byte[] src, int offset, int length) {
        return fillShortToBytes(mode, value, src, offset, length, true);
    }

    /**
     * 将short填充至byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static int fillShortToBytes(StorageMode mode, short value, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (length < 1 || length > SHORT_MAX_LENGTH) { //纠正错误的长度
            length = SHORT_MAX_LENGTH;
        }
        fillValueToBytes(mode, value, src, offset, length, unsigned);
        return offset + length;
    }


    /**
     * 将short填充至byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @return short数值
     */
    public static int fillShortToBytes(StorageMode mode, short value, @NonNull byte[] src, int offset) {
        fillValueToBytes(mode, value, src, offset, SHORT_MAX_LENGTH, true);
        return offset + SHORT_MAX_LENGTH;
    }

    /**
     * 将short填充至byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static int fillShortToBytes(StorageMode mode, short value, @NonNull byte[] src, int offset, boolean unsigned) {
        fillValueToBytes(mode, value, src, offset, SHORT_MAX_LENGTH, unsigned);
        return offset + SHORT_MAX_LENGTH;
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
        return bytesToInt(mode, src, offset, length, true);
    }

    /**
     * byte数组中取int数值
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static int bytesToInt(StorageMode mode, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (length < 1 || length > INT_MAX_LENGTH) {  //纠正错误的长度
            length = INT_MAX_LENGTH;
        }
        return readBytes(mode, src, offset, length, unsigned);
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
        return fillIntToBytes(mode, value, src, offset, length, true);
    }

    /**
     * 将Int填充至byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static int fillIntToBytes(StorageMode mode, int value, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (length < 1 || length > INT_MAX_LENGTH) { //纠正错误的长度
            length = INT_MAX_LENGTH;
        }
        fillValueToBytes(mode, value, src, offset, length, unsigned);
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
        return bytesToLong(mode, src, offset, length, true);
    }

    /**
     * byte数组中取long数值
     *
     * @param mode     存储方式
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static long bytesToLong(StorageMode mode, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (length < 1 || length > LONG_MAX_LENGTH) {  //纠正错误的长度
            length = LONG_MAX_LENGTH;
        }

        long value = 0;
        //从低位开始读
        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                if (unsigned || i != length -1) { //最高位为符号位，不能与0xFF
                    value |= ((long) src[offset + i] & 0xFF) << (8 * i);
                } else {
                    value |= ((long) src[offset + i]) << (8 * i);
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (unsigned || i != length -1) { //最高位为符号位，不能与0xFF
                    value |= ((long) src[offset + length - i - 1] & 0xFF) << (8 * i);
                } else {
                    value |= ((long) src[offset + length - i - 1]) << (8 * i);
                }
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
        return fillLongToBytes(mode, value, src, offset, length, true);
    }

    /**
     * 将long填充至byte数组中
     *
     * @param mode     存储方式
     * @param value    数值
     * @param src      byte数组
     * @param offset   从数组的第offset位开始
     * @param length   长度
     * @param unsigned 是否是无符号数
     * @return short数值
     */
    public static int fillLongToBytes(StorageMode mode, long value, @NonNull byte[] src, int offset, int length, boolean unsigned) {
        if (length < 1 || length > LONG_MAX_LENGTH) { //纠正错误的长度
            length = LONG_MAX_LENGTH;
        }

        if (StorageMode.LittleEndian.equals(mode)) {
            for (int i = 0; i < length; i++) {
                if (unsigned) {
                    src[offset + i] = (byte) ((value >> (i * 8)) & 0xFF);
                } else {
                    src[offset + i] = (byte) (value >> (i * 8));
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (unsigned) {
                    src[offset + length - i - 1] = (byte) ((value >> (i * 8)) & 0xFF);
                } else {
                    src[offset + length - i - 1] = (byte) (value >> (i * 8));
                }
            }
        }
        return offset + length;
    }

    // ======================【大小端转化】=====================================//

    /**
     * 大端值转小端
     *
     * @param value
     * @param length
     * @param unsigned 是否是无符号数
     * @return
     */
    public static int bigEndianToLittleEndian(int value, int length, boolean unsigned) {
        byte[] src = valueToBytes(StorageMode.BigEndian, value, length, unsigned);
        return readBytes(StorageMode.LittleEndian, src, 0, length, unsigned);
    }

    /**
     * 小端值转大端
     *
     * @param value
     * @param length
     * @param unsigned 是否是无符号数
     * @return
     */
    public static int littleEndianToBigEndian(int value, int length, boolean unsigned) {
        byte[] src = valueToBytes(StorageMode.LittleEndian, value, length, unsigned);
        return readBytes(StorageMode.BigEndian, src, 0, length, unsigned);
    }


}
