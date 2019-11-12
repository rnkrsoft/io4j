/*
 * Copyright 2013 The Netty Project
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

import com.rnkrsoft.io.buffer.util.internal.EmptyArrays;
import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;
import com.rnkrsoft.io.buffer.util.internal.StringUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * An empty {@link ByteBuffer} whose capacity and maximum capacity are all {@code 0}.
 */
public final class EmptyByteBuffer extends ByteBuffer {

    private static final java.nio.ByteBuffer EMPTY_BYTE_BUFFER = java.nio.ByteBuffer.allocateDirect(0);
    private static final long EMPTY_BYTE_BUFFER_ADDRESS;

    static {
        long emptyByteBufferAddress = 0;
        try {
            if (PlatformDependent.hasUnsafe()) {
                emptyByteBufferAddress = PlatformDependent.directBufferAddress(EMPTY_BYTE_BUFFER);
            }
        } catch (Throwable t) {
            // Ignore
        }
        EMPTY_BYTE_BUFFER_ADDRESS = emptyByteBufferAddress;
    }

    private final ByteBufferAllocator alloc;
    private final ByteOrder order;
    private final String str;
    private EmptyByteBuffer swapped;

    public EmptyByteBuffer(ByteBufferAllocator alloc) {
        this(alloc, ByteOrder.BIG_ENDIAN);
    }

    private EmptyByteBuffer(ByteBufferAllocator alloc, ByteOrder order) {
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }

        this.alloc = alloc;
        this.order = order;
        str = StringUtil.simpleClassName(this) + (order == ByteOrder.BIG_ENDIAN? "BE" : "LE");
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufferAllocator alloc() {
        return alloc;
    }

    @Override
    public ByteOrder order() {
        return order;
    }

    @Override
    public ByteBuffer unwrap() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int maxCapacity() {
        return 0;
    }

    @Override
    public ByteBuffer order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (endianness == order()) {
            return this;
        }

        EmptyByteBuffer swapped = this.swapped;
        if (swapped != null) {
            return swapped;
        }

