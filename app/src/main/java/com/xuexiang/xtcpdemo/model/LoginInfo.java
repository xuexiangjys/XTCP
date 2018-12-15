package com.xuexiang.xtcpdemo.model;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.StringField;
import com.xuexiang.xtcp.core.model.XProtocolItem;

/**
 * 登陆信息协议项
 *
 * @author xuexiang
 * @since 2018/12/15 下午6:53
 */
public class LoginInfo extends XProtocolItem {


    @ProtocolField(index = 0)
    private StringField loginName;

    @ProtocolField(index = 1)
    private StringField password;

    public LoginInfo() {

    }

    public LoginInfo(String loginName, String password) {
        setLoginName(loginName);
        setPassword(password);
    }

    public LoginInfo setLoginName(String loginName) {
        this.loginName = StringField.wrap(loginName);
        return this;
    }

    public LoginInfo setPassword(String password) {
        this.password = StringField.wrap(password);
        return this;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "loginName=" + loginName +
                ", password=" + password +
                '}';
    }
}
