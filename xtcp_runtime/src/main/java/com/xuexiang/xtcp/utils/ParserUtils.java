package com.xuexiang.xtcp.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IArrayItem;
import com.xuexiang.xtcp.model.IProtocolItem;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import static com.xuexiang.xtcp.enums.StorageMode.Default;

/**
 * 解析工具类
 *
 * @author xuexiang
 * @since 2018/12/11 下午2:52
 */
public final class ParserUtils {

    private ParserUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 计算协议数据部分的长度
     *
     * @param obj 协议消息体， 类需要被@Protocol修饰
     * @return
     */
    public static int calculateProtocolLength(@NonNull Object obj) throws IllegalAccessException, UnsupportedEncodingException {
        int length = 0;
        Class<?> classType = obj.getClass();
        Field[] fields = classType.getDeclaredFields();

        ProtocolField protocolField;
        Class<?> fieldType;
        for (Field field : fields) {
            field.setAccessible(true);
            protocolField = field.getAnnotation(ProtocolField.class);
            if (protocolField == null || !protocolField.isField()) {
                continue;
            }

            fieldType = field.getType();

            if (protocolField.length() > 0) {
                length += protocolField.length();
            } else {
                if (byte.class.equals(fieldType)) {
                    length += 1;
                } else if (short.class.equals(fieldType)) {
                    length += getFieldLength(protocolField, 2);
                } else if (int.class.equals(fieldType)) {
                    length += getFieldLength(protocolField, 4);
                } else if (long.class.equals(fieldType)) {
                    length += getFieldLength(protocolField, 8);
                } else if (byte[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((byte[]) data).length;
                    }
                } else if (short[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((short[]) data).length * getFieldLength(protocolField, 2);
                    }
                } else if (int[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((int[]) data).length * getFieldLength(protocolField, 4);
                    }
                } else if (long[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((long[]) data).length * getFieldLength(protocolField, 8);
                    }
                } else if (String.class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((String) data).getBytes(protocolField.charset()).length;
                    }
                } else if (IProtocolItem.class.isAssignableFrom(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((IProtocolItem) data).getProtocolLength();
                    }
                } else if (IProtocolItem[].class.isAssignableFrom(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        IProtocolItem[] items = (IProtocolItem[]) data;
                        for (IProtocolItem item : items) {
                            length += item.getProtocolLength();
                        }
                    }
                }
            }
        }
        return length;
    }


    /**
     * 将传输的Byte数组解析为协议体
     *
     * @param obj        消息对象, 类需要被@Protocol修饰
     * @param bytes      数据集合[整个消息体数据，包含头和尾]
     * @param index      需要开始解析的索引
     * @param tailLength 消息尾的长度[和index一起决定了数据解析的范围]
     */
    public static void byte2ProtoBody(@NonNull Object obj, byte[] bytes, int index, int tailLength, StorageMode protocolMode) throws IllegalAccessException {
        Class<?> classType = obj.getClass();

        Field[] fields = classType.getDeclaredFields();
        if (fields == null) {
            return;
        }

        if (protocolMode == null) {
            protocolMode = getProtocolStorageMode(classType);
        }

        ProtocolField protocolField;
        Class<?> fieldType;
        int offset = index;
        int length;
        StorageMode mode;
        for (Field field : fields) {
            field.setAccessible(true);
            if (bytes.length - index - tailLength <= 0) { //剩余长度已不足以读取，直接结束
                break;
            }

            protocolField = field.getAnnotation(ProtocolField.class);
            if (protocolField == null || !protocolField.isField()) {
                continue;
            }

            fieldType = field.getType();
            mode = getStorageMode(protocolMode, protocolField.mode());

            // 开始各种类型的处理
            if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
                field.set(obj, bytes[offset]);
                offset += 1;
            } else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
                length = getFieldLength(protocolField, 2);
                field.set(obj, ConvertUtils.bytesToShort(mode, bytes, offset, length));
                offset += length;
            } else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
                length = getFieldLength(protocolField, 4);
                field.set(obj, ConvertUtils.bytesToInt(mode, bytes, offset, length));
                offset += length;
            } else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
                length = getFieldLength(protocolField, 8);
                field.set(obj, ConvertUtils.bytesToLong(mode, bytes, offset, length));
                offset += length;
            } else if (IArrayItem.class.isAssignableFrom(fieldType)) {


            }


        }
    }

    /**
     * 将协议体转为传输的Byte数组
     *
     * @param obj 消息对象, 类需要被@Protocol修饰
     * @return
     */
    public static byte[] protoBody2Byte(@NonNull Object obj, StorageMode protocolMode) throws UnsupportedEncodingException, IllegalAccessException {
        byte[] res = new byte[0];

        Class<?> classType = obj.getClass();
        Field[] fields = classType.getDeclaredFields();
        if (fields == null) {
            return res;
        }

        if (protocolMode == null) {
            protocolMode = getProtocolStorageMode(classType);
        }

        int totalLength = calculateProtocolLength(obj);
        ProtocolField protocolField;
        res = new byte[totalLength];
        Class<?> fieldType;
        int offset = 0;
        int length;
        StorageMode mode;
        for (Field field : fields) {
            field.setAccessible(true);

            protocolField = field.getAnnotation(ProtocolField.class);
            if (protocolField == null || !protocolField.isField()) {
                continue;
            }

            fieldType = field.getType();
            mode = getStorageMode(protocolMode, protocolField.mode());
            Object value = field.get(obj);

            if (value == null) { //未赋值的直接跳过
                continue;
            }

            // 开始各种类型的处理
            if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
                res[offset] = (byte) value;
                offset += 1;
            } else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
                length = getFieldLength(protocolField, 2);
                ConvertUtils.fillShortToBytes(mode, (short) value, res, offset, length);
                offset += length;
            } else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
                length = getFieldLength(protocolField, 4);
                ConvertUtils.fillIntToBytes(mode, (int) value, res, offset, length);
                offset += length;
            } else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
                length = getFieldLength(protocolField, 8);
                ConvertUtils.fillLongToBytes(mode, (long) value, res, offset, length);
                offset += length;
            } else if (byte[].class.equals(fieldType) || Byte[].class.equals(fieldType)) {
                byte[] tmp = (byte[]) value;
                System.arraycopy(tmp, 0, res, offset, tmp.length);
                offset += tmp.length;
            } else if (short[].class.equals(fieldType) || Short[].class.equals(fieldType)) {
                length = getFieldLength(protocolField, 2);
                short[] tmp = (short[]) value;
                for (short s : tmp) {
                    ConvertUtils.fillShortToBytes(mode, s, res, offset, length);
                    offset += length;
                }
            } else if (int[].class.equals(fieldType) || Integer[].class.equals(fieldType)) {
                length = getFieldLength(protocolField, 4);
                int[] tmp = (int[]) value;
                for (int i : tmp) {
                    ConvertUtils.fillIntToBytes(mode, i, res, offset, length);
                    offset += length;
                }
            } else if (long[].class.equals(fieldType) || Long[].class.equals(fieldType)) {
                length = getFieldLength(protocolField, 8);
                long[] tmp = (long[]) value;
                for (long l : tmp) {
                    ConvertUtils.fillLongToBytes(mode, l, res, offset, length);
                    offset += length;
                }
            } else if (String.class.equals(fieldType)) {
                byte[] tmp = ((String) value).getBytes(protocolField.charset());
                System.arraycopy(tmp, 0, res, offset, tmp.length);
                offset += tmp.length;
            } else if (IProtocolItem.class.isAssignableFrom(fieldType)) {
                IProtocolItem item = (IProtocolItem) value;
                byte[] tmp = item.proto2byte(mode);
                System.arraycopy(tmp, 0, res, offset, tmp.length);
                offset += tmp.length;
            } else if (IProtocolItem[].class.isAssignableFrom(fieldType)) {
                IProtocolItem[] items = (IProtocolItem[]) value;
                byte[] tmp;
                for (IProtocolItem item: items) {
                    tmp = item.proto2byte(mode);
                    System.arraycopy(tmp, 0, res, offset, tmp.length);
                    offset += tmp.length;
                }
            }
        }
        return res;
    }


    //========================辅助方法==============================//

    @Nullable
    private static StorageMode getProtocolStorageMode(Class<?> classType) {
        StorageMode protocolMode = null;
        Protocol protocol = classType.getAnnotation(Protocol.class);
        if (protocol != null) {
            protocolMode = protocol.mode();
        }
        return protocolMode;
    }

    /**
     * 获取字段的存储方式
     *
     * @param protocolMode 协议的存储方式
     * @param fieldMode    协议字段的存储方式
     * @return
     */
    public static StorageMode getStorageMode(StorageMode protocolMode, @NonNull StorageMode fieldMode) {
        StorageMode mode = _XTCP.getDefaultStorageMode();
        if (protocolMode != null) {
            if (Default.equals(protocolMode)) {
                if (!Default.equals(fieldMode)) {
                    mode = fieldMode;
                }
            } else {
                mode = Default.equals(fieldMode) ? protocolMode : fieldMode;
            }
        } else {
            if (!Default.equals(fieldMode)) {
                mode = fieldMode;
            }
        }
        return mode;
    }

    /**
     * 获取协议字段的长度
     *
     * @param protocolField
     * @param maxLength     最大的长度
     * @return
     */
    public static int getFieldLength(ProtocolField protocolField, int maxLength) {
        return protocolField.length() > maxLength || protocolField.length() < 1 ? maxLength : protocolField.length();
    }


}
