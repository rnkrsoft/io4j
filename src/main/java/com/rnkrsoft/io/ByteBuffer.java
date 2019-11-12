//package com.rnkrsoft.io;
//
//
//        import com.rnkrsoft.io.buffer.ReferenceCounted;
//        import com.rnkrsoft.io.buffer.process.ByteProcessor;
//
//        import java.io.IOException;
//        import java.io.InputStream;
//        import java.io.OutputStream;
//        import java.nio.charset.Charset;
//
///**
// * Created by rnkrsoft on 2019/10/30.
// * 字节缓冲区定义
// */
//public interface ByteBuffer extends ByteBufferReadable, ByteBufferWritable, ByteBufferGettable, ByteBufferSettable, ReferenceCounted, Comparable<ByteBuffer> {
//    /**
//     * 缓冲区当前载体的当前容量，容量即字节数组长度
//     *
//     * @return 当前容量
//     */
//    int capacity();
//
//    /**
//     * 设置当前缓冲区新的容量，可进行扩容或者压缩
//     *
//     * @param newCapacity 新的容量当前容量
//     * @return 当前字节缓冲区
//     */
//    ByteBuffer capacity(int newCapacity);
//
//    /**
//     * 缓冲区最大的容量大小，该值由字节缓冲区创建时设置，具有不可变
//     *
//     * @return 最大的容量大小
//     */
//    int maxCapacity();
//
//
//    /**
//     * 缓冲区当前是否为只读状态，当为只读状态时，只可进行读取操作，不能进行修改操作
//     *
//     * @return 返回真为只读状态
//     */
//    boolean isReadOnly();
//
//    /**
//     * 设置当前缓冲区是否为只读状态
//     *
//     * @param readOnly 是否为只读状态
//     * @return 当前字节缓冲区
//     */
//    ByteBuffer readOnly(boolean readOnly);
//
//    /**
//     * 是否可读，可读的条件为readIndex < writeIndex
//     *
//     * @return 返回真则可以读取至少一个字节，具体可读取多少字节可{@code(readableBytesLength)}
//     */
//    boolean isReadable();
//
//    /**
//     * 是否可写，可写的条件为maxCapacity > writeIndex
//     *
//     * @return 返回真则可以写入至少一个字节，具体可写入多少字节可{@code(writableBytesLength)}
//     */
//    boolean isWritable();
//
//    /**
//     * 当前已读取的游标
//     *
//     * @return 游标索引
//     */
//    int readerIndex();
//
//    /**
//     * 重设读取游标位置
//     *
//     * @param readerIndex 游标位置，[0，writerIndex)
//     * @return 当前字节缓冲区
//     */
//    ByteBuffer readerIndex(int readerIndex);
//
//    /**
//     * 当前已写的游标
//     *
//     * @return 游标索引
//     */
//    int writerIndex();
//
//    /**
//     * 重设写入游标位置
//     *
//     * @param writerIndex 游标位置，[0，capacity)
//     * @return 当前字节缓冲区
//     */
//    ByteBuffer writerIndex(int writerIndex);
//
//    /**
//     * 可读取字节的长度
//     *
//     * @return 字节长度
//     */
//    int readableBytesLength();
//
//    /**
//     * 在不进行扩容的情况下，当前可写入字节的长度
//     *
//     * @return 字节长度
//     */
//    int writableBytesLength();
//
//    /**
//     * 在进行扩容的情况下，可写入字节的长度
//     *
//     * @return 字节长度
//     */
//    int maxWritableBytesLength();
//
//    /**
//     * 清空缓冲区内容，仅进行读写游标的重置，并不进行数据重置为0
//     *
//     * @return 当前字节缓冲区
//     */
//    ByteBuffer clear();
//
//    ByteBuffer markReaderIndex();
//
//    ByteBuffer resetReaderIndex();
//
//    ByteBuffer markWriterIndex();
//
//    ByteBuffer resetWriterIndex();
//
//    ByteBuffer discardReadBytes();
//
//    ByteBuffer discardSomeReadBytes();
//
//    ByteBuffer ensureWritable(int minWritableBytes);
//
//    int ensureWritable(int minWritableBytes, boolean force);
//
//    /**
//     * 在字节缓冲区中搜索指定的字节内容，查找到返回索引位置，否则返回-1
//     *
//     * @param fromIndex 起始查找位置
//     * @param toIndex   结束查找位置
//     * @param value     内容
//     * @return 查找到返回索引位置，否则返回-1
//     */
//    int indexOf(int fromIndex, int toIndex, byte value);
//
//    /**
//     * 在字节缓冲区中搜索指定的字节数组内容，查找到返回索引位置，否则返回-1
//     *
//     * @param fromIndex 起始查找位置
//     * @param toIndex   结束查找位置
//     * @param value     内容
//     * @return 查找到返回索引位置，否则返回-1
//     */
//    int indexOf(int fromIndex, int toIndex, byte[] value);
//
//    int bytesBefore(byte value);
//
//    int bytesBefore(int length, byte value);
//
//    int bytesBefore(int index, int length, byte value);
//
//    int forEachByte(ByteProcessor processor);
//
//    int forEachByte(int index, int length, ByteProcessor processor);
//
//    /**
//     * 使用一个字节处理器数组，如果均匹配则返回第一匹配开始的索引位置
//     *
//     * @param index
//     * @param length
//     * @param processors
//     * @return
//     */
//    int forEachByte(int index, int length, ByteProcessor[] processors);
//
//    int forEachByteDesc(ByteProcessor processor);
//
//    int forEachByteDesc(int index, int length, ByteProcessor processor);
//
//    /**
//     * 复制当前的字节缓冲区实例，拥有独立的载体底层和readerIndex, writerIndex。相当于进行了深拷贝
//     *
//     * @return 新的字节缓冲区实例
//     */
//    ByteBuffer copy();
//
//    /**
//     * 从指定的索引位置开始，复制指定长度的当前字节缓冲区实例的内容为新的缓冲区，拥有独立的载体底层和readerIndex, writerIndex。相当于进行了深拷贝
//     *
//     * @param index  复制起始索引，包含
//     * @param length 复制指定长度的内容，字节
//     * @return 新的字节缓冲区实例
//     */
//    ByteBuffer copy(int index, int length);
//
//    ByteBuffer slice();
//
//    ByteBuffer slice(int index, int length);
//
//    ByteBuffer retainedSlice();
//
//    ByteBuffer retainedSlice(int index, int length);
//
//    /**
//     * 复制当前的字节缓冲区实例，但是共享同一个载体底层，拥有独立的readerIndex, writerIndex。相当于进行了浅拷贝
//     *
//     * @return 新的字节缓冲区实例
//     */
//    ByteBuffer duplicate();
//
//    ByteBuffer retainedDuplicate();
//
//    int nioBufferCount();
//
//    /**
//     * 将当前字节缓冲区内容以java.nio.ByteBuffer对象包装
//     *
//     * @return JDK字节缓冲区
//     */
//    java.nio.ByteBuffer nioBuffer();
//
//    /**
//     * 将当前字节缓冲区指定区间的内容以java.nio.ByteBuffer对象包装
//     *
//     * @param index  起始索引
//     * @param length 内容字节长度
//     * @return JDK字节缓冲区
//     */
//    java.nio.ByteBuffer nioBuffer(int index, int length);
//
//    /**
//     * 获取内部的DK字节缓冲区
//     *
//     * @param index  起始索引
//     * @param length 内容字节长度
//     * @return JDK字节缓冲区
//     */
//    java.nio.ByteBuffer internalNioBuffer(int index, int length);
//
//    /**
//     * 将当前字节缓冲区以JDK字节缓冲区数组形式返回，如果非组合字节缓冲区返回数组长度为1的数组，组合字节缓冲区返回数组
//     *
//     * @return DK字节缓冲区数组
//     */
//    java.nio.ByteBuffer[] nioBuffers();
//
//    java.nio.ByteBuffer[] nioBuffers(int index, int length);
//
//
//    /**
//     * 缓冲区是否基于堆外直接内存,如果是组合缓冲区，只要组合中有一个不是直接内存实现，则返回假，也就是必须全部为直接内存才返回真
//     *
//     * @return 返回为真表示基于直接内存的缓冲区
//     * @see #hasArray()
//     */
//    boolean isDirect();
//
//    /**
//     * 缓冲区是否基于堆字节数组实现，如果是组合缓冲区，只要组合中有一个是堆数组实现，则返回真，也就是为假则全部为非堆数组实现才返回假
//     *
//     * @return 返回为真表示存在基于堆数组实现的缓冲区
//     * @see #isDirect()
//     */
//    boolean hasArray();
//
//    /**
//     * 如果当前缓冲区基于堆数组实现，则返回载体对应的字节数组，否则返回{@code null}
//     *
//     * @return 载体数组
//     */
//    byte[] array();
//
//    /**
//     * fixme 这个方法有什么用
//     * 如果当前缓冲区基于堆数组实现，则返回数组偏移索引，否则返回{@code -1}
//     *
//     * @return 数组偏移索引
//     */
//    int arrayOffset();
//
//    boolean hasMemoryAddress();
//
//    long memoryAddress();
//
//    /**
//     * 将可读取的缓冲区内容以字符串形式输出，不改变读取游标的位置
//     *
//     * @param charset 字符集对象
//     * @return 以charset编码的字符串内容
//     */
//    String toString(Charset charset);
//
//    /**
//     * 将指定起始位置，指定长度的缓冲区内容以字符串形式输出，不改变读取游标的位置
//     *
//     * @param index   起始位置索引
//     * @param length  内容长度
//     * @param charset 字符集对象
//     * @return 以charset编码的字符串内容
//     */
//    String toString(int index, int length, Charset charset);
//
//
//    /**
//     * 将缓冲区中的内容作为输入流
//     *
//     * @return 字节数组输入流
//     */
//    InputStream asInputStream();
//
//    /**
//     * 从输入流读取
//     *
//     * @param is 输入流
//     * @return 读取字节数
//     * @throws IOException 异常
//     */
//    int load(InputStream is) throws IOException;
//
//    /**
//     * 从文件读取
//     *
//     * @param fileName 文件名
//     * @return 读取字节数
//     * @throws IOException 异常
//     */
//    int load(String fileName) throws IOException;
//
//    /**
//     * 向输出流写入
//     *
//     * @param os 输入流
//     * @return 写入字节数
//     * @throws IOException 异常
//     */
//    int store(OutputStream os) throws IOException;
//
//    /**
//     * 像文件写入
//     *
//     * @param fileName 文件名
//     * @return 写入字节数
//     * @throws IOException 异常
//     */
//    int store(String fileName) throws IOException;
//
//    /**
//     * 如果当前缓冲区是包装，则可以获取真实的缓冲区
//     * @return
//     */
//    ByteBuffer unwrap();
//}
