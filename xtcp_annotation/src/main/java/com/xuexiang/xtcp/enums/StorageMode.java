package com.xuexiang.xtcp.enums;

/**
 * 数据的存储方式
 *
 * @author xuexiang
 * @since 2018/12/10 下午3:35
 */
public enum StorageMode {

    /**
     * 大端存储（默认符合我们正常的习惯）
     */
    BigEndian,
    /**
     * 小端存储
     */
    LittleEndian,

    /**
     * 使用全局设置的默认方式
     */
    Default

}
