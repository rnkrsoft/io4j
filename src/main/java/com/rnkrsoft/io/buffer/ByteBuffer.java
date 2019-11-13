/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.util.ReferenceCounted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * A random and sequential accessible sequence of zero or more bytes (octets).
 * This interface provides an abstract view for one or more primitive byte
 * arrays ({@code byte[]}) and {@linkplain java.nio.ByteBuffer NIO buffers}.
 * <p>
 * <h3>Creation of a buffer</h3>
 * <p>
 * It is recommended to create a new buffer using the helper methods in
 * {@link Unpooled} rather than calling an individual implementation's
 * constructor.
 * <p>
 * <h3>Random Access Indexing</h3>
 * <p>
 * Just like an ordinary primitive byte array, {@link ByteBuffer} uses
 * <a href="http://en.wikipedia.org/wiki/Zero-based_numbering">zero-based indexing</a>.
 * It means the index of the first byte is always {@code 0} and the index of the last byte is
 * always {@link #capacity() capacity - 1}.  For example, to iterate all bytes of a buffer, you
 * can do the following, regardless of its internal implementation:
 * <p>
 * <pre>
 * {@link ByteBuffer} buffer = ...;
 * for (int i = 0; i &lt; buffer.capacity(); i ++) {
 *     byte b = buffer.getByte(i);
 *     System.out.println((char) b);
 * }
 * </pre>
 * <p>
 * <h3>Sequential Access Indexing</h3>
 * <p>
 * {@link ByteBuffer} provides two pointer variables to support sequential
 * read and write operations - {@link #readerIndex() readerIndex} for a read
 * operation and {@link #writerIndex() writerIndex} for a write operation
 * respectively.  The following diagram shows how a buffer is segmented into
 * three areas by the two pointers:
 * <p>
 * <pre>
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      |                   |     (CONTENT)    |                  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 * </pre>
 * <p>
 * <h4>Readable bytes (the actual content)</h4>
 * <p>
 * This segment is where the actual data is stored.  Any operation whose name
 * starts with {@code read} or {@code skip} will get or skip the data at the
 * current {@link #readerIndex() readerIndex} and increase it by the number of
 * read bytes.  If the argument of the read operation is also a
 * {@link ByteBuffer} and no destination index is specified, the specified
 * buffer's {@link #writerIndex() writerIndex} is increased together.
 * <p>
 * If there's not enough content left, {@link IndexOutOfBoundsException} is
 * raised.  The default value of newly allocated, wrapped or copied buffer's
 * {@link #readerIndex() readerIndex} is {@code 0}.
 * <p>
 * <pre>
 * // Iterates the readable bytes of a buffer.
 * {@link ByteBuffer} buffer = ...;
 * while (buffer.isReadable()) {
 *     System.out.println(buffer.readByte());
 * }
 * </pre>
 * <p>
 * <h4>Writable bytes</h4>
 * <p>
 * This segment is a undefined space which needs to be filled.  Any operation
 * whose name starts with {@code write} will write the data at the current
 * {@link #writerIndex() writerIndex} and increase it by the number of written
 * bytes.  If the argument of the write operation is also a {@link ByteBuffer},
 * and no source index is specified, the specified buffer's
 * {@link #readerIndex() readerIndex} is increased together.
 * <p>
 * If there's not enough writable bytes left, {@link IndexOutOfBoundsException}
 * is raised.  The default value of newly allocated buffer's
 * {@link #writerIndex() writerIndex} is {@code 0}.  The default value of
 * wrapped or copied buffer's {@link #writerIndex() writerIndex} is the
 * {@link #capacity() capacity} of the buffer.
 * <p>
 * <pre>
 * // Fills the writable bytes of a buffer with random integers.
 * {@link ByteBuffer} buffer = ...;
 * while (buffer.maxWritableBytesLength() >= 4) {
 *     buffer.writeInt(random.nextInt());
 * }
 * </pre>
 * <p>
 * <h4>Discardable bytes</h4>
 * <p>
 * This segment contains the bytes which were read already by a read operation.
 * Initially, the size of this segment is {@code 0}, but its size increases up
 * to the {@link #writerIndex() writerIndex} as read operations are executed.
 * The read bytes can be discarded by calling {@link #discardReadBytes()} to
 * reclaim unused area as depicted by the following diagram:
 * <p>
 * <pre>
 *  BEFORE discardReadBytes()
 *
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 *
 *  AFTER discardReadBytes()
 *
 *      +------------------+--------------------------------------+
 *      |  readable bytes  |    writable bytes (got more space)   |
 *      +------------------+--------------------------------------+
 *      |                  |                                      |
 * readerIndex (0) <= writerIndex (decreased)        <=        capacity
 * </pre>
 * <p>
 * Please note that there is no guarantee about the content of writable bytes
 * after calling {@link #discardReadBytes()}.  The writable bytes will not be
 * moved in most cases and could even be filled with completely different data
 * depending on the underlying buffer implementation.
 * <p>
 * <h4>Clearing the buffer indexes</h4>
 * <p>
 * You can set both {@link #readerIndex() readerIndex} and
 * {@link #writerIndex() writerIndex} to {@code 0} by calling {@link #clear()}.
 * It does not clear the buffer content (e.g. filling with {@code 0}) but just
 * clears the two pointers.  Please also note that the semantic of this
 * operation is different from {@link java.nio.ByteBuffer#clear()}.
 * <p>
 * <pre>
 *  BEFORE clear()
 *
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 *
 *  AFTER clear()
 *
 *      +---------------------------------------------------------+
 *      |             writable bytes (got more space)             |
 *      +---------------------------------------------------------+
 *      |                                                         |
 *      0 = readerIndex = writerIndex            <=            capacity
 * </pre>
 * <p>
 * <h3>Search operations</h3>
 * <p>
 * For simple single-byte searches, use {@link #indexOf(int, int, byte)} and {@link #bytesBefore(int, int, byte)}.
 * {@link #bytesBefore(byte)} is especially useful when you deal with a {@code NUL}-terminated string.
 * For complicated searches, use {@link #forEachByte(int, int, ByteBufferProcessor)} with a {@link ByteBufferProcessor}
 * implementation.
 * <p>
 * <h3>Mark and reset</h3>
 * <p>
 * There are two marker indexes in every buffer. One is for storing
 * {@link #readerIndex() readerIndex} and the other is for storing
 * {@link #writerIndex() writerIndex}.  You can always reposition one of the
 * two indexes by calling a reset method.  It works in a similar fashion to
 * the mark and reset methods in {@link InputStream} except that there's no
 * {@code readlimit}.
 * <p>
 * <h3>Derived buffers</h3>
 * <p>
 * You can create a view of an existing buffer by calling either
 * {@link #duplicate()}, {@link #slice()} or {@link #slice(int, int)}.
 * A derived buffer will have an independent {@link #readerIndex() readerIndex},
 * {@link #writerIndex() writerIndex} and marker indexes, while it shares
 * other internal data representation, just like a NIO buffer does.
 * <p>
 * In case a completely fresh copy of an existing buffer is required, please
 * call {@link #copy()} method instead.
 * <p>
 * Also be aware that obtaining derived buffers will NOT call {@link #retain()} and so the
 * reference count will NOT be increased.
 * <p>
 * <h3>Conversion to existing JDK types</h3>
 * <p>
 * <h4>Byte array</h4>
 * <p>
 * If a {@link ByteBuffer} is backed by a byte array (i.e. {@code byte[]}),
 * you can access it directly via the {@link #array()} method.  To determine
 * if a buffer is backed by a byte array, {@link #hasArray()} should be used.
 * <p>
 * <h4>NIO Buffers</h4>
 * <p>
 * If a {@link ByteBuffer} can be converted into an NIO {@link java.nio.ByteBuffer} which shares its
 * content (i.e. view buffer), you can get it via the {@link #nioBuffer()} method.  To determine
 * if a buffer can be converted into an NIO buffer, use {@link #nioBufferCount()}.
 * <p>
 * <h4>Strings</h4>
 * <p>
 * Various {@link #toString(Charset)} methods convert a {@link ByteBuffer}
 * into a {@link String}.  Please note that {@link #toString()} is not a
 * conversion method.
 * <p>
 * <h4>I/O Streams</h4>
 * <p>
 * Please refer to {@link ByteBufferInputStream} and
 * {@link ByteBufferOutputStream}.
 */
@SuppressWarnings("ClassMayBeInterface")
public interface ByteBuffer extends ByteBufferGettable, ByteBufferSettable, ByteBufferReadable, ByteBufferWritable, ReferenceCounted, Comparable<ByteBuffer> {

    /**
     * 缓冲区当前载体的当前容量，容量即字节数组长度
     *
     * @return 当前容量
     */
    int capacity();

    /**
     * 设置当前缓冲区新的容量，可进行扩容或者压缩,如果新的容量小于当前已写入内容，则会发生截断。
     *
     * @param newCapacity 新的容量当前容量
     * @return 当前字节缓冲区
     */
    ByteBuffer capacity(int newCapacity);

    /**
     * 缓冲区最大的容量大小，该值由字节缓冲区创建时设置，具有不可变。当运行时，缓冲区内容超过容量值时，则进行扩增，直到最大容量，方法{@link #ensureWritable(int)}调用该方法的设置值
     *
     * @return 最大的容量大小
     */
    int maxCapacity();

    /**
     * 获取当前缓冲区实例对应的缓冲区分配器
     *
     * @return 缓冲区分配器
     */
    ByteBufferAllocator alloc();

    /**
     * 获取缓冲区使用的是大端顺序还是小端顺序
     */
    ByteOrder order();

    /**
     * 设置缓冲区的端顺序
     */
    ByteBuffer order(ByteOrder endianness);

    /**
     * 如果该缓冲区是包装类型缓冲区，则可返回真正的缓冲区实例
     *
     * @return 如果该缓冲区不是包装类型缓冲区，则返回{@code null}
     */
    ByteBuffer unwrap();

    /**
     * 可返回当前缓冲区底层是基于JDK的直接内存区的缓冲区
     *
     * @return 如果为直接内存缓冲区则返回真
     */
    boolean isDirect();

    /**
     * 当前读游标
     *
     * @return 游标索引
     */
    int readerIndex();

    /**
     * 重设读游标位置
     *
     * @param readerIndex 游标位置，[0，writerIndex)
     * @return 当前字节缓冲区
     * @throws IndexOutOfBoundsException 当读游标{@code readerIndex} 小于 {@code 0} 或者大于当前缓冲区的读游标 {@code writerIndex}则抛出该异常
     */
    ByteBuffer readerIndex(int readerIndex);

    /**
     * 当前已写游标
     *
     * @return 游标索引
     */
    int writerIndex();

    /**
     * 重设写游标位置
     *
     * @param writerIndex 游标位置，[0，capacity)
     * @return 当前字节缓冲区
     * @throws IndexOutOfBoundsException 当写游标{@code writerIndex} 小于 {@code readerIndex} 或者大于当前缓冲区的容量{@code capacity()}则抛出该异常
     */
    ByteBuffer writerIndex(int writerIndex);

    /**
     * 用于快速重设读写游标
     *
     * @throws IndexOutOfBoundsException if the specified {@code readerIndex} is less than 0,
     *                                   if the specified {@code writerIndex} is less than the specified
     *                                   {@code readerIndex} or if the specified {@code writerIndex} is
     *                                   greater than {@code this.capacity}
     */
    ByteBuffer setIndex(int readerIndex, int writerIndex);

    /**
     * 可读取字节的长度
     * {@code (this.writerIndex - this.readerIndex)}.
     */
    int readableBytesLength();

    /**
     * 在不进行扩容的情况下，当前可写入字节的长度 {@code (this.capacity() - this.writerIndex)}.
     */
    int writableBytesLength();

    /**
     * 在进行扩容的情况下，可写入字节的长度
     * {@code (this.maxCapacity - this.writerIndex)}.
     */
    int maxWritableBytesLength();

    /**
     * 是否可读，可读的条件为readIndex < writeIndex
     *
     * @return 返回真则可以读取至少一个字节，具体可读取多少字节可{@code(readableBytesLength)}
     */
    boolean isReadable();

    /**
     * Returns {@code true} if and only if this buffer contains equal to or more than the specified number of elements.
     */
    boolean isReadable(int size);

    /**
     * 是否可写，可写的条件为maxCapacity > writeIndex
     *
     * @return 返回真则可以写入至少一个字节，具体可写入多少字节可{@code(writableBytesLength)}
     */
    boolean isWritable();

    /**
     * Returns {@code true} if and only if this buffer has enough room to allow writing the specified number of
     * elements.
     */
    boolean isWritable(int size);

    /**
     * 清空缓冲区内容，仅进行读写游标的重置，并不进行数据重置为0
     *
     * @return 当前字节缓冲区
     */
    ByteBuffer clear();

    /**
     * Marks the current {@code readerIndex} in this buffer.  You can
     * reposition the current {@code readerIndex} to the marked
     * {@code readerIndex} by calling {@link #resetReaderIndex()}.
     * The initial value of the marked {@code readerIndex} is {@code 0}.
     */
    ByteBuffer markReaderIndex();

    /**
     * Repositions the current {@code readerIndex} to the marked
     * {@code readerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException if the current {@code writerIndex} is less than the marked
     *                                   {@code readerIndex}
     */
    ByteBuffer resetReaderIndex();

    /**
     * Marks the current {@code writerIndex} in this buffer.  You can
     * reposition the current {@code writerIndex} to the marked
     * {@code writerIndex} by calling {@link #resetWriterIndex()}.
     * The initial value of the marked {@code writerIndex} is {@code 0}.
     */
    ByteBuffer markWriterIndex();

    /**
     * Repositions the current {@code writerIndex} to the marked
     * {@code writerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException if the current {@code readerIndex} is greater than the marked
     *                                   {@code writerIndex}
     */
    ByteBuffer resetWriterIndex();

    /**
     * Discards the bytes between the 0th index and {@code readerIndex}.
     * It moves the bytes between {@code readerIndex} and {@code writerIndex}
     * to the 0th index, and sets {@code readerIndex} and {@code writerIndex}
     * to {@code 0} and {@code oldWriterIndex - oldReaderIndex} respectively.
     * <p>
     * Please refer to the class documentation for more detailed explanation.
     */
    ByteBuffer discardReadBytes();

    /**
     * Similar to {@link ByteBuffer#discardReadBytes()} except that this method might discard
     * some, all, or none of read bytes depending on its internal implementation to reduce
     * overall memory bandwidth consumption at the cost of potentially additional memory
     * consumption.
     */
    ByteBuffer discardSomeReadBytes();

    /**
     * Makes sure the number of {@linkplain #writableBytesLength() the writable bytes}
     * is equal to or greater than the specified value.  If there is enough
     * writable bytes in this buffer, this method returns with no side effect.
     * Otherwise, it raises an {@link IllegalArgumentException}.
     *
     * @param minWritableBytes the expected minimum number of writable bytes
     * @throws IndexOutOfBoundsException if {@link #writerIndex()} + {@code minWritableBytes} > {@link #maxCapacity()}
     */
    ByteBuffer ensureWritable(int minWritableBytes);

    /**
     * Tries to make sure the number of {@linkplain #writableBytesLength() the writable bytes}
     * is equal to or greater than the specified value.  Unlike {@link #ensureWritable(int)},
     * this method does not raise an exception but returns a code.
     *
     * @param minWritableBytes the expected minimum number of writable bytes
     * @param force            When {@link #writerIndex()} + {@code minWritableBytes} > {@link #maxCapacity()}:
     *                         <ul>
     *                         <li>{@code true} - the capacity of the buffer is expanded to {@link #maxCapacity()}</li>
     *                         <li>{@code false} - the capacity of the buffer is unchanged</li>
     *                         </ul>
     * @return {@code 0} if the buffer has enough writable bytes, and its capacity is unchanged.
     * {@code 1} if the buffer does not have enough bytes, and its capacity is unchanged.
     * {@code 2} if the buffer has enough writable bytes, and its capacity has been increased.
     * {@code 3} if the buffer does not have enough bytes, but its capacity has been
     * increased to its maximum.
     */
    int ensureWritable(int minWritableBytes, boolean force);


    /**
     * Increases the current {@code readerIndex} by the specified
     * {@code length} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code this.readableBytes}
     */
    ByteBuffer skipBytes(int length);


    /**
     * 在字节缓冲区中搜索指定的字节内容，查找到返回索引位置，否则返回-1
     *
     * @param fromIndex 起始查找位置
     * @param toIndex   结束查找位置
     * @param value     内容
     * @return 查找到返回索引位置，否则返回-1
     */
    int indexOf(int fromIndex, int toIndex, byte value);

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search takes place from the current {@code readerIndex}
     * (inclusive) to the current {@code writerIndex} (exclusive).
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the number of bytes between the current {@code readerIndex}
     * and the first occurrence if found. {@code -1} otherwise.
     */
    int bytesBefore(byte value);

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search starts from the current {@code readerIndex}
     * (inclusive) and lasts for the specified {@code length}.
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the number of bytes between the current {@code readerIndex}
     * and the first occurrence if found. {@code -1} otherwise.
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code this.readableBytes}
     */
    int bytesBefore(int length, byte value);

    /**
     * Locates the first occurrence of the specified {@code value} in this
     * buffer.  The search starts from the specified {@code index} (inclusive)
     * and lasts for the specified {@code length}.
     * <p>
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @return the number of bytes between the specified {@code index}
     * and the first occurrence if found. {@code -1} otherwise.
     * @throws IndexOutOfBoundsException if {@code index + length} is greater than {@code this.capacity}
     */
    int bytesBefore(int index, int length, byte value);

    /**
     * Iterates over the readable bytes of this buffer with the specified {@code processor} in ascending order.
     *
     * @return {@code -1} if the processor iterated to or beyond the end of the readable bytes.
     * The last-visited index If the {@link ByteBufferProcessor#process(byte)} returned {@code false}.
     */
    int forEachByte(ByteBufferProcessor processor);

    /**
     * Iterates over the specified area of this buffer with the specified {@code processor} in ascending order.
     * (i.e. {@code index}, {@code (index + 1)},  .. {@code (index + length - 1)})
     *
     * @return {@code -1} if the processor iterated to or beyond the end of the specified area.
     * The last-visited index If the {@link ByteBufferProcessor#process(byte)} returned {@code false}.
     */
    int forEachByte(int index, int length, ByteBufferProcessor processor);

    /**
     * Iterates over the readable bytes of this buffer with the specified {@code processor} in descending order.
     *
     * @return {@code -1} if the processor iterated to or beyond the beginning of the readable bytes.
     * The last-visited index If the {@link ByteBufferProcessor#process(byte)} returned {@code false}.
     */
    int forEachByteDesc(ByteBufferProcessor processor);

    /**
     * Iterates over the specified area of this buffer with the specified {@code processor} in descending order.
     * (i.e. {@code (index + length - 1)}, {@code (index + length - 2)}, ... {@code index})
     *
     * @return {@code -1} if the processor iterated to or beyond the beginning of the specified area.
     * The last-visited index If the {@link ByteBufferProcessor#process(byte)} returned {@code false}.
     */
    int forEachByteDesc(int index, int length, ByteBufferProcessor processor);

    /**
     * Returns a copy of this buffer's readable bytes.  Modifying the content
     * of the returned buffer or this buffer does not affect each other at all.
     * This method is identical to {@code buf.copy(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    ByteBuffer copy();

    /**
     * Returns a copy of this buffer's sub-region.  Modifying the content of
     * the returned buffer or this buffer does not affect each other at all.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    ByteBuffer copy(int index, int length);

    /**
     * Returns a slice of this buffer's readable bytes. Modifying the content
     * of the returned buffer or this buffer affects each other's content
     * while they maintain separate indexes and marks.  This method is
     * identical to {@code buf.slice(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     */
    ByteBuffer slice();

    /**
     * Returns a slice of this buffer's sub-region. Modifying the content of
     * the returned buffer or this buffer affects each other's content while
     * they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     */
    ByteBuffer slice(int index, int length);

    /**
     * Returns a buffer which shares the whole region of this buffer.
     * Modifying the content of the returned buffer or this buffer affects
     * each other's content while they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * The reader and writer marks will not be duplicated. Also be aware that this method will
     * NOT call {@link #retain()} and so the reference count will NOT be increased.
     *
     * @return A buffer whose readable content is equivalent to the buffer returned by {@link #slice()}.
     * However this buffer will share the capacity of the underlying buffer, and therefore allows access to all of the
     * underlying content if necessary.
     */
    ByteBuffer duplicate();

    /**
     * Returns the maximum number of NIO {@link java.nio.ByteBuffer}s that consist this buffer.  Note that {@link #nioBuffers()}
     * or {@link #nioBuffers(int, int)} might return a less number of {@link java.nio.ByteBuffer}s.
     *
     * @return {@code -1} if this buffer has no underlying {@link java.nio.ByteBuffer}.
     * the number of the underlying {@link java.nio.ByteBuffer}s if this buffer has at least one underlying
     * {@link java.nio.ByteBuffer}.  Note that this method does not return {@code 0} to avoid confusion.
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    int nioBufferCount();

    /**
     * Exposes this buffer's readable bytes as an NIO {@link java.nio.ByteBuffer}. The returned buffer
     * either share or contains the copied content of this buffer, while changing the position
     * and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method is identical to {@code buf.nioBuffer(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     * Please note that the returned NIO buffer will not see the changes of this buffer if this buffer
     * is a dynamic buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     * @see #nioBufferCount()
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    java.nio.ByteBuffer nioBuffer();

    /**
     * Exposes this buffer's sub-region as an NIO {@link java.nio.ByteBuffer}. The returned buffer
     * either share or contains the copied content of this buffer, while changing the position
     * and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     * Please note that the returned NIO buffer will not see the changes of this buffer if this buffer
     * is a dynamic buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     * @see #nioBufferCount()
     * @see #nioBuffers()
     * @see #nioBuffers(int, int)
     */
    java.nio.ByteBuffer nioBuffer(int index, int length);

    /**
     * Internal use only: Exposes the internal NIO buffer.
     */
    java.nio.ByteBuffer internalNioBuffer(int index, int length);

    /**
     * Exposes this buffer's readable bytes as an NIO {@link java.nio.ByteBuffer}'s. The returned buffer
     * either share or contains the copied content of this buffer, while changing the position
     * and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer.
     * Please note that the returned NIO buffer will not see the changes of this buffer if this buffer
     * is a dynamic buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     * @see #nioBufferCount()
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     */
    java.nio.ByteBuffer[] nioBuffers();

    /**
     * Exposes this buffer's bytes as an NIO {@link java.nio.ByteBuffer}'s for the specified index and length
     * The returned buffer either share or contains the copied content of this buffer, while changing
     * the position and limit of the returned NIO buffer does not affect the indexes and marks of this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of this buffer. Please note that the
     * returned NIO buffer will not see the changes of this buffer if this buffer is a dynamic
     * buffer and it adjusted its capacity.
     *
     * @throws UnsupportedOperationException if this buffer cannot create a {@link java.nio.ByteBuffer} that shares the content with itself
     * @see #nioBufferCount()
     * @see #nioBuffer()
     * @see #nioBuffer(int, int)
     */
    java.nio.ByteBuffer[] nioBuffers(int index, int length);

    /**
     * Returns {@code true} if and only if this buffer has a backing byte array.
     * If this method returns true, you can safely call {@link #array()} and
     * {@link #arrayOffset()}.
     */
    boolean hasArray();


    /**
     * 如果当前缓冲区基于堆数组实现，则返回载体对应的字节数组，否则返回{@code null}
     *
     * @return 载体数组
     * @throws UnsupportedOperationException 如果后端不是基于堆数组的则抛出异常
     */
    byte[] array();

    /**
     * 如果当前缓冲区基于堆数组实现，则返回数组偏移索引，否则返回{@code -1}
     *
     * @return 数组偏移索引
     * @throws UnsupportedOperationException 如果后端不是基于堆数组的则抛出异常
     */
    int arrayOffset();

    /**
     * Returns {@code true} if and only if this buffer has a reference to the low-level memory address that points
     * to the backing data.
     */
    boolean hasMemoryAddress();

    /**
     * Returns the low-level memory address that point to the first byte of ths backing data.
     *
     * @throws UnsupportedOperationException if this buffer does not support accessing the low-level memory address
     */
    long memoryAddress();

    /**
     * Decodes this buffer's readable bytes into a string with the specified
     * character set name.  This method is identical to
     * {@code buf.toString(buf.readerIndex(), buf.readableBytes(), charsetName)}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws UnsupportedCharsetException if the specified character set name is not supported by the
     *                                     current VM
     */
    String toString(Charset charset);

    /**
     * 将指定起始位置，指定长度的缓冲区内容以字符串形式输出，不改变读取游标的位置
     *
     * @param index   起始位置索引
     * @param length  内容长度
     * @param charset 字符集对象
     * @return 以charset编码的字符串内容
     */
    String toString(int index, int length, Charset charset);

    /**
     * Returns a hash code which was calculated from the content of this
     * buffer.  If there's a byte array which is
     * {@linkplain #equals(Object) equal to} this array, both arrays should
     * return the same value.
     */
    @Override
    int hashCode();

    /**
     * Determines if the content of the specified buffer is identical to the
     * content of this array.  'Identical' here means:
     * <ul>
     * <li>the size of the contents of the two buffers are same and</li>
     * <li>every single byte of the content of the two buffers are same.</li>
     * </ul>
     * Please note that it does not compare {@link #readerIndex()} nor
     * {@link #writerIndex()}.  This method also returns {@code false} for
     * {@code null} and an object which is not an instance of
     * {@link ByteBuffer} type.
     */
    @Override
    boolean equals(Object obj);

    /**
     * Compares the content of the specified buffer to the content of this
     * buffer. Comparison is performed in the same manner with the string
     * comparison functions of various languages such as {@code strcmp},
     * {@code memcmp} and {@link String#compareTo(String)}.
     */
    @Override
    int compareTo(ByteBuffer buffer);

    /**
     * Returns the string representation of this buffer.  This method does not
     * necessarily return the whole content of the buffer but returns
     * the values of the key properties such as {@link #readerIndex()},
     * {@link #writerIndex()} and {@link #capacity()}.
     */
    @Override
    String toString();

    @Override
    ByteBuffer retain(int increment);

    @Override
    ByteBuffer retain();


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
