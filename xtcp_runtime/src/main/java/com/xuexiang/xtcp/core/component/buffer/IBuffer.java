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

package com.xuexiang.xtcp.core.component.buffer;

/**
 * 缓冲区的实现接口
 *
 * @author xuexiang
 * @since 2018/12/17 下午1:09
 */
public interface IBuffer {

    /**
     * 向缓冲区中添加数据
     *
     * @param data      要添加的数据
     * @param available 添加数据的长度
     * @throws BufferException
     */
    void putData(byte[] data, int available) throws BufferException;

    /**
     * 从缓冲区中取出所有有效数据
     *
     * @return 缓冲区中有效区间的数据
     */
    byte[] getData() throws BufferException;

    /**
     * 从缓冲区中取出指定长度的数据
     *
     * @param dataLength 数据长度
     * @return 指定长度的数据
     */
    byte[] getData(int dataLength) throws BufferException;

    /**
     * 重置缓冲区，不释放缓冲区内存空间
     */
    void clear();

    /**
     * 针对缓冲区中有效数据的空间释放
     *
     * @param length
     * @throws BufferException
     */
    void release(int length) throws BufferException;

    /**
     * @return 获取当前缓冲区中存储有效数据的长度
     */
    int getValidDataLength();

    /**
     * 回收资源
     */
    void recycle();
}
