package com.xuexiang.xtcpdemo.protocol;

import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.BCD;
import com.xuexiang.xtcp.core.model.ByteArray;
import com.xuexiang.xtcp.core.model.FixedString;
import com.xuexiang.xtcp.core.model.IntArray;
import com.xuexiang.xtcp.core.model.LargeString;
import com.xuexiang.xtcp.core.model.LongArray;
import com.xuexiang.xtcp.core.model.ShortArray;
import com.xuexiang.xtcp.core.model.StringField;
import com.xuexiang.xtcp.core.model.XProtocolItem;
import com.xuexiang.xtcpdemo.model.LoginInfo;
import com.xuexiang.xtcpdemo.model.LoginInfoArray;
import com.xuexiang.xtcpdemo.protocol.test.TestProtocolItem;

import java.util.Date;

import static com.xuexiang.xtcp.model.ProtocolInfo.byte2HexString;

/**
 * @author xuexiang
 * @since 2018/12/11 上午10:15
 */
@Protocol(name = "参数设置请求", opCode = 0x12, resCode = 0x33, desc = "注意重启下位机后生效！")
public class SettingRequest extends XProtocolItem {

    @ProtocolField(index = 0)
    private byte func1;
    @ProtocolField(index = 1)
    private short func2;
    @ProtocolField(index = 2)
    private int func3;
    @ProtocolField(index = 3)
    private long func4;
    @ProtocolField(index = 4)
    private ByteArray list1;
    @ProtocolField(index = 5)
    private ShortArray list2;
    @ProtocolField(index = 6)
    private IntArray list3;
    @ProtocolField(index = 7)
    private LongArray list4;
    @ProtocolField(index = 8)
    private StringField string1;
    @ProtocolField(index = 9)
    private LargeString string2;
    @ProtocolField(index = 10)
    private TestProtocolItem testItem;
    @ProtocolField(index = 11)
    private LoginInfo loginInfo;
    @ProtocolField(index = 12)
    private BCD<Date> time = new BCD<>(Date.class, "yy-MM-dd HH:mm:ss");
    @ProtocolField(index = 13)
    private FixedString ID = new FixedString(10);
    @ProtocolField(index = 14)
    private LoginInfoArray loginInfos;

    public SettingRequest() {

    }

    public SettingRequest setList1(byte... list) {
        this.list1 = ByteArray.wrap(list);
        return this;
    }

    public SettingRequest setList2(short... list) {
        this.list2 = ShortArray.wrap(list);
        return this;
    }

    public SettingRequest setList3(int... list) {
        this.list3 = IntArray.wrap(list);
        return this;
    }

    public SettingRequest setList4(long... list) {
        this.list4 = LongArray.wrap(list);
        return this;
    }

    public SettingRequest setFunc1(byte func1) {
        this.func1 = func1;
        return this;
    }

    public SettingRequest setFunc2(short func2) {
        this.func2 = func2;
        return this;
    }

    public SettingRequest setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public SettingRequest setFunc4(long func4) {
        this.func4 = func4;
        return this;
    }

    public SettingRequest setString1(String s) {
        this.string1 = new StringField(s);
        return this;
    }

    public SettingRequest setString2(String s) {
        this.string2 = LargeString.wrap(s);
        return this;
    }

    public SettingRequest setTestItem(TestProtocolItem item) {
        this.testItem = item;
        return this;
    }

    public SettingRequest setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        return this;
    }

    public SettingRequest setTime(Date date) {
        time.setValue(date);
        return this;
    }

    public SettingRequest setID(String id) {
        ID.setFixedString(id);
        return this;
    }

    public SettingRequest setLoginInfos(LoginInfo... loginInfos) {
        this.loginInfos= LoginInfoArray.wrap(loginInfos);
        return this;
    }

    @Override
    public String toString() {
        return "SettingRequest{" +
                "func1=" + byte2HexString(func1) +
                ", func2=" + func2 +
                ", func3=" + func3 +
                ", func4=" + func4 +
                ", list1=" + list1 +
                ", list2=" + list2 +
                ", list3=" + list3 +
                ", list4=" + list4 +
                ", string1=" + string1 +
                ", string2=" + string2 +
                ", testItem=" + testItem +
                ", loginInfo=" + loginInfo +
                ", time=" + time.getFormatValue() +
                ", ID=" + ID.getFixedString() +
                ", loginInfos=" + loginInfos +
                '}';
    }
}
