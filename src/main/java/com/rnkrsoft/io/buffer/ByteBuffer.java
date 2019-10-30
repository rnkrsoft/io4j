package com.rnkrsoft.io.buffer;


import com.rnkrsoft.io.buffer.process.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public interface ByteBuffer extends ByteBufferReadable, ByteBufferWritable, ReferenceCounted, Comparable<ByteBuffer> {
    boolean isAutoExpand();

    ByteBuffer autoExpand(boolean autoExpand);

    int capacity();

    ByteBuffer capacity(int newCapacity);

    int maxCapacity();

    boolean isDirect();

    boolean isReadOnly();

    ByteBuffer readOnly(boolean readOnly);

    boolean isReadable();

    boolean isWritable();

    int readerIndex();

    ByteBuffer readerIndex(int readerIndex);

    int writerIndex();

    ByteBuffer writerIndex(int writerIndex);

    int readableBytes();

    int writableBytes();

    int maxWritableBytes();

    ByteBuffer clear();

    ByteBuffer markReaderIndex();

    ByteBuffer resetReaderIndex();

    ByteBuffer markWriterIndex();

    ByteBuffer resetWriterIndex();

    ByteBuffer discardReadBytes();

    ByteBuffer discardSomeReadBytes();

    ByteBuffer ensureWritable(int minWritableBytes);

    int ensureWritable(int minWritableBytes, boolean force);

    int indexOf(int fromIndex, int toIndex, byte value);

    int bytesBefore(byte value);

    int bytesBefore(int length, byte value);

    int bytesBefore(int index, int length, byte value);

    int forEachByte(ByteProcessor processor);

    int forEachByte(int index, int length, ByteProcessor processor);

    int forEachByteDesc(ByteProcessor processor);

    int forEachByteDesc(int index, int length, ByteProcessor processor);

    ByteBuffer copy();

    ByteBuffer copy(int index, int length);

    ByteBuffer slice();

    ByteBuffer slice(int index, int length);

    ByteBuffer retainedSlice();

    ByteBuffer retainedSlice(int index, int length);

    ByteBuffer duplicate();

    ByteBuffer retainedDuplicate();

    int nioBufferCount();

    java.nio.ByteBuffer nioBuffer();

    java.nio.ByteBuffer nioBuffer(int index, int length);

    java.nio.ByteBuffer internalNioBuffer(int index, int length);

    java.nio.ByteBuffer[] nioBuffers();

    java.nio.ByteBuffer[] nioBuffers(int index, int length);

    boolean hasArray();

    byte[] array();

    int arrayOffset();

    boolean hasMemoryAddress();

    long memoryAddress();

    String toString(Charset charset);

    String toString(int index, int length, Charset charset);


    /**
     * 将缓冲区中的内容作为输入流
     *
     * @return 字节数组输入流
     */
    InputStream asInputStream();

    /**
     * 从输入流读取
     *
     * @param is 输入流
     * @return 读取字节数
     * @throws IOException 异常
     */
    int load(InputStream is) throws IOException;

    /**
     * 从文件读取
     *
     * @param fileName 文件名
     * @return 读取字节数
     * @throws IOException 异常
     */
    int load(String fileName) throws IOException;

    /**
     * 向输出流写入
     *
     * @param os 输入流
     * @return 写入字节数
     * @throws IOException 异常
     */
     int store(OutputStream os) throws IOException;

    /**
     * 像文件写入
     *
     * @param fileName 文件名
     * @return 写入字节数
     * @throws IOException 异常
     */
    int store(String fileName) throws IOException;
}
