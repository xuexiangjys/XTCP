package com.xuexiang.xtcpdemo.fragment;

import android.util.Log;

import com.xuexiang.xaop.annotation.DebugLog;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xtcp.core.buffer.BufferException;
import com.xuexiang.xtcp.core.buffer.IBuffer;
import com.xuexiang.xtcp.core.buffer.impl.CircularBuffer;
import com.xuexiang.xtcp.core.message.IMessage;
import com.xuexiang.xtcp.core.message.template.XMessage;
import com.xuexiang.xtcp.core.message.template.XOrderlyMessage;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolItem;
import com.xuexiang.xtcp.utils.ConvertUtils;
import com.xuexiang.xtcp.utils.MessageUtils;
import com.xuexiang.xtcpdemo.model.LoginInfo;
import com.xuexiang.xtcpdemo.protocol.SettingRequest;
import com.xuexiang.xtcpdemo.protocol.test.MessageTest;
import com.xuexiang.xtcpdemo.protocol.test.TestProtocolItem;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xuexiang
 * @since 2018/12/15 下午11:51
 */
@Page(name = "测试")
public class TestFragment extends XPageSimpleListFragment {

    private IBuffer buffer;

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("测试byte化和反byte化");
        lists.add("测试无序消息XMessage包装");
        lists.add("测试有序消息XOrderlyMessage包装");
        lists.add("测试多个消息包的解析");
        lists.add("性能测试");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        switch(position) {
            case 0:
                test();
                break;
            case 1:
                testXMessage();
                break;
            case 2:
                testXOrderlyMessage();
                break;
            case 3:
                testMultiMessage();
                break;
            case 4:
                long time = 0;
                for (int i = 0; i < 30; i++) {
                    time += test();
                }
                ToastUtils.toast("平均耗时：" + time / 30 + "毫秒");

                break;
            default:
                break;
        }
    }

    @DebugLog
    private long test() {
        long startNanos = System.nanoTime();
        SettingRequest request = new SettingRequest()
                .setFunc1((byte) 0x45)
                .setFunc2((short) 12)
                .setFunc3(2345)
                .setFunc4((long) 1213131233)
                .setList1((byte) 0x23, (byte) 0x45, (byte) 0x56)
                .setList2((short) 11, (short) 22, (short) 33)
                .setList3(111, 222, 333)
                .setList4((long) 1221312, (long) 23123123)
                //长度超过255的话，就溢出了
                .setString1("我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！")
                .setString2("我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                        "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                        "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                        "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                        "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                        "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                        "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！")
                .setLoginInfo(new LoginInfo("xuexiang", "123456"))
                .setLoginInfos(new LoginInfo("xuexiang1", "222"),
                        new LoginInfo("xuexiang23", "3333"),
                        new LoginInfo("xuexiang456", "44444"))
                .setTestItem(new TestProtocolItem()
                        .setFunc1((byte) 0x56)
                        .setFunc2((short) 314)
                        .setFunc3(6111)
                        .setFunc4((long) 35536234)
                        .setList1(314, 334, 34235, 67584, 45234, 6757)
                        .setLoginInfo(new LoginInfo("xuexiangjys", "111111")));
        byte[] bytes = request.proto2byte(StorageMode.Default);
        Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));

        SettingRequest request1 = new SettingRequest();
        request1.byte2proto(bytes, 0, 0, StorageMode.Default);

        Log.e("xuexiang", request1.toString());
        long stopNanos = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
    }

    @DebugLog
    private void testXMessage() {

        MessageTest messageTest = new MessageTest()
                .setFunc1((byte) 0x45)
                .setFunc2((short) 12)
                .setFunc3(2345)
                .setFunc4((long) 1213131233)
                .setList2((short) 11, (short) 22, (short) 33)
                .setLoginInfo(new LoginInfo("xuexiang", "123456"));

        XMessage message = getXMessage(messageTest);
        byte[] bytes = message.msg2Byte();
        Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));


        XMessage message1 = new XMessage();
        boolean result = message1.byte2Msg(bytes);

        Log.e("xuexiang", "result:" + result +", ProtocolItem:" + message1.getProtocolItem());

    }

    @DebugLog
    private void testXOrderlyMessage() {

        MessageTest messageTest = new MessageTest()
                .setFunc1((byte) 0x45)
                .setFunc2((short) 12)
                .setFunc3(2345)
                .setFunc4((long) 1213131233)
                .setList2((short) 11, (short) 22, (short) 33)
                .setLoginInfo(new LoginInfo("xuexiang", "123456"));

        XOrderlyMessage message = getXOrderlyMessage(messageTest, 23);
        byte[] bytes = message.msg2Byte();
        Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));


        XOrderlyMessage message1 = new XOrderlyMessage();
        boolean result = message1.byte2Msg(bytes);

        Log.e("xuexiang", "result:" + result +", ProtocolItem:" + message1.getProtocolItem());

    }

    @DebugLog
    private void testMultiMessage() {
        if (buffer == null) {
            buffer = new CircularBuffer();
        }

        MessageTest messageTest = new MessageTest()
                .setFunc1((byte) 0x45)
                .setFunc2((short) 12)
                .setFunc3(2345)
                .setFunc4((long) 1213131233)
                .setList2((short) 11, (short) 22, (short) 33)
                .setLoginInfo(new LoginInfo("xuexiang", "123456"));
        XOrderlyMessage message = getXOrderlyMessage(messageTest, 23);
        byte[] bytes = message.msg2Byte();
        try {
            for (int i = 0; i < 10; i++) {
                buffer.putData(bytes, bytes.length);
                buffer.putData(new byte[]{(byte)12, (byte)23, (byte)45}, 3); //添加入乱数据
            }

            bytes = buffer.getData();
            Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));

            List<IMessage> result = MessageUtils.parseMessageBytes(XOrderlyMessage.class, bytes);

            Log.e("xuexiang", "size:" + result.size() + ", result:" + result.get(6).getProtocolItem());

            buffer.clear();
        } catch (BufferException e) {
            e.printStackTrace();
        }
    }

    private XMessage getXMessage(IProtocolItem protocolItem) {
        return new XMessage().setIProtocolItem(protocolItem);
    }

    private XOrderlyMessage getXOrderlyMessage(IProtocolItem protocolItem, int msgID) {
        return new XOrderlyMessage()
                .setIProtocolItem(protocolItem)
                .setMsgID(msgID);
    }
}
