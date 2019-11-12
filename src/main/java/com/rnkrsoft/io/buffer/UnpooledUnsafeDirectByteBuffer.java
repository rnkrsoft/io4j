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

/**
 * A NIO {@link java.nio.ByteBuffer} based buffer. It is recommended to use
 * {@link UnpooledByteBufferAllocator#directBuffer(int, int)}, {@link Unpooled#directBuffer(int)} and
 * {@link Unpooled#wrappedBuffer(java.nio.ByteBuffer)} instead of calling the constructor explicitly.}
 */
public class UnpooledUnsafeDirectByteBuffer extends AbstractReferenceCountedByteBuffer {

    private final ByteBufferAllocator alloc;

    private java.nio.ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;
    java.nio.ByteBuffer buffer;
    long memoryAddress;

    /**
     * Creates a new direct buffer.
     *
     * @param initialCapacity the initial capacity of the underlying direct buffer
     * @param maxCapacity     the maximum capacity of the underlying direct buffer
     */
    public UnpooledUnsafeDirectByteBuffer(ByteBufferAllocator alloc, int initialCapacity, int maxCapacity) {
        super(maxCapacity);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity: " + initialCapacity);
        }
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("maxCapacity: " + maxCapacity);
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }

        this.alloc = alloc;
        setByteBuffer(allocateDirect(initialCapacity), false);
    }

    /**
     * Creates a new direct buffer by wrapping the specified initial buffer.
     *
     * @param maxCapacity the maximum capacity of the underlying direct buffer
     */
    protected UnpooledUnsafeDirectByteBuffer(ByteBufferAllocator alloc, java.nio.ByteBuffer initialBuffer, int maxCapacity) {
        // We never try to free the buffer if it was provided by the end-user as we not know if this is an duplicate or
        // an slice. This is done to prevent an IllegalArgumentException when using Java9 as Unsafe.invokeCleaner(...)
        // will check if the given buffer is either an duplicate or slice and in this case throw an
        // IllegalArgumentException.
        //
        // See http://hg.openjdk.java.net/jdk9/hs-demo/jdk/file/0d2ab72ba600/src/jdk.unsupported/share/classes/
        // sun/misc/Unsafe.java#l1250
        //
        // We also call slice() explicitly here to preserve behaviour with previous netty releases.
        this(alloc, initialBuffer.slice(), maxCapacity, false);
    }

    UnpooledUnsafeDirectByteBuffer(ByteBufferAllocator alloc, java.nio.ByteBuffer initialBuffer, int maxCapacity, boolean doFree) {
        super(maxCapacity);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (initialBuffer == null) {
            throw new NullPointerException("initialBuffer");
        }
        if (!initialBuffer.isDirect()) {
            throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
        }
        if (initialBuffer.isReadOnly()) {
            throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
        }

        int initialCapacity = initialBuffer.remaining();
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }

        this.alloc = alloc;
        doNotFree = !doFree;
        setByteBuffer(initialBuffer.order(ByteOrder.BIG_ENDIAN), false);
        writerIndex(initialCapacity);
    }

    /**
     * Allocate a new direct {@link java.nio.ByteBuffer} with the given initialCapacity.
     */
    protected java.nio.ByteBuffer allocateDirect(int initialCapacity) {
        return java.nio.ByteBuffer.allocateDirect(initialCapacity);
    }

    /**
     * Free a direct {@link java.nio.ByteBuffer}
     */
    protected void freeDirect(java.nio.ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer(buffer);
    }

    final void setByteBuffer(java.nio.ByteBuffer buffer, boolean tryFree) {
        if (tryFree) {
            java.nio.ByteBuffer oldBuffer = this.buffer;
            if (oldBuffer != null) {
                if (doNotFree) {
                    doNotFree = false;
                } else {
                    freeDirect(oldBuffer);
                }
            }
        }
        this.buffer = buffer;
        memoryAddress = PlatformDependent.directBufferAddress(buffer);
        tmpNioBuf = null;
        capacity = buffer.remaining();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        checkNewCapacity(newCapacity);

        int readerIndex = readerIndex();
        int writerIndex = writerIndex();

        int oldCapacity = capacity;
        if (newCapacity > oldCapacity) {
            java.nio.ByteBuffer oldBuffer = buffer;
            java.nio.ByteBuffer newBuffer = allocateDirect(newCapacity);
            oldBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.put(oldBuffer);
            newBuffer.clear();
            setByteBuffer(newBuffer, true);
        } else if (newCapacity < oldCapacity) {
            java.nio.ByteBuffer oldBuffer = buffer;
            java.nio.ByteBuffer newBuffer = allocateDirect(newCapacity);
            if (readerIndex < newCapacity) {
                if (writerIndex > newCapacity) {
                    writerIndex(writerIndex = newCapacity);
                }
                oldBuffer.position(readerIndex).limit(writerIndex);
                newBuffer.position(readerIndex).limit(writerIndex);
                newBuffer.put(oldBuffer);
                newBuffer.clear();
            } else {
                setIndex(newCapacity, newCapacity);
            }
            setByteBuffer(newBuffer, true);
        }
        return this;
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
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public boolean hasMemoryAddress() {
        return true;
    }

    @Override
    public long memoryAddress() {
        ensureAccessible();
        return memoryAddress;
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufferUtil.getByte(addr(index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufferUtil.getShort(addr(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufferUtil.getUnsignedMedium(addr(index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufferUtil.getInt(addr(index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufferUtil.getLong(addr(index));
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        UnsafeByteBufferUtil.getBytes(this, addr(index), index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        UnsafeByteBufferUtil.getBytes(this, addr(index), index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        UnsafeByteBufferUtil.getBytes(this, addr(index), index, dst);
        return this;
    }

    @Override
    public ByteBuffer readBytes(java.nio.ByteBuffer dst) {
        int length = dst.remaining();
        checkReadableBytes(length);
        getBytes(readerIndex, dst);
        readerIndex += length;
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufferUtil.setByte(addr(index), value);
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufferUtil.setShort(addr(index), value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufferUtil.setMedium(addr(index), value);
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufferUtil.setInt(addr(index), value);
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufferUtil.setLong(addr(index), value);
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        UnsafeByteBufferUtil.setBytes(this, addr(index), index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        UnsafeByteBufferUtil.setBytes(this, addr(index), index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        UnsafeByteBufferUtil.setBytes(this, addr(index), index, src);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        UnsafeByteBufferUtil.getBytes(this, addr(index), index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        ensureAccessible();
        if (length == 0) {
            return 0;
        }

        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = buffer.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        checkReadableBytes(length);
        int readBytes = getBytes(readerIndex, out, length, true);
        readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return UnsafeByteBufferUtil.setBytes(this, addr(index), index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        ensureAccessible();
        java.nio.ByteBuffer tmpBuf = internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf);
        } catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[] { nioBuffer(index, length) };
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        return UnsafeByteBufferUtil.copy(this, addr(index), index, length);
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        checkIndex(index, length);
        return (java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
    }

    private java.nio.ByteBuffer internalNioBuffer() {
        java.nio.ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = buffer.duplicate();
        }
        return tmpNioBuf;
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        return ((java.nio.ByteBuffer) buffer.duplicate().position(index).limit(index + length)).slice();
    }

    @Override
    protected void deallocate() {
        java.nio.ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            return;
        }

        this.buffer = null;

        if (!doNotFree) {
            freeDirect(buffer);
        }
    }

    @Override
    public ByteBuffer unwrap() {
        return null;
    }

    long addr(int index) {
        return memoryAddress + index;
    }

    @Override
    protected SwappedByteBuffer newSwappedByteBuf() {
        if (PlatformDependent.isUnaligned()) {
            // Only use if unaligned access is supported otherwise there is no gain.
            return new UnsafeDirectSwappedByteBuffer(this);
        }
        return super.newSwappedByteBuf();
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        checkIndex(index, length);
        UnsafeByteBufferUtil.setZero(addr(index), length);
        return this;
    }

    @Override
    public ByteBuffer writeZero(int length) {
        ensureWritable(length);
        int wIndex = writerIndex;
        UnsafeByteBufferUtil.setZero(addr(wIndex), length);
        writerIndex = wIndex + length;
        return this;
    }
}
