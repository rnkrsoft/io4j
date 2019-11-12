/*
 * Copyright 2015 The Netty Project
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
import com.rnkrsoft.io.buffer.util.Recycler.Handle;
import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

final class PooledUnsafeHeapByteBuffer extends PooledHeapByteBuffer {

    private static final Recycler<PooledUnsafeHeapByteBuffer> RECYCLER = new Recycler<PooledUnsafeHeapByteBuffer>() {
        @Override
        protected PooledUnsafeHeapByteBuffer newObject(Handle handle) {
            return new PooledUnsafeHeapByteBuffer(handle, 0);
        }
    };

    static PooledUnsafeHeapByteBuffer newUnsafeInstance(int maxCapacity) {
        PooledUnsafeHeapByteBuffer buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    private PooledUnsafeHeapByteBuffer(Handle recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufferUtil.getByte(memory, idx(index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufferUtil.getShort(memory, idx(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufferUtil.getUnsignedMedium(memory, idx(index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufferUtil.getInt(memory, idx(index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufferUtil.getLong(memory, idx(index));
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufferUtil.setByte(memory, idx(index), value);
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufferUtil.setShort(memory, idx(index), value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufferUtil.setMedium(memory, idx(index), value);
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufferUtil.setInt(memory, idx(index), value);
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufferUtil.setLong(memory, idx(index), value);
    }

    @Override
    protected Recycler<?> recycler() {
        return RECYCLER;
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            checkIndex(index, length);
            // Only do on java7+ as the needed Unsafe call was only added there.
            UnsafeByteBufferUtil.setZero(memory, idx(index), length);
            return this;
        }
        return super.setZero(index, length);
    }

    @Override
    public ByteBuffer writeZero(int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            // Only do on java7+ as the needed Unsafe call was only added there.
            ensureWritable(length);
            int wIndex = writerIndex;
            UnsafeByteBufferUtil.setZero(memory, idx(wIndex), length);
            writerIndex = wIndex + length;
            return this;
        }
        return super.writeZero(length);
    }

    @Override
    @Deprecated
    protected SwappedByteBuffer newSwappedByteBuf() {
        if (PlatformDependent.isUnaligned()) {
            // Only use if unaligned access is supported otherwise there is no gain.
            return new UnsafeHeapSwappedByteBuffer(this);
        }
        return super.newSwappedByteBuf();
    }
}
