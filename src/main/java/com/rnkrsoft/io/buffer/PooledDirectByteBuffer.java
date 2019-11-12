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

import com.rnkrsoft.io.buffer.util.Recycler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledDirectByteBuffer extends PooledByteBuffer<java.nio.ByteBuffer> {

    private static final Recycler<PooledDirectByteBuffer> RECYCLER = new Recycler<PooledDirectByteBuffer>() {
        @Override
        protected PooledDirectByteBuffer newObject(Handle handle) {
            return new PooledDirectByteBuffer(handle, 0);
        }
    };

    static PooledDirectByteBuffer newInstance(int maxCapacity) {
        PooledDirectByteBuffer buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    private PooledDirectByteBuffer(Recycler.Handle recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
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
        return memory.get(idx(index));
    }

    @Override
    protected short _getShort(int index) {
        return memory.getShort(idx(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        index = idx(index);
        return (memory.get(index) & 0xff) << 16 | (memory.get(index + 1) & 0xff) << 8 | memory.get(index + 2) & 0xff;
    }

    @Override
    protected int _getInt(int index) {
        return memory.getInt(idx(index));
    }

    @Override
    protected long _getLong(int index) {
        return memory.getLong(idx(index));
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasArray()) {
            getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else if (dst.nioBufferCount() > 0) {
            for (java.nio.ByteBuffer bb: dst.nioBuffers(dstIndex, length)) {
                int bbLen = bb.remaining();
                getBytes(index, bb);
                index += bbLen;
            }
        } else {
            dst.setBytes(dstIndex, this, index, length);
        }
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        getBytes(index, dst, dstIndex, length, false);
        return this;
    }

    private void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
        checkDstIndex(index, length, dstIndex, dst.length);
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = memory.duplicate();
        }
        index = idx(index);
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.get(dst, dstIndex, length);
    }

    @Override
    public ByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        checkReadableBytes(length);
        getBytes(readerIndex, dst, dstIndex, length, true);
        readerIndex += length;
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        getBytes(index, dst, false);
        return this;
    }

    private void getBytes(int index, java.nio.ByteBuffer dst, boolean internal) {
        checkIndex(index, dst.remaining());
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = memory.duplicate();
        }
        index = idx(index);
        tmpBuf.clear().position(index).limit(index + dst.remaining());
        dst.put(tmpBuf);
    }

    @Override
    public ByteBuffer readBytes(java.nio.ByteBuffer dst) {
        int length = dst.remaining();
        checkReadableBytes(length);
        getBytes(readerIndex, dst, true);
        readerIndex += length;
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        getBytes(index, out, length, false);
        return this;
    }

    private void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        checkIndex(index, length);
        if (length == 0) {
            return;
        }

        byte[] tmp = new byte[length];
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = memory.duplicate();
        }
        tmpBuf.clear().position(idx(index));
        tmpBuf.get(tmp);
        out.write(tmp);
    }

    @Override
    public ByteBuffer readBytes(OutputStream out, int length) throws IOException {
        checkReadableBytes(length);
        getBytes(readerIndex, out, length, true);
        readerIndex += length;
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
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        checkReadableBytes(length);
        int readBytes = getBytes(readerIndex, out, length, true);
        readerIndex += readBytes;
        return readBytes;
    }

    @Override
    protected void _setByte(int index, int value) {
        memory.put(idx(index), (byte) value);
    }

    @Override
    protected void _setShort(int index, int value) {
        memory.putShort(idx(index), (short) value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        index = idx(index);
        memory.put(index, (byte) (value >>> 16));
        memory.put(index + 1, (byte) (value >>> 8));
        memory.put(index + 2, (byte) value);
    }

    @Override
    protected void _setInt(int index, int value) {
        memory.putInt(idx(index), value);
    }

    @Override
    protected void _setLong(int index, long value) {
        memory.putLong(idx(index), value);
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.hasArray()) {
            setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
        } else if (src.nioBufferCount() > 0) {
            for (java.nio.ByteBuffer bb: src.nioBuffers(srcIndex, length)) {
                int bbLen = bb.remaining();
                setBytes(index, bb);
                index += bbLen;
            }
        } else {
            src.getBytes(srcIndex, this, index, length);
        }
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.length);
        java.nio.ByteBuffer tmpBuf = internalNioBuffer();
        index = idx(index);
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.put(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        checkIndex(index, src.remaining());
        java.nio.ByteBuffer tmpBuf = internalNioBuffer();
        if (src == tmpBuf) {
            src = src.duplicate();
        }

        index = idx(index);
        tmpBuf.clear().position(index).limit(index + src.remaining());
        tmpBuf.put(src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        checkIndex(index, length);
        byte[] tmp = new byte[length];
        int readBytes = in.read(tmp);
        if (readBytes <= 0) {
            return readBytes;
        }
        java.nio.ByteBuffer tmpBuf = internalNioBuffer();
        tmpBuf.clear().position(idx(index));
        tmpBuf.put(tmp, 0, readBytes);
        return readBytes;
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
        checkIndex(index, length);
        ByteBuffer copy = alloc().directBuffer(length, maxCapacity());
        copy.writeBytes(this, index, length);
        return copy;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        index = idx(index);
        return ((java.nio.ByteBuffer) memory.duplicate().position(index).limit(index + length)).slice();
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[] { nioBuffer(index, length) };
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
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Recycler<?> recycler() {
        return RECYCLER;
    }
}
