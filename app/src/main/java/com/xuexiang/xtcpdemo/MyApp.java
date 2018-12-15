package com.xuexiang.xtcpdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xuexiang.xaop.XAOP;
import com.xuexiang.xaop.util.PermissionUtils;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xtcp.AppProtocolCenter;
import com.xuexiang.xtcp.AppProtocolFieldCenter;
import com.xuexiang.xtcp.XTCP;
import com.xuexiang.xtcp.XTCPProtocolFieldCenter;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.core.model.IntArray;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;
import com.xuexiang.xtcpdemo.protocol.LoginInfo;
import com.xuexiang.xtcpdemo.protocol.SettingRequest;
import com.xuexiang.xtcpdemo.protocol.TestProtocolItem;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:12
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLibs();

        initXTCP();
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        XUtil.init(this);
        XUtil.debug(true);

        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() { //页面注册
            @Override
            public List<PageInfo> registerPages(Context context) {
                return AppPageConfig.getInstance().getPages(); //自动注册页面
            }
        }).debug("PageLog").enableWatcher(true).init(this);

        XAOP.init(this); //初始化插件
        XAOP.debug(true); //日志打印切片开启
        //设置动态申请权限切片 申请权限被拒绝的事件响应监听
        XAOP.setOnPermissionDeniedListener(new PermissionUtils.OnPermissionDeniedListener() {
            @Override
            public void onDenied(List<String> permissionsDenied) {
                ToastUtils.toast("权限申请被拒绝:" + StringUtils.listToString(permissionsDenied, ","));
            }

        });
    }

    private void initXTCP() {
        XTCP.getInstance()
                .setIProtocolCenter(AppProtocolCenter.getInstance())
                .setIProtocolFieldCenter(AppProtocolFieldCenter.getInstance(), XTCPProtocolFieldCenter.getInstance());

        Log.e("xuexiang", XProtocolCenter.getInstance().getProtocol((byte) 0x12).toString());

        Log.e("xuexiang", Arrays.toString(XProtocolCenter.getInstance().getProtocolFields(IntArray.class.getName())));

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

    }
}
