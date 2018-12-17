package com.xuexiang.xtcpdemo.protocol.test;

import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.ShortArray;
import com.xuexiang.xtcp.core.model.XProtocolItem;
import com.xuexiang.xtcpdemo.model.LoginInfo;

import static com.xuexiang.xtcp.model.ProtocolInfo.byte2HexString;

/**
 * @author xuexiang
 * @since 2018/12/17 上午12:36
 */
@Protocol(name = "测试消息包装", opCode = 0x34, resCode = 0x55)
public class MessageTest extends XProtocolItem {

    @ProtocolField(index = 0)
    private byte func1;
    @ProtocolField(index = 1)
    private short func2;
    @ProtocolField(index = 2)
    private int func3;
    @ProtocolField(index = 3)
    private long func4;
    @ProtocolField(index = 4)
    private ShortArray list2;
    @ProtocolField(index = 5)
    private LoginInfo loginInfo;

    public MessageTest setFunc1(byte func1) {
        this.func1 = func1;
        return this;
    }

    public MessageTest setFunc2(short func2) {
        this.func2 = func2;
        return this;
    }

    public MessageTest setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public MessageTest setFunc4(long func4) {
        this.func4 = func4;
        return this;
    }

    public MessageTest setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        return this;
    }

    public MessageTest setList2(short... list) {
        this.list2 = ShortArray.wrap(list);
        return this;
    }

    @Override
    public String toString() {
        return "MessageTest{" +
                "func1=" + byte2HexString(func1) +
                ", func2=" + func2 +
                ", func3=" + func3 +
                ", func4=" + func4 +
                ", list2=" + list2 +
                ", loginInfo=" + loginInfo +
                '}';
    }
}
