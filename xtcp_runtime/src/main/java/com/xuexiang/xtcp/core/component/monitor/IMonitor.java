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

package com.xuexiang.xtcp.core.component.monitor;

/**
 * 监控器，用于监控设备的状态
 *
 * @author xuexiang
 * @since 2019-07-23 23:54
 */
public interface IMonitor {

    /**
     * 开始监听工作
     */
    void startWork();

    /**
     * 结束监听工作
     */
    void finishWork();

    /**
     * 处理监听工作
     */
    void processWork();

    /**
     * 处理监听工作，并更改监听对象
     *
     * @param targetName
     */
    void processWork(String targetName);

    /**
     * 处理监听工作，并更改监听对象和监听间期
     *
     * @param targetName
     */
    void processWork(String targetName, long interval);

    /**
     * @return 是否在工作
     */
    boolean isWorking();

    /**
     * @return 监听对象的名称
     */
    String getTargetName();

    /**
     * 设置监控器的监听回调接口
     *
     * @param listener
     */
    IMonitor setOnMonitorListener(OnMonitorListener listener);

}
