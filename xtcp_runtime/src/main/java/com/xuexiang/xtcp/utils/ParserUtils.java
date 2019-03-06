package com.xuexiang.xtcp.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.logs.XTLog;
import com.xuexiang.xtcp.model.IArrayItem;
import com.xuexiang.xtcp.model.IProtocol;
import com.xuexiang.xtcp.model.IProtocolItem;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import static com.xuexiang.xtcp.core.XTCPConstants.INT_MAX_LENGTH;
import static com.xuexiang.xtcp.core.XTCPConstants.LONG_MAX_LENGTH;
import static com.xuexiang.xtcp.core.XTCPConstants.SHORT_MAX_LENGTH;
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
     * @param obj 协议消息体
     * @return
     */
    public static int calculateProtocolLength(@NonNull Object obj) throws IllegalAccessException {
        int length = 0;
        Class<?> classType = obj.getClass();
        Field[] fields = XProtocolCenter.getInstance().getProtocolFields(classType);

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
                    length += getFieldLength(protocolField, SHORT_MAX_LENGTH);
                } else if (int.class.equals(fieldType)) {
                    length += getFieldLength(protocolField, INT_MAX_LENGTH);
                } else if (long.class.equals(fieldType)) {
                    length += getFieldLength(protocolField, LONG_MAX_LENGTH);
                } else if (byte[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((byte[]) data).length;
                    }
                } else if (short[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((short[]) data).length * getFieldLength(protocolField, SHORT_MAX_LENGTH);
                    }
                } else if (int[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((int[]) data).length * getFieldLength(protocolField, INT_MAX_LENGTH);
                    }
                } else if (long[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((long[]) data).length * getFieldLength(protocolField, LONG_MAX_LENGTH);
                    }
                } else if (String.class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        length += ((String) data).getBytes(Charset.forName(protocolField.charset())).length;
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
                } else {
                    XTLog.e("无法识别的字段类型，无法获取字段长度! 字段: " + getFieldInfo(classType, fieldType, field) + ", 字段值:" + field.get(obj));
                }
            }
        }
        return length;
    }


    /**
     * 计算从某个字段之后，剩余可解析的协议长度
     *
     * @param obj          协议消息体
     * @param objFieldName 协议字段， 需要被@ProtocolField修饰
     * @return
     */
    public static int calculateLeftFieldLength(@NonNull Object obj, String objFieldName) throws IllegalAccessException {
        int leftLength = 0; //计算剩余长度
        int fieldOffset = 0;// 目标的位置
        Class<?> classType = obj.getClass();
        Field[] fields = XProtocolCenter.getInstance().getProtocolFields(classType);

        ProtocolField protocolField;
        Class<?> fieldType;
        for (Field field : fields) {
            field.setAccessible(true);
            protocolField = field.getAnnotation(ProtocolField.class);
            if (protocolField == null || !protocolField.isField()) {
                continue;
            }

            if (field.getName().equals(objFieldName)) {
                fieldOffset = leftLength;
                continue;
            }

            fieldType = field.getType();

            if (protocolField.length() > 0) {
                leftLength += protocolField.length();
            } else {
                if (byte.class.equals(fieldType)) {
                    leftLength += 1;
                } else if (short.class.equals(fieldType)) {
                    leftLength += getFieldLength(protocolField, SHORT_MAX_LENGTH);
                } else if (int.class.equals(fieldType)) {
                    leftLength += getFieldLength(protocolField, INT_MAX_LENGTH);
                } else if (long.class.equals(fieldType)) {
                    leftLength += getFieldLength(protocolField, LONG_MAX_LENGTH);
                } else if (byte[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        leftLength += ((byte[]) data).length;
                    }
                } else if (short[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        leftLength += ((short[]) data).length * getFieldLength(protocolField, SHORT_MAX_LENGTH);
                    }
                } else if (int[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        leftLength += ((int[]) data).length * getFieldLength(protocolField, INT_MAX_LENGTH);
                    }
                } else if (long[].class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        leftLength += ((long[]) data).length * getFieldLength(protocolField, LONG_MAX_LENGTH);
                    }
                } else if (String.class.equals(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        leftLength += ((String) data).getBytes(Charset.forName(protocolField.charset())).length;
                    }
                } else if (IProtocolItem.class.isAssignableFrom(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        leftLength += ((IProtocolItem) data).getProtocolLength();
                    }
                } else if (IProtocolItem[].class.isAssignableFrom(fieldType)) {
                    Object data = field.get(obj);
                    if (data != null) {
                        IProtocolItem[] items = (IProtocolItem[]) data;
                        for (IProtocolItem item : items) {
                            leftLength += item.getProtocolLength();
                        }
                    }
                } else {
                    XTLog.e("无法识别的字段类型，无法获取字段长度! 字段: " + getFieldInfo(classType, fieldType, field));
                }
            }
        }
        leftLength -= fieldOffset;
        return leftLength;
    }


    /**
     * 将传输的Byte数组解析为协议体
     *
     * @param obj        消息对象, 类需要被@Protocol修饰
     * @param bytes      数据集合[整个消息体数据，包含头和尾]
     * @param index      需要开始解析的索引
     * @param tailLength 消息尾的长度[和index一起决定了数据解析的范围]
     * @return 是否解析成功
     */
    public static boolean byte2ProtoBody(@NonNull Object obj, byte[] bytes, int index, int tailLength, StorageMode protocolMode) throws IllegalAccessException, InstantiationException {
        Class<?> classType = obj.getClass();

        Field[] fields = XProtocolCenter.getInstance().getProtocolFields(classType);
        if (fields == null) {
            return false;
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
                XTLog.e("剩余长度已不足以解析读取，直接结束! 字段: " + getFieldInfo(classType, field.getType(), field));
                return false;
            }

            protocolField = field.getAnnotation(ProtocolField.class);
            if (protocolField == null || !protocolField.isField()) {
                continue;
            }

            fieldType = field.getType();
            mode = getStorageMode(protocolMode, protocolField.mode());

            // 开始各种类型的处理
            if (byte.class.equals(fieldType)) {
                field.set(obj, bytes[offset]);
                offset += 1;
            } else if (short.class.equals(fieldType)) {
                length = getFieldLength(protocolField, SHORT_MAX_LENGTH);
                field.set(obj, ConvertUtils.bytesToShort(mode, bytes, offset, length));
                offset += length;
            } else if (int.class.equals(fieldType)) {
                length = getFieldLength(protocolField, INT_MAX_LENGTH);
                field.set(obj, ConvertUtils.bytesToInt(mode, bytes, offset, length));
                offset += length;
            } else if (long.class.equals(fieldType)) {
                length = getFieldLength(protocolField, LONG_MAX_LENGTH);
                field.set(obj, ConvertUtils.bytesToLong(mode, bytes, offset, length));
                offset += length;
            } else if (IArrayItem.class.isAssignableFrom(fieldType)) {  // 使用了包装类定义了数组的长度
                IArrayItem arrayItem = (IArrayItem) fieldType.newInstance();
                length = arrayItem.fillArrayLength(bytes, offset, mode); //填充数组长度字段
                offset += length;
                if (arrayItem.byte2proto(bytes, offset, tailLength, mode)) {
                    field.set(obj, arrayItem);
                    offset += arrayItem.getProtocolLength() - length;
                } else {
                    XTLog.e("IArrayItem.class 数组解析失败，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }
            } else if (IProtocolItem.class.isAssignableFrom(fieldType)) {
                IProtocolItem item = (IProtocolItem) field.get(obj);
                if (item == null) {
                    item = (IProtocolItem) fieldType.newInstance();
                }
                if (item.byte2proto(bytes, offset, tailLength, mode)) {
                    field.set(obj, item);
                    offset += item.getProtocolLength();
                } else {
                    XTLog.e("IProtocolItem.class 自定义协议项解析失败，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }
            } else if (byte[].class.isAssignableFrom(fieldType)) {
                int leftParseLength = getLeftArrayParseLength(obj, bytes, tailLength, offset, field);
                if (leftParseLength > 0) {
                    byte[] temp = new byte[leftParseLength];
                    System.arraycopy(bytes, offset, temp, 0, temp.length);
                    offset += temp.length;
                    field.set(obj, temp);
                } else {
                    XTLog.e("剩余长度无法解析，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }
            } else if (short[].class.isAssignableFrom(fieldType)) {
                int leftParseLength = getLeftArrayParseLength(obj, bytes, tailLength, offset, field);
                if (leftParseLength > 0) {
                    length = getFieldLength(protocolField, SHORT_MAX_LENGTH);
                    short[] temp = new short[leftParseLength / length];
                    for (int i = 0; i < temp.length; i++) {
                        temp[i] = ConvertUtils.bytesToShort(mode, bytes, offset, length);
                        offset += length;
                    }
                    field.set(obj, temp);
                } else {
                    XTLog.e("剩余长度无法解析，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }
            } else if (int[].class.isAssignableFrom(fieldType)) {
                int leftParseLength = getLeftArrayParseLength(obj, bytes, tailLength, offset, field);
                if (leftParseLength > 0) {
                    length = getFieldLength(protocolField, INT_MAX_LENGTH);
                    int[] temp = new int[leftParseLength / length];
                    for (int i = 0; i < temp.length; i++) {
                        temp[i] = ConvertUtils.bytesToInt(mode, bytes, offset, length);
                        offset += length;
                    }
                    field.set(obj, temp);
                } else {
                    XTLog.e("剩余长度无法解析，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }

            } else if (long[].class.isAssignableFrom(fieldType)) {
                int leftParseLength = getLeftArrayParseLength(obj, bytes, tailLength, offset, field);
                if (leftParseLength > 0) {
                    length = getFieldLength(protocolField, LONG_MAX_LENGTH);
                    long[] temp = new long[leftParseLength / length];
                    for (int i = 0; i < temp.length; i++) {
                        temp[i] = ConvertUtils.bytesToLong(mode, bytes, offset, length);
                        offset += length;
                    }
                    field.set(obj, temp);
                } else {
                    XTLog.e("剩余长度无法解析，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }
            } else if (String.class.isAssignableFrom(fieldType)) {
                int leftParseLength = getLeftArrayParseLength(obj, bytes, tailLength, offset, field);
                if (leftParseLength > 0) {
                    byte[] temp = new byte[leftParseLength];
                    System.arraycopy(bytes, offset, temp, 0, temp.length);
                    offset += temp.length;
                    field.set(obj, new String(temp, Charset.forName(protocolField.charset())));
                } else {
                    XTLog.e("剩余长度无法解析，错误字段: " + getFieldInfo(classType, fieldType, field));
                    return false;
                }
            } else {
                XTLog.e("无法识别的解析类型，错误字段: " + getFieldInfo(classType, fieldType, field));
                return false;
            }
        }
        return true;
    }

    /**
     * 对于没有实现IArrayItem接口的数组，因为不知道数组的长度，因此只能根据当前解析的位置以及计算该字段之后的协议字段所需要的长度，估测该数组的长度<br>
     * [注意，这样的数组只能有且只能有一个]
     *
     * @param obj
     * @param bytes      解析的数组
     * @param tailLength 包尾长度[data之后的长度]
     * @param offset     解析的偏移
     * @param field      解析的字段
     * @return
     * @throws IllegalAccessException
     */
    private static int getLeftArrayParseLength(@NonNull Object obj, byte[] bytes, int tailLength, int offset, Field field) throws IllegalAccessException {
        return bytes.length - offset - tailLength - calculateLeftFieldLength(obj, field.getName());
    }

    /**
     * 将协议体转为传输的Byte数组
     *
     * @param obj 消息对象, 类需要被@Protocol修饰
     * @return
     */
    public static byte[] protoBody2Byte(@NonNull Object obj, StorageMode protocolMode) throws IllegalAccessException {
        byte[] res = new byte[0];

        Class<?> classType = obj.getClass();
        Field[] fields = XProtocolCenter.getInstance().getProtocolFields(classType);
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
                XTLog.d("未赋值的直接跳过, 字段: " + getFieldInfo(classType, fieldType, field));
                continue;
            }

            // 开始各种类型的处理
            if (byte.class.equals(fieldType)) {
                res[offset] = (byte) value;
                offset += 1;
            } else if (short.class.equals(fieldType)) {
                length = getFieldLength(protocolField, SHORT_MAX_LENGTH);
                ConvertUtils.fillShortToBytes(mode, (short) value, res, offset, length);
                offset += length;
            } else if (int.class.equals(fieldType)) {
                length = getFieldLength(protocolField, INT_MAX_LENGTH);
                ConvertUtils.fillIntToBytes(mode, (int) value, res, offset, length);
                offset += length;
            } else if (long.class.equals(fieldType)) {
                length = getFieldLength(protocolField, LONG_MAX_LENGTH);
                ConvertUtils.fillLongToBytes(mode, (long) value, res, offset, length);
                offset += length;
            } else if (byte[].class.equals(fieldType)) {
                byte[] tmp = (byte[]) value;
                System.arraycopy(tmp, 0, res, offset, tmp.length);
                offset += tmp.length;
            } else if (short[].class.equals(fieldType)) {
                length = getFieldLength(protocolField, SHORT_MAX_LENGTH);
                short[] tmp = (short[]) value;
                for (short s : tmp) {
                    ConvertUtils.fillShortToBytes(mode, s, res, offset, length);
                    offset += length;
                }
            } else if (int[].class.equals(fieldType)) {
                length = getFieldLength(protocolField, INT_MAX_LENGTH);
                int[] tmp = (int[]) value;
                for (int i : tmp) {
                    ConvertUtils.fillIntToBytes(mode, i, res, offset, length);
                    offset += length;
                }
            } else if (long[].class.equals(fieldType)) {
                length = getFieldLength(protocolField, LONG_MAX_LENGTH);
                long[] tmp = (long[]) value;
                for (long l : tmp) {
                    ConvertUtils.fillLongToBytes(mode, l, res, offset, length);
                    offset += length;
                }
            } else if (String.class.equals(fieldType)) {
                byte[] tmp = ((String) value).getBytes(Charset.forName(protocolField.charset()));
                System.arraycopy(tmp, 0, res, offset, tmp.length);
                offset += tmp.length;
            } else if (IProtocol.class.isAssignableFrom(fieldType)) {
                IProtocol item = (IProtocol) value;
                byte[] tmp = item.proto2byte(mode);
                System.arraycopy(tmp, 0, res, offset, tmp.length);
                offset += tmp.length;
            } else if (IProtocol[].class.isAssignableFrom(fieldType)) {
                IProtocol[] items = (IProtocol[]) value;
                byte[] tmp;
                for (IProtocol item : items) {
                    tmp = item.proto2byte(mode);
                    System.arraycopy(tmp, 0, res, offset, tmp.length);
                    offset += tmp.length;
                }
            } else {
                XTLog.e("无法识别的字段类型，错误字段: " + getFieldInfo(classType, fieldType, field) + ", 字段值:" + value);
            }
        }
        return res;
    }

    /**
     * 获取字段信息
     *
     * @param classType
     * @param fieldType
     * @param field
     * @return
     */
    @NonNull
    private static String getFieldInfo(Class<?> classType, Class<?> fieldType, Field field) {
        return classType.getCanonicalName() + "[" + fieldType.getName() + "][" + field.getName() + "]";
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
