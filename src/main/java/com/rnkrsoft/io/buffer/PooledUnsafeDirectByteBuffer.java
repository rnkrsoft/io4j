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

import com.rnkrsoft.io.buffer.util.Recycler;
import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledUnsafeDirectByteBuffer extends PooledByteBuffer<java.nio.ByteBuffer> {
    private static final Recycler<PooledUnsafeDirectByteBuffer> RECYCLER = new Recycler<PooledUnsafeDirectByteBuffer>() {
        @Override
        protected PooledUnsafeDirectByteBuffer newObject(Handle handle) {
            return new PooledUnsafeDirectByteBuffer(handle, 0);
        }
    };

    static PooledUnsafeDirectByteBuffer newInstance(int maxCapacity) {
        PooledUnsafeDirectByteBuffer buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    private long memoryAddress;

    private PooledUnsafeDirectByteBuffer(Recycler.Handle recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    void init(PoolChunk<java.nio.ByteBuffer> chunk, long handle, int offset, int length, int maxLength,
              PoolThreadCache cache) {
        super.init(chunk, handle, offset, length, maxLength, cache);
        initMemoryAddress();
    }

    @Override
    void initUnpooled(PoolChunk<java.nio.ByteBuffer> chunk, int length) {
        super.initUnpooled(chunk, length);
        initMemoryAddress();
    }

    private void initMemoryAddress() {
        memoryAddress = PlatformDependent.directBufferAddress(memory) + offset;
    }

    @Override
    protected java.nio.ByteBuffer newInternalNioBuffer(java.nio.ByteBuffer memory) {
        return memory.duplicate();
    }

    @Override
    public boolean isDirect() {
        return true;
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
    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        UnsafeByteBufferUtil.getBytes(this, addr(index), index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        checkIndex(index, length);
        if (length == 0) {
            return 0;
        }

        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = memory.duplicate();
        }
        index = idx(index);
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length)
            throws IOException {
        checkReadableBytes(length);
        int readBytes = getBytes(readerIndex, out, length, true);
        readerIndex += readBytes;
        return readBytes;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufferUtil.setByte(addr(index), (byte) value);
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
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return UnsafeByteBufferUtil.setBytes(this, addr(index), index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        checkIndex(index, length);
        java.nio.ByteBuffer tmpBuf = internalNioBuffer();
        index = idx(index);
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf);
        } catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        return UnsafeByteBufferUtil.copy(this, addr(index), index, length);
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
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        index = idx(index);
        return ((java.nio.ByteBuffer) memory.duplicate().position(index).limit(index + length)).slice();
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        checkIndex(index, length);
        index = idx(index);
        return (java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
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

    private long addr(int index) {
        return memoryAddress + index;
    }

    @Override
    protected Recycler<?> recycler() {
        return RECYCLER;
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
