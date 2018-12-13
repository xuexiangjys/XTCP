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
import com.xuexiang.xtcp._XTCP;
import com.xuexiang.xtcp.core.XProtocolCenter;
import com.xuexiang.xtcp.core.entity.IntArray;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.utils.ConvertUtils;
import com.xuexiang.xtcpdemo.protocol.SettingRequest;
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

        SettingRequest request = new SettingRequest().setList(11,22,33);
        byte[] bytes = request.proto2byte(StorageMode.Default);
        Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));

        SettingRequest request1 = new SettingRequest();
        request1.byte2proto(bytes, 0, 0, StorageMode.Default);

        Log.e("xuexiang", Arrays.toString(request1.getList()));

    }
}
