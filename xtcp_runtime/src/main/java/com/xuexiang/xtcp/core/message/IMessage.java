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

package com.xuexiang.xtcp.core.message;

import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocolItem;

/**
 * 消息体都需要实现该接口，消息体是承载协议项的容器
 *
 * @author xuexiang
 * @since 2018/12/16 下午11:19
 */
public interface IMessage {

    /**
     * 将消息实体转化为byte数组
     *
     * @return
     */
    byte[] msg2Byte(StorageMode storageMode);

    /**
     * 将byte数组数据转化为消息实体
     *
     * @param bytes 需要解析的byte数组数据
     * @return 是否解析成功
     */
    boolean byte2Msg(byte[] bytes, StorageMode storageMode);

    /**
     * 设置消息的协议项
     *
     * @param protocolItem 协议项
     * @param <T>
     * @return
     */
    <T extends IMessage> T setIProtocolItem(IProtocolItem protocolItem);

    /**
     * @return 消息的协议项
     */
    IProtocolItem getProtocolItem();

    /**
     * @return 消息体最小长度
     */
    int getMinMessageLength();
}
