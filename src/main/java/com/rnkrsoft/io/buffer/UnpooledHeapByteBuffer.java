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

import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

import static com.rnkrsoft.io.buffer.util.internal.ObjectUtil.checkNotNull;

/**
 * Big endian Java heap buffer implementation. It is recommended to use
 * {@link UnpooledByteBufferAllocator#heapBuffer(int, int)}, {@link Unpooled#buffer(int)} and
 * {@link Unpooled#wrappedBuffer(byte[])} instead of calling the constructor explicitly.
 */
class UnpooledHeapByteBuffer extends AbstractReferenceCountedByteBuffer {

    private final ByteBufferAllocator alloc;
    byte[] array;
    private java.nio.ByteBuffer tmpNioBuf;

    /**
     * Creates a new heap buffer with a newly allocated byte array.
     *
     * @param initialCapacity the initial capacity of the underlying byte array
     * @param maxCapacity the max capacity of the underlying byte array
     */
    public UnpooledHeapByteBuffer(ByteBufferAllocator alloc, int initialCapacity, int maxCapacity) {
        super(maxCapacity);

        checkNotNull(alloc, "alloc");

        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }

        this.alloc = alloc;
        setArray(allocateArray(initialCapacity));
        setIndex(0, 0);
    }

    /**
     * Creates a new heap buffer with an existing byte array.
     *
     * @param initialArray the initial underlying byte array
     * @param maxCapacity the max capacity of the underlying byte array
     */
    protected UnpooledHeapByteBuffer(ByteBufferAllocator alloc, byte[] initialArray, int maxCapacity) {
        super(maxCapacity);

        checkNotNull(alloc, "alloc");
        checkNotNull(initialArray, "initialArray");

        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity(%d) > maxCapacity(%d)", initialArray.length, maxCapacity));
        }

        this.alloc = alloc;
        setArray(initialArray);
        setIndex(0, initialArray.length);
    }

    byte[] allocateArray(int initialCapacity) {
        return new byte[initialCapacity];
    }

    void freeArray(byte[] array) {
        // NOOP
    }

    private void setArray(byte[] initialArray) {
        array = initialArray;
        tmpNioBuf = null;
    }

    @Override
    public ByteBufferAllocator alloc() {
        return alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public int capacity() {
        ensureAccessible();
        return array.length;
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        checkNewCapacity(newCapacity);

        int oldCapacity = array.length;
        byte[] oldArray = array;
        if (newCapacity > oldCapacity) {
            byte[] newArray = allocateArray(newCapacity);
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            setArray(newArray);
            freeArray(oldArray);
        } else if (newCapacity < oldCapacity) {
            byte[] newArray = allocateArray(newCapacity);
            int readerIndex = readerIndex();
            if (readerIndex < newCapacity) {
                int writerIndex = writerIndex();
                if (writerIndex > newCapacity) {
                    writerIndex(writerIndex = newCapacity);
                }
                System.arraycopy(oldArray, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
            } else {
                setIndex(newCapacity, newCapacity);
            }
            setArray(newArray);
            freeArray(oldArray);
        }
        return this;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        ensureAccessible();
        return array;
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory(array, index, dst.memoryAddress() + dstIndex, length);
        } else if (dst.hasArray()) {
            getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else {
            dst.setBytes(dstIndex, array, index, length);
        }
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.length);
        System.arraycopy(array, index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        checkIndex(index, dst.remaining());
        dst.put(array, index, dst.remaining());
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        ensureAccessible();
        out.write(array, index, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        ensureAccessible();
        return getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        ensureAccessible();
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = java.nio.ByteBuffer.wrap(array);
        }
        return out.write((java.nio.ByteBuffer) tmpBuf.clear().position(index).limit(index + length));
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        checkReadableBytes(length);
        int readBytes = getBytes(readerIndex, out, length, true);
        readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.hasMemoryAddress()) {
            PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, array, index, length);
        } else  if (src.hasArray()) {
            setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
        } else {
            src.getBytes(srcIndex, array, index, length);
        }
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.length);
        System.arraycopy(src, srcIndex, array, index, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        ensureAccessible();
        src.get(array, index, src.remaining());
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        ensureAccessible();
        return in.read(array, index, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        ensureAccessible();
        try {
            return in.read((java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length));
        } catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        ensureAccessible();
        return java.nio.ByteBuffer.wrap(array, index, length).slice();
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[] { nioBuffer(index, length) };
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        checkIndex(index, length);
        return (java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
    }

    @Override
    public byte getByte(int index) {
        ensureAccessible();
        return _getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return HeapByteBufferUtil.getByte(array, index);
    }

    @Override
    public short getShort(int index) {
        ensureAccessible();
        return _getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return HeapByteBufferUtil.getShort(array, index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        ensureAccessible();
        return _getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return HeapByteBufferUtil.getUnsignedMedium(array, index);
    }

    @Override
    public int getInt(int index) {
        ensureAccessible();
        return _getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return HeapByteBufferUtil.getInt(array, index);
    }

    @Override
    public long getLong(int index) {
        ensureAccessible();
        return _getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return HeapByteBufferUtil.getLong(array, index);
    }

    @Override
    public ByteBuffer setByte(int index, int value) {
        ensureAccessible();
        _setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufferUtil.setByte(array, index, value);
    }

    @Override
    public ByteBuffer setShort(int index, int value) {
        ensureAccessible();
        _setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        HeapByteBufferUtil.setShort(array, index, value);
    }

    @Override
    public ByteBuffer setMedium(int index, int   value) {
        ensureAccessible();
        _setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        HeapByteBufferUtil.setMedium(array, index, value);
    }

    @Override
    public ByteBuffer setInt(int index, int   value) {
        ensureAccessible();
        _setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        HeapByteBufferUtil.setInt(array, index, value);
    }

    @Override
    public ByteBuffer setLong(int index, long  value) {
        ensureAccessible();
        _setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        HeapByteBufferUtil.setLong(array, index, value);
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        checkIndex(index, length);
        byte[] copiedArray = new byte[length];
        System.arraycopy(array, index, copiedArray, 0, length);
        return new UnpooledHeapByteBuffer(alloc(), copiedArray, maxCapacity());
    }

    private java.nio.ByteBuffer internalNioBuffer() {
        java.nio.ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = java.nio.ByteBuffer.wrap(array);
        }
        return tmpNioBuf;
    }

    @Override
    protected void deallocate() {
        freeArray(array);
        array = null;
    }

    @Override
    public ByteBuffer unwrap() {
        return null;
    }
}
