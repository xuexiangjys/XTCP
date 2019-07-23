package com.xuexiang.xtcp.core.buffer.impl;

import com.xuexiang.xtcp.core.buffer.BufferException;

/**
 * 简单实用的环形缓冲区
 *
 * @author xuexiang
 * @since 2019-07-23 8:44
 */
public class SimpleBuffer extends CircularBuffer {

    /**
     * 构造方法
     */
    public SimpleBuffer() {
        super();
    }

    /**
     * 构造方法
     *
     * @param bufferLength 缓冲区大小
     */
    public SimpleBuffer(int bufferLength) {
        super(bufferLength);
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void putData(byte[] data) {
        putData(data, data.length);
    }

    @Override
    public void putData(byte[] data, int available) {
        try {
            super.putData(data, available);
        } catch (BufferException e) {
            e.printStackTrace();
        }
    }


    @Override
    public byte[] getData() {
        try {
            return super.getData();
        } catch (BufferException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public byte[] getData(int dataLength) {
        try {
            return super.getData(dataLength);
        } catch (BufferException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 释放缓存【实质就是指针往后偏移】
     *
     * @param length 需要释放的长度
     */
    @Override
    public void release(int length) {
        try {
            super.release(length);
        } catch (BufferException e) {
            e.printStackTrace();
        }
    }

}
