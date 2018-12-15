package com.xuexiang.xtcpdemo.protocol;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.IntArray;
import com.xuexiang.xtcp.core.model.XProtocolItem;

import static com.xuexiang.xtcp.model.ProtocolInfo.byte2HexString;

/**
 * @author xuexiang
 * @since 2018/12/15 下午6:37
 */
public class TestProtocolItem extends XProtocolItem {

    @ProtocolField(index = 0)
    private byte func1;
    @ProtocolField(index = 1)
    private short func2;
    @ProtocolField(index = 2)
    private int func3;
    @ProtocolField(index = 3)
    private long func4;
    @ProtocolField(index = 4)
    private IntArray list;
    @ProtocolField(index = 5)
    private LoginInfo loginInfo;

    public TestProtocolItem setFunc1(byte func1) {
        this.func1 = func1;
        return this;
    }

    public TestProtocolItem setFunc2(short func2) {
        this.func2 = func2;
        return this;
    }

    public TestProtocolItem setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public TestProtocolItem setFunc4(long func4) {
        this.func4 = func4;
        return this;
    }

    public TestProtocolItem setList1(int... list) {
        this.list = new IntArray(list);
        return this;
    }

    public TestProtocolItem setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        return this;
    }

    @Override
    public String toString() {
        return "TestProtocolItem{" +
                "func1=" + byte2HexString(func1) +
                ", func2=" + func2 +
                ", func3=" + func3 +
                ", func4=" + func4 +
                ", list=" + list +
                ", LoginInfo=" + loginInfo +
                '}';
    }
}
