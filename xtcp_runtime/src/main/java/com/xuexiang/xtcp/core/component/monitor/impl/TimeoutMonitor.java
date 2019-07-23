/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xtcp.core.component.monitor.impl;

import com.xuexiang.xtcp.core.component.monitor.IMonitor;
import com.xuexiang.xtcp.core.component.monitor.OnMonitorListener;
import com.xuexiang.xtcp.logs.XTLog;

/**
 * 超时监控器
 *
 * @author xuexiang
 * @since 2019-07-23 23:54
 */
public class TimeoutMonitor extends Thread implements IMonitor {
    /**
     * 默认监听对象
     */
    public static final String DEFAULT_TARGET_NAME = "Device_Timeout";
    /**
     * 默认监控器监控间期
     */
    public static final long DEFAULT_MONITOR_INTERVAL = 2 * 1000;
    /**
     * 监控器是否在运行
     */
    private volatile boolean mIsMonitorRunning = false;
    /**
     * 记录超时的次数
     */
    private int mCount = 0;

    /**
     * 检查间期
     */
    private long mInterval;
    /**
     * 监听目标的名称
     */
    private String mTargetName;
    /**
     * 通信通道超时监听
     */
    private OnMonitorListener mOnMonitorListener;

    /**
     * 获得超时监控器
     *
     * @param interval
     * @return
     */
    public static TimeoutMonitor get(long interval) {
        return new TimeoutMonitor(interval);
    }

    /**
     * 构造方法
     *
     * @param interval 监控器检查的间期
     */
    public TimeoutMonitor(long interval) {
        this(DEFAULT_TARGET_NAME, interval);
    }

    /**
     * 构造方法
     *
     * @param targetName 监听目标的名称
     */
    public TimeoutMonitor(String targetName) {
        this(targetName, DEFAULT_MONITOR_INTERVAL);
    }

    /**
     * 构造方法
     *
     * @param targetName 监听目标的名称
     * @param interval   监控器检查的间期
     */
    public TimeoutMonitor(String targetName, long interval) {
        mTargetName = targetName;
        mInterval = interval;
    }

    @Override
    public void run() {
        while (mIsMonitorRunning) {
            mCount++;
            try {
                sleep(mInterval);
            } catch (InterruptedException e) {
                return;
            }
            if (mCount > 0 && mIsMonitorRunning) {
                if (mOnMonitorListener != null) {
                    mOnMonitorListener.onExcepted(this);
                }
                mIsMonitorRunning = false;
            }
        }
    }

    @Override
    public synchronized void startWork() {
        if (!mIsMonitorRunning) {
            XTLog.i("超时监控器已启动...");
            mIsMonitorRunning = true;
            super.start();
        }
    }

    @Override
    public void finishWork() {
        XTLog.i("超时监控器已停止...");
        mIsMonitorRunning = false;
        interrupt();
    }

    @Override
    public void processWork() {
        mCount = 0;
    }

    @Override
    public boolean isWorking() {
        return mIsMonitorRunning;
    }

    @Override
    public String getTargetName() {
        return mTargetName;
    }

    @Override
    public IMonitor setOnMonitorListener(OnMonitorListener listener) {
        mOnMonitorListener = listener;
        return this;
    }

}
