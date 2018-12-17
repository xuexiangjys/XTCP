package com.xuexiang.xtcp.core;

import android.support.annotation.NonNull;

import com.xuexiang.xtcp.model.IProtocolCenter;
import com.xuexiang.xtcp.model.IProtocolFieldCenter;
import com.xuexiang.xtcp.model.ProtocolFieldInfo;
import com.xuexiang.xtcp.model.ProtocolInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议管理中心
 *
 * @author xuexiang
 * @since 2018/12/12 下午5:06
 */
public class XProtocolCenter implements IProtocolCenter, IProtocolFieldCenter {


    private static volatile XProtocolCenter sInstance = null;

    /**
     * 协议类名 -> 协议信息
     */
    private Map<String, ProtocolInfo> mClass2Info = new HashMap<>();

    /**
     * opcode -> 协议信息
     */
    private Map<Byte, ProtocolInfo> mOpCode2Info = new HashMap<>();

    /**
     * 类名 -> 协议字段名集合
     */
    private Map<String, ProtocolFieldInfo> mClass2Fields = new HashMap<>();

    private XProtocolCenter() {

    }

    /**
     * 获取单例
     *
     * @return
     */
    public static XProtocolCenter getInstance() {
        if (sInstance == null) {
            synchronized (XProtocolCenter.class) {
                if (sInstance == null) {
                    sInstance = new XProtocolCenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 设置协议中心
     *
     * @param iProtocolCenters 协议中心
     * @return
     */
    public XProtocolCenter addIProtocolCenter(@NonNull IProtocolCenter... iProtocolCenters) {
        for (IProtocolCenter iProtocolCenter : iProtocolCenters) {
            addIProtocolCenter(iProtocolCenter);
        }
        return this;
    }

    /**
     * 设置协议中心
     *
     * @param iProtocolCenter 协议中心
     * @return
     */
    public XProtocolCenter addIProtocolCenter(@NonNull IProtocolCenter iProtocolCenter) {
        mClass2Info.putAll(iProtocolCenter.getClass2Info());
        mOpCode2Info.putAll(iProtocolCenter.getOpCode2Info());
        return this;
    }

    /**
     * 设置协议字段中心
     *
     * @param iIProtocolFieldCenters 协议字段中心
     * @return
     */
    public XProtocolCenter addIProtocolFieldCenter(@NonNull IProtocolFieldCenter... iIProtocolFieldCenters) {
        for (IProtocolFieldCenter iProtocolFieldCenter : iIProtocolFieldCenters) {
            addIProtocolFieldCenter(iProtocolFieldCenter);
        }
        return this;
    }

    /**
     * 设置协议字段中心
     *
     * @param iIProtocolFieldCenter 协议字段中心
     * @return
     */
    public XProtocolCenter addIProtocolFieldCenter(@NonNull IProtocolFieldCenter iIProtocolFieldCenter) {
        mClass2Fields.putAll(iIProtocolFieldCenter.getClass2Fields());
        return this;
    }


    /**
     * 根据协议的类名获取协议的详细信息
     */
    @Override
    public ProtocolInfo getProtocol(final String className) {
        return mClass2Info.get(className);
    }

    /**
     * 根据opcode获取协议的详细信息
     */
    @Override
    public ProtocolInfo getProtocol(final byte opcode) {
        return mOpCode2Info.get(opcode);
    }

    /**
     * 根据协议的类名获取对应的OpCode
     */
    @Override
    public byte getOpCodeByClassName(final String className) {
        return mClass2Info.get(className).getOpCode();
    }

    /**
     * 根据类名获取协议字段名集合
     */
    @Override
    public Field[] getProtocolFields(final String className) {
        return mClass2Fields.get(className).getFields();
    }

    @Override
    public Map<String, ProtocolInfo> getClass2Info() {
        return mClass2Info;
    }

    @Override
    public Map<Byte, ProtocolInfo> getOpCode2Info() {
        return mOpCode2Info;
    }

    @Override
    public Map<String, ProtocolFieldInfo> getClass2Fields() {
        return mClass2Fields;
    }

    /**
     * 根据类名获取协议字段
     *
     * @param cls
     * @return
     */
    public Field[] getProtocolFields(@NonNull Class<?> cls) {
        return getProtocolFields(cls.getCanonicalName());
    }

    /**
     * 根据opCode获取协议类名
     *
     * @param opCode
     * @return
     */
    public String getClassNameByOpCode(@NonNull byte opCode) {
        return mOpCode2Info.get(opCode).getClassName();
    }
}
