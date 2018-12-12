package com.xuexiang.xtcp.model;

/**
 * 存储协议的字段名信息
 *
 * @author xuexiang
 * @since 2018/12/12 下午3:52
 */
public class ProtocolFieldInfo {

    private String[] mFields;

    public ProtocolFieldInfo(String fields) {
        mFields = fields.split(",");
    }

    public String[] getFields() {
        return mFields;
    }

}
