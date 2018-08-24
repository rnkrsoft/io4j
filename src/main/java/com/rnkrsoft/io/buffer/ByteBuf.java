package com.rnkrsoft.io.buffer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by rnkrsoft.com on 2017/11/30.
 * 字节缓冲区
 */
public abstract class ByteBuf {
    public static ByteBuf allocate(int capacity) {
        return new HeapByteBuf(capacity);
    }

    public static ByteBuf allocate(byte[] data) {
        return new HeapByteBuf(data);
    }

    public abstract boolean bigEndian();

    public abstract ByteBuf bigEndian(boolean bigEndian);

    public abstract int capacity();

    public abstract ByteBuf capacity(int newCapacity);

    public abstract int maxCapacity();

    public abstract boolean isAutoExpand();

    public abstract ByteBuf autoExpand(boolean autoExpand);

    public abstract boolean isReadOnly();

    public abstract ByteBuf readOnly(boolean readOnly);

    public abstract ByteBuf clear();

    public abstract ByteBuf put(byte v);

    public abstract ByteBuf put(short v);

    public abstract ByteBuf put(int v);

    public abstract ByteBuf put(long v);

    public abstract ByteBuf put(float v);

    public abstract ByteBuf put(double v);

    public abstract ByteBuf put(byte[] v);

    public abstract ByteBuf put(String charset, String... strings);

    public abstract ByteBuf put(ByteBuffer buffer);

    public abstract ByteBuf get(byte[] data);

    /**
     * 检测缓存是否还有可读的
     * @return 是否可读
     */
    public abstract boolean readyRead();

    /**
     * 重置写指针
     * @return 缓冲区对象
     */
    public abstract ByteBuf resetWrite();
    /**
     * 重置读指针
     * @return 缓冲区对象
     */
    public abstract ByteBuf resetRead();
    /**
     * 将缓冲区中的内容作为ByteBuffer缓冲区
     * @return ByteBuffer缓冲区
     */
    public abstract ByteBuffer asByteBuffer();

    /**
     * 将缓冲区中的内容作为输入流
     * @return 字节数组输入流
     */
    public abstract ByteArrayInputStream asInputStream();

    /**
     * 从输入流读取
     * @param is 输入流
     * @return 读取字节数
     * @throws IOException 异常
     */
    public abstract int read(InputStream is)throws IOException;
    /**
     * 向输出流写入
     * @param os 输入流
     * @return 写入字节数
     * @throws IOException 异常
     */
    public abstract int write(OutputStream os) throws IOException;

    public abstract ByteBuf get(ByteBuffer buffer);

    /**
     * 根据默认字符集将所有内容输出未字符串
     * @return 字符串
     */
    public String asString(){
        return asString(System.getProperty("file.encoding"));
    }
    /**
     * 将所有内容作为字符串输出
     * @param charset 字符集
     * @return 字符串
     */
    public abstract String asString(String charset);

    /**
     * 根据默认字符集从当前读指针开始读取指定长度的字符串
     * @param length 长度
     * @return 字符串
     */
    public String getString(int length){
        return getString(System.getProperty("file.encoding"), length);
    }
    /**
     * 根据默认字符集从当前读指针开始读取指定长度的字符串
     * @param charset 字符集
     * @param length 长度
     * @return 字符串
     */
    public abstract String getString(String charset, int length);

    public abstract byte getByte();

    public abstract short getShort();

    public abstract int getInt();

    public abstract long getLong();

    public abstract float getFloat();

    public abstract double getDouble();

    public abstract byte[] getBytes();

    public abstract byte[] getBytes(int length);

    public abstract int readableLength();

    public abstract int writableLength();
}
