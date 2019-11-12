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

import java.nio.ByteOrder;

/**
 * Special {@link SwappedByteBuffer} for {@link ByteBuffer}s that is using unsafe.
 */
abstract class AbstractUnsafeSwappedByteBuffer extends SwappedByteBuffer {
    private final boolean nativeByteOrder;
    private final AbstractByteBuffer wrapped;

    AbstractUnsafeSwappedByteBuffer(AbstractByteBuffer buf) {
        super(buf);
        assert PlatformDependent.isUnaligned();
        wrapped = buf;
        nativeByteOrder = UnsafeByteBufferUtil.BIG_ENDIAN_NATIVE_ORDER == (order() == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public final long getLong(int index) {
        wrapped.checkIndex(index, 8);
        long v = _getLong(wrapped, index);
        return nativeByteOrder ? v : Long.reverseBytes(v);
    }

    @Override
    public final float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override
    public final double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    @Override
    public final char getChar(int index) {
        return (char) getShort(index);
    }

    @Override
    public final long getUnsignedInt(int index) {
        return getInt(index) & 0xFFFFFFFFL;
    }

    @Override
    public final int getInt(int index) {
        wrapped.checkIndex0(index, 4);
        int v = _getInt(wrapped, index);
        return nativeByteOrder ? v : Integer.reverseBytes(v);
    }

    @Override
    public final int getUnsignedShort(int index) {
        return getShort(index) & 0xFFFF;
    }

    @Override
    public final short getShort(int index) {
        wrapped.checkIndex0(index, 2);
        short v = _getShort(wrapped, index);
        return nativeByteOrder ? v : Short.reverseBytes(v);
    }

    @Override
    public final ByteBuffer setShort(int index, int value) {
        wrapped.checkIndex0(index, 2);
        _setShort(wrapped, index, nativeByteOrder ? (short) value : Short.reverseBytes((short) value));
        return this;
    }

    @Override
    public final ByteBuffer setInt(int index, int value) {
        wrapped.checkIndex0(index, 4);
        _setInt(wrapped, index, nativeByteOrder ? value : Integer.reverseBytes(value));
        return this;
    }

    @Override
    public final ByteBuffer setLong(int index, long value) {
        wrapped.checkIndex(index, 8);
        _setLong(wrapped, index, nativeByteOrder ? value : Long.reverseBytes(value));
        return this;
    }

    @Override
    public final ByteBuffer setChar(int index, int value) {
        setShort(index, value);
        return this;
    }

    @Override
    public final ByteBuffer setFloat(int index, float value) {
        setInt(index, Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public final ByteBuffer setDouble(int index, double value) {
        setLong(index, Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public final ByteBuffer writeShort(int value) {
        wrapped.ensureWritable0(2);
        _setShort(wrapped, wrapped.writerIndex, nativeByteOrder ? (short) value : Short.reverseBytes((short) value));
        wrapped.writerIndex += 2;
        return this;
    }

    @Override
    public final ByteBuffer writeInt(int value) {
        wrapped.ensureWritable0(4);
        _setInt(wrapped, wrapped.writerIndex, nativeByteOrder ? value : Integer.reverseBytes(value));
        wrapped.writerIndex += 4;
        return this;
    }

    @Override
    public final ByteBuffer writeLong(long value) {
        wrapped.ensureWritable0(8);
        _setLong(wrapped, wrapped.writerIndex, nativeByteOrder ? value : Long.reverseBytes(value));
        wrapped.writerIndex += 8;
        return this;
    }

    @Override
    public final ByteBuffer writeChar(int value) {
        writeShort(value);
        return this;
    }

    @Override
    public final ByteBuffer writeFloat(float value) {
        writeInt(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public final ByteBuffer writeDouble(double value) {
        writeLong(Double.doubleToRawLongBits(value));
        return this;
    }

    protected abstract short _getShort(AbstractByteBuffer wrapped, int index);
    protected abstract int _getInt(AbstractByteBuffer wrapped, int index);
    protected abstract long _getLong(AbstractByteBuffer wrapped, int index);
    protected abstract void _setShort(AbstractByteBuffer wrapped, int index, short value);
    protected abstract void _setInt(AbstractByteBuffer wrapped, int index, int value);
    protected abstract void _setLong(AbstractByteBuffer wrapped, int index, long value);
}
