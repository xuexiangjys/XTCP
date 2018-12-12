package com.xuexiang.xtcp.model;

import com.xuexiang.xtcp.enums.StorageMode;

/**
 * 协议信息
 *
 * @author xuexiang
 * @since 2018/12/11 上午12:40
 */
public class ProtocolInfo {

    /**
     * 协议的名称
     */
    private String mName;

    /**
     * 协议的类名
     */
    private String mClassName;

    /**
     * 协议的命令码
     */
    private byte mOpCode;

    /**
     * 协议的响应码（结果码）
     */
    private byte mResCode;

    /**
     * 数据的存储方式
     */
    private StorageMode mStorageMode;

    /**
     * 协议的描述信息
     */
    private String mDescription;

    public ProtocolInfo(String name, String className, byte opCode, byte resCode, StorageMode mode, String description) {
        mName = name;
        mClassName = className;
        mOpCode = opCode;
        mResCode = resCode;
        mStorageMode = mode;
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    /**
     * @return 获取协议的类名
     */
    public String getClassName() {
        return mClassName;
    }

    /**
     * @return 获取协议的命令码
     */
    public byte getOpCode() {
        return mOpCode;
    }

    public byte getResCode() {
        return mResCode;
    }

    /**
     * @return 获取协议的数据存储方式
     */
    public StorageMode getStorageMode() {
        return mStorageMode;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public String toString() {
        return "ProtocolInfo{" +
                "mName='" + mName + '\'' +
                ", mClassName='" + mClassName + '\'' +
                ", mOpCode=" + byte2HexString(mOpCode) +
                ", mResCode=" + byte2HexString(mResCode) +
                ", mStorageMode=" + mStorageMode +
                ", mDescription='" + mDescription + '\'' +
                '}';
    }

    /**
     * 将byte转为16进制便于查看
     *
     * @param value
     * @return
     */
    private String byte2HexString(byte value) {
        int v = value & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            hv = "0" + hv;
        }
        return "0x" + hv;
    }
}
