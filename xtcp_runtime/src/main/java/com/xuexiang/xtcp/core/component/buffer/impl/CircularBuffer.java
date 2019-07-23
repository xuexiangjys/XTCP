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

package com.xuexiang.xtcp.core.component.buffer.impl;

import com.xuexiang.xtcp.core.component.buffer.BufferException;
import com.xuexiang.xtcp.core.component.buffer.IBuffer;

/**
 * 环形缓冲区，默认大小为2M
 *
 * @author xuexiang
 * @since 2018/12/17 下午1:09
 */
public class CircularBuffer implements IBuffer {
    /**
     * 默认缓冲区的长度为2M
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 * 1024 * 1024;
    /**
     * 缓冲区
     */
    private byte[] mBuffer;
    /**
     * 缓冲区中有效数据的起始索引
     */
    private int mStartIndex;
    /**
     * 缓冲区中有效数据的长度
     */
    private int mValidDataLength;

    /**
     * 构造方法
     */
    public CircularBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }

    /**
     * 构造方法
     *
     * @param bufferSize 缓冲区大小
     */
    public CircularBuffer(int bufferSize) {
        mBuffer = new byte[bufferSize];
        mStartIndex = 0;
        mValidDataLength = 0;
    }

    @Override
    public void putData(byte[] data, int available) throws BufferException {
        // 缓冲区溢出，抛出异常
        if (getBufferFreeSpace() < available) {
            throw new BufferException("data buffer overflow, must extend buffer size!");
        } else {
            int totalDataLength = mValidDataLength + available; //现有数据长度加上需要加入的数据长度
            // 当前有效数据没有分段
            if (isDataStoreContinuous()) {
                // 待存数据不需要分段
                if ((getIndexRightSpace()) >= totalDataLength) {
                    System.arraycopy(data, 0, mBuffer, mStartIndex + mValidDataLength, available);
                } else {// 数据需要分段
                    System.arraycopy(data, 0, mBuffer, mStartIndex + mValidDataLength, getIndexRightSpace() - mValidDataLength); // 填充缓冲区后半部分
                    System.arraycopy(data, getIndexRightSpace() - mValidDataLength, mBuffer, 0, available - (getIndexRightSpace() - mValidDataLength)); // 填充缓冲区前半部分
                }
            } else {// 当前有效数据已经分段
                System.arraycopy(data, 0, mBuffer, mValidDataLength - getIndexRightSpace(), available);
            }
            mValidDataLength = totalDataLength;
        }
    }

    /**
     * 获取指针mStartIndex右侧的剩余空间长度
     *
     * @return
     */
    private int getIndexRightSpace() {
        return mBuffer.length - mStartIndex;
    }

    /**
     * 获取缓冲器空余长度
     *
     * @return
     */
    private int getBufferFreeSpace() {
        return mBuffer.length - mValidDataLength;
    }

    /**
     * 当前数据存储是否连续
     * 【如果当前指针右侧的剩余空间长度大于目前存储数据的长度，那么就不需要分段存储了】
     *
     * @return 数据存储是否连续
     */
    private boolean isDataStoreContinuous() {
        return getIndexRightSpace() >= mValidDataLength;
    }

    /**
     * 指定长度的数据存储是否连续
     * 【如果当前指针右侧的剩余空间长度大于指定数据的长度，那么就不需要分段存储了】
     *
     * @param dataLength 指定数据的长度
     * @return 数据存储是否连续
     */
    private boolean isDataStoreContinuous(int dataLength) {
        return getIndexRightSpace() >= dataLength;
    }

    @Override
    public byte[] getData() throws BufferException {
        return getData(mValidDataLength);
    }

    @Override
    public byte[] getData(int dataLength) throws BufferException {
        if (dataLength > mValidDataLength) {
            throw new BufferException("Buffer does not have enough data for access! The length of data currently stored is " + mValidDataLength + ", but the length of data to be acquired is " + dataLength);
        }

        byte[] data = new byte[dataLength];
        // 未分段数据
        if (isDataStoreContinuous(dataLength)) {
            System.arraycopy(mBuffer, mStartIndex, data, 0, dataLength);
        } else {// 分段数据
            System.arraycopy(mBuffer, mStartIndex, data, 0, getIndexRightSpace());
            System.arraycopy(mBuffer, 0, data, getIndexRightSpace(), dataLength - getIndexRightSpace());
        }
        return data;
    }

    @Override
    public void clear() {
        mStartIndex = 0;
        mValidDataLength = 0;
    }

    @Override
    public void recycle() {
        mBuffer = null;
        clear();
    }

    /**
     * 释放缓存【实质就是指针往后偏移】
     *
     * @param length 需要释放的长度
     */
    @Override
    public void release(int length) throws BufferException {
        // 请求释放的长度大于有效区间的长度，就是指针往后移动
        if (mValidDataLength < length) {
            throw new BufferException("no enough valid data to release!");
        }
        int offsetIndex = mStartIndex + length;
        if (offsetIndex < mBuffer.length) {  //偏移后的指针位置小于缓存的长度，不需要分段的释放
            mStartIndex = offsetIndex;
        } else {                              //偏移后的指针位置大于缓存的长度，需要分段的释放
            mStartIndex = offsetIndex - mBuffer.length;
        }
        mValidDataLength = mValidDataLength - length;
    }

    @Override
    public int getValidDataLength() {
        return mValidDataLength;
    }

}
