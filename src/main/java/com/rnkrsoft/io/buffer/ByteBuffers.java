package com.rnkrsoft.io.buffer;

import static com.rnkrsoft.io.buffer.util.DiskSizeUnit.*;

/**
 * Created by rnkrsoft.com on 2019/10/31.
 * ByteBuffer实用工具类
 */
public abstract class ByteBuffers {
    static final ThreadLocal<byte[]> BYTE_ARRAY_POOL = new ThreadLocal<byte[]>();
    /**
     * 使用缓存数组的最大字节大小
     */
    public static int MAX_BUFFER_LENGTH = nKB(1);
    /**
     * 倍增扩容的阻断阈值，默认为4MB
     */
    public static int DOUBLE_EXPAND_THRESHOLD = nMB(4);
    /**
     * 默认进行按需分配的其实阈值，默认8MB
     */
    public static int DISTRIBUTION_ON_DEMAND_EXPAND_THRESHOLD = nMB(8);
    /**
     * 调用 newBuffer(int, int)或者 newBuffer(int)时创建的缓冲区类型，是基于堆的还是基于直接内存区的
     *
     * @see #newBuffer(int, int)
     */
    public static ByteBufferType DEFAULT_BUFFER_TYPE = ByteBufferType.HEAP;
    public static boolean DEFAULT_POOLED = true;

    /**
     * 获取一个指定长度的字节数组
     *
     * @param length 长度
     * @return 返回的数组可能是基于缓存的，也可能是基于堆的
     */
    public static byte[] getBytes(int length) {
        byte[] bytes = null;
        if (length > MAX_BUFFER_LENGTH) {
            bytes = new byte[length];
        } else {
            bytes = BYTE_ARRAY_POOL.get();
            if (bytes == null) {
                bytes = new byte[MAX_BUFFER_LENGTH];
                BYTE_ARRAY_POOL.set(bytes);
            }
        }
        return bytes;
    }

    /**
     * 创建一个基于堆字节数组的非池化字节缓冲区
     *
     * @param type         缓存类型
     * @param pooled       是否进行池化
     * @param initCapacity 初始化容量
     * @param maxCapacity  最大容量
     * @return 字节缓冲区实例
     */
    public static final ByteBuffer newBuffer(ByteBufferType type, boolean pooled, int initCapacity, int maxCapacity) {
        if (type == ByteBufferType.DIRECT) {
            return new UnpooledDirectByteBuffer(UnpooledByteBufferAllocator.DEFAULT, initCapacity, maxCapacity);
        } else if (type == ByteBufferType.HEAP) {
            return new UnpooledHeapByteBuffer(UnpooledByteBufferAllocator.DEFAULT, initCapacity, maxCapacity);
        } else {
            throw new IllegalArgumentException("illegal argument type:" + type);
        }

    }

    /**
     * 创建一个固定容量的缓冲区
     *
     * @param fixedCapacity 固定容量，字节数组长度
     * @return 缓冲区对象
     */
    public static final ByteBuffer newBuffer(int fixedCapacity) {
        return newBuffer(fixedCapacity, fixedCapacity);
    }
    /**
     * 创建一个有初始容量和最大容量的缓冲区，根据需要进行扩增
     *
     * @param initCapacity 初始容量，字节数组长度
     * @param maxCapacity  最大容量，字节数组长度
     * @return 缓冲区对象
     */
    public static final ByteBuffer newBuffer(int initCapacity, int maxCapacity) {
        return newBuffer(DEFAULT_BUFFER_TYPE, DEFAULT_POOLED, initCapacity, maxCapacity);
    }

}