        this.swapped = swapped = new EmptyByteBuffer(alloc(), endianness);
        return swapped;
    }

    @Override
    public int readerIndex() {
        return 0;
    }

    @Override
    public ByteBuffer readerIndex(int readerIndex) {
        return checkIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return 0;
    }

    @Override
    public ByteBuffer writerIndex(int writerIndex) {
        return checkIndex(writerIndex);
    }

    @Override
    public ByteBuffer setIndex(int readerIndex, int writerIndex) {
        checkIndex(readerIndex);
        checkIndex(writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return 0;
    }

    @Override
    public int writableBytes() {
        return 0;
    }

    @Override
    public int maxWritableBytes() {
        return 0;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public ByteBuffer clear() {
        return this;
    }

    @Override
    public ByteBuffer markReaderIndex() {
        return this;
    }

    @Override
    public ByteBuffer resetReaderIndex() {
        return this;
    }

    @Override
    public ByteBuffer markWriterIndex() {
        return this;
    }

    @Override
    public ByteBuffer resetWriterIndex() {
        return this;
    }

    @Override
    public ByteBuffer discardReadBytes() {
        return this;
    }

    @Override
    public ByteBuffer discardSomeReadBytes() {
        return this;
    }

    @Override
    public ByteBuffer ensureWritable(int minWritableBytes) {
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException("minWritableBytes: " + minWritableBytes + " (expected: >= 0)");
        }
        if (minWritableBytes != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException("minWritableBytes: " + minWritableBytes + " (expected: >= 0)");
        }

        if (minWritableBytes == 0) {
            return 0;
        }

        return 1;
    }

    @Override
    public boolean getBoolean(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public byte getByte(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short getUnsignedByte(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short getShort(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getUnsignedShort(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getMedium(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getUnsignedMedium(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getInt(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long getUnsignedInt(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long getLong(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public char getChar(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public float getFloat(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public double getDouble(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst) {
        return checkIndex(index, dst.writableBytes());
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst) {
        return checkIndex(index, dst.length);
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        return checkIndex(index, dst.remaining());
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length) {
        return checkIndex(index, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) {
        checkIndex(index, length);
        return 0;
    }

    @Override
    public ByteBuffer setBoolean(int index, boolean value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setByte(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setShort(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setMedium(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setInt(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setLong(int index, long value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setChar(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setFloat(int index, float value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setDouble(int index, double value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src) {
        return checkIndex(index, src.length);
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        return checkIndex(index, src.remaining());
    }

    @Override
    public int setBytes(int index, InputStream in, int length) {
        checkIndex(index, length);
        return 0;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) {
        checkIndex(index, length);
        return 0;
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        return checkIndex(index, length);
    }

    @Override
    public boolean readBoolean() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public byte readByte() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short readUnsignedByte() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short readShort() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readUnsignedShort() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readMedium() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readUnsignedMedium() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readInt() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long readUnsignedInt() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long readLong() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public char readChar() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public float readFloat() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public double readDouble() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer readBytes(int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer readSlice(int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst) {
        return checkLength(dst.writableBytes());
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer readBytes(byte[] dst) {
        return checkLength(dst.length);
    }

    @Override
    public ByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer readBytes(java.nio.ByteBuffer dst) {
        return checkLength(dst.remaining());
    }

    @Override
    public ByteBuffer readBytes(OutputStream out, int length) {
        return checkLength(length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) {
        checkLength(length);
        return 0;
    }

    @Override
    public ByteBuffer skipBytes(int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer writeBoolean(boolean value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeByte(int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeShort(int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeMedium(int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeInt(int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeLong(long value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeChar(int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeFloat(float value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeDouble(double value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src) {
        return checkLength(src.readableBytes());
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer writeBytes(byte[] src) {
        return checkLength(src.length);
    }

    @Override
    public ByteBuffer writeBytes(byte[] src, int srcIndex, int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuffer writeBytes(java.nio.ByteBuffer src) {
        return checkLength(src.remaining());
    }

    @Override
    public int writeBytes(InputStream in, int length) {
        checkLength(length);
        return 0;
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) {
        checkLength(length);
        return 0;
    }

    @Override
    public ByteBuffer writeZero(int length) {
        return checkLength(length);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        checkIndex(fromIndex);
        checkIndex(toIndex);
        return -1;
    }

    @Override
    public int bytesBefore(byte value) {
        return -1;
    }

    @Override
    public int bytesBefore(int length, byte value) {
        checkLength(length);
        return -1;
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        checkIndex(index, length);
        return -1;
    }

    @Override
    public int forEachByte(ByteBufferProcessor processor) {
        return -1;
    }

    @Override
    public int forEachByte(int index, int length, ByteBufferProcessor processor) {
        checkIndex(index, length);
        return -1;
    }

    @Override
    public int forEachByteDesc(ByteBufferProcessor processor) {
        return -1;
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufferProcessor processor) {
        checkIndex(index, length);
        return -1;
    }

    @Override
    public ByteBuffer copy() {
        return this;
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer slice() {
        return this;
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuffer duplicate() {
        return this;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public java.nio.ByteBuffer nioBuffer() {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        return nioBuffer();
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers() {
        return new java.nio.ByteBuffer[] { EMPTY_BYTE_BUFFER };
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        checkIndex(index, length);
        return nioBuffers();
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        return EmptyArrays.EMPTY_BYTES;
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return EMPTY_BYTE_BUFFER_ADDRESS != 0;
    }

    @Override
    public long memoryAddress() {
        if (hasMemoryAddress()) {
            return EMPTY_BYTE_BUFFER_ADDRESS;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString(Charset charset) {
        return "";
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        checkIndex(index, length);
        return toString(charset);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteBuffer && !((ByteBuffer) obj).isReadable();
    }

    @Override
    public int compareTo(ByteBuffer buffer) {
        return buffer.isReadable()? -1 : 0;
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    public boolean isReadable(int size) {
        return false;
    }

    @Override
    public boolean isWritable(int size) {
        return false;
    }

    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public ByteBuffer retain() {
        return this;
    }

    @Override
    public ByteBuffer retain(int increment) {
        return this;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int decrement) {
        return false;
    }

    private ByteBuffer checkIndex(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    private ByteBuffer checkIndex(int index, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (index != 0 || length != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    private ByteBuffer checkLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length + " (expected: >= 0)");
        }
        if (length != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }
}
