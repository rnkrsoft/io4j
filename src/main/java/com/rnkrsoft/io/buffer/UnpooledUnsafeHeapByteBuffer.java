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

import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

class UnpooledUnsafeHeapByteBuffer extends UnpooledHeapByteBuffer {

    /**
     * Creates a new heap buffer with a newly allocated byte array.
     *
     * @param initialCapacity the initial capacity of the underlying byte array
     * @param maxCapacity the max capacity of the underlying byte array
     */
    UnpooledUnsafeHeapByteBuffer(ByteBufferAllocator alloc, int initialCapacity, int maxCapacity) {
        super(alloc, initialCapacity, maxCapacity);
    }

    @Override
    byte[] allocateArray(int initialCapacity) {
        return PlatformDependent.allocateUninitializedArray(initialCapacity);
    }

    @Override
    public byte getByte(int index) {
        checkIndex(index);
        return _getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufferUtil.getByte(array, index);
    }

    @Override
    public short getShort(int index) {
        checkIndex(index, 2);
        return _getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufferUtil.getShort(array, index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        checkIndex(index, 3);
        return _getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufferUtil.getUnsignedMedium(array, index);
    }

    @Override
    public int getInt(int index) {
        checkIndex(index, 4);
        return _getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufferUtil.getInt(array, index);
    }

    @Override
    public long getLong(int index) {
        checkIndex(index, 8);
        return _getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufferUtil.getLong(array, index);
    }

    @Override
    public ByteBuffer setByte(int index, int value) {
        checkIndex(index);
        _setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufferUtil.setByte(array, index, value);
    }

    @Override
    public ByteBuffer setShort(int index, int value) {
        checkIndex(index, 2);
        _setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufferUtil.setShort(array, index, value);
    }

    @Override
    public ByteBuffer setMedium(int index, int   value) {
        checkIndex(index, 3);
        _setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufferUtil.setMedium(array, index, value);
    }

    @Override
    public ByteBuffer setInt(int index, int   value) {
        checkIndex(index, 4);
        _setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufferUtil.setInt(array, index, value);
    }

    @Override
    public ByteBuffer setLong(int index, long  value) {
        checkIndex(index, 8);
        _setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufferUtil.setLong(array, index, value);
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            // Only do on java7+ as the needed Unsafe call was only added there.
            checkIndex(index, length);
            UnsafeByteBufferUtil.setZero(array, index, length);
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
            UnsafeByteBufferUtil.setZero(array, wIndex, length);
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
