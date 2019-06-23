package com.xuexiang.xtcpdemo.protocol.test.indefiniteArray;

import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.model.XProtocolItem;

import java.util.Arrays;

import static com.xuexiang.xtcp.model.ProtocolInfo.byte2HexString;

/**
 * 不定长数组测试【long】
 *
 * @author xuexiang
 * @since 2018/12/24 上午10:11
 */
public class TestLongArray extends XProtocolItem {

    @ProtocolField(index = 0)
    private byte func1;
    @ProtocolField(index = 1)
    private short func2;
    @ProtocolField(index = 2, unsigned = false)
    private long[] longs;
    @ProtocolField(index = 3)
    private int func3;
    @ProtocolField(index = 4)
    private long func4;

    public TestLongArray setFunc1(byte func1) {
        this.func1 = func1;
        return this;
    }

    public TestLongArray setFunc2(short func2) {
        this.func2 = func2;
        return this;
    }

    public TestLongArray setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public TestLongArray setFunc4(long func4) {
        this.func4 = func4;
        return this;
    }

    public TestLongArray setLongs(long[] longs) {
        this.longs = longs;
        return this;
    }

    @Override
    public String toString() {
        return "TestLongArray{" +
                "func1=" + byte2HexString(func1) +
                ", func2=" + func2 +
                ", longs=" + Arrays.toString(longs) +
                ", func3=" + func3 +
                ", func4=" + func4 +
                '}';
    }
}
