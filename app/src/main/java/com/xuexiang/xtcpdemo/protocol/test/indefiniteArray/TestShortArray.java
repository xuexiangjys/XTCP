package com.xuexiang.xtcpdemo.protocol.test.indefiniteArray;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.XProtocolItem;

import java.util.Arrays;

import static com.xuexiang.xtcp.model.ProtocolInfo.byte2HexString;

/**
 * 不定长数组测试【short】
 *
 * @author xuexiang
 * @since 2018/12/24 上午9:25
 */
public class TestShortArray extends XProtocolItem {

    @ProtocolField(index = 0)
    private byte func1;
    @ProtocolField(index = 1)
    private short func2;
    @ProtocolField(index = 2)
    private int func3;
    @ProtocolField(index = 3)
    private long func4;
    @ProtocolField(index = 4)
    private short[] shorts;


    public TestShortArray setFunc1(byte func1) {
        this.func1 = func1;
        return this;
    }

    public TestShortArray setFunc2(short func2) {
        this.func2 = func2;
        return this;
    }

    public TestShortArray setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public TestShortArray setFunc4(long func4) {
        this.func4 = func4;
        return this;
    }

    public TestShortArray setShorts(short[] shorts) {
        this.shorts = shorts;
        return this;
    }

    @Override
    public String toString() {
        return "TestShortArray{" +
                "func1=" + byte2HexString(func1) +
                ", func2=" + func2 +
                ", shorts=" + Arrays.toString(shorts) +
                ", func3=" + func3 +
                ", func4=" + func4 +
                '}';
    }
}
