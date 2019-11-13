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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * Wrapper which swap the {@link ByteOrder} of a {@link ByteBuffer}.
 */
class SwappedByteBuffer implements ByteBuffer {

    private final ByteBuffer buf;
    private final ByteOrder order;

    public SwappedByteBuffer(ByteBuffer buf) {
        if (buf == null) {
            throw new NullPointerException("buf");
        }
        this.buf = buf;
        if (buf.order() == ByteOrder.BIG_ENDIAN) {
            order = ByteOrder.LITTLE_ENDIAN;
        } else {
            order = ByteOrder.BIG_ENDIAN;
        }
    }

    @Override
    public ByteOrder order() {
        return order;
    }

    @Override
    public ByteBuffer order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (endianness == order) {
            return this;
        }
        return buf;
    }

    @Override
    public ByteBuffer unwrap() {
        return buf;
    }

    @Override
    public ByteBufferAllocator alloc() {
        return buf.alloc();
    }

    @Override
    public int capacity() {
        return buf.capacity();
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        buf.capacity(newCapacity);
        return this;
    }

    @Override
    public int maxCapacity() {
        return buf.maxCapacity();
    }

    @Override
    public boolean isDirect() {
        return buf.isDirect();
    }

    @Override
    public int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public ByteBuffer readerIndex(int readerIndex) {
        buf.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public ByteBuffer writerIndex(int writerIndex) {
        buf.writerIndex(writerIndex);
        return this;
    }

    @Override
    public ByteBuffer setIndex(int readerIndex, int writerIndex) {
        buf.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public int readableBytesLength() {
        return buf.readableBytesLength();
    }

    @Override
    public int writableBytesLength() {
        return buf.writableBytesLength();
    }

    @Override
    public int maxWritableBytesLength() {
        return buf.maxWritableBytesLength();
    }

    @Override
    public boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return buf.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return buf.isWritable(size);
    }

    @Override
    public ByteBuffer clear() {
        buf.clear();
        return this;
    }

    @Override
    public ByteBuffer markReaderIndex() {
        buf.markReaderIndex();
        return this;
    }

    @Override
    public ByteBuffer resetReaderIndex() {
        buf.resetReaderIndex();
        return this;
    }

    @Override
    public ByteBuffer markWriterIndex() {
        buf.markWriterIndex();
        return this;
    }

    @Override
    public ByteBuffer resetWriterIndex() {
        buf.resetWriterIndex();
        return this;
    }

    @Override
    public ByteBuffer discardReadBytes() {
        buf.discardReadBytes();
        return this;
    }

    @Override
    public ByteBuffer discardSomeReadBytes() {
        buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuffer ensureWritable(int writableBytes) {
        buf.ensureWritable(writableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return ByteBufferUtil.swapShort(buf.getShort(index));
    }

    @Override
    public int getUnsignedShort(int index) {
        return getShort(index) & 0xFFFF;
    }

    @Override
    public int getMedium(int index) {
        return ByteBufferUtil.swapMedium(buf.getMedium(index));
    }

    @Override
    public int getUnsignedMedium(int index) {
        return getMedium(index) & 0xFFFFFF;
    }

    @Override
    public int getInt(int index) {
        return ByteBufferUtil.swapInt(buf.getInt(index));
    }

    @Override
    public long getUnsignedInt(int index) {
        return getInt(index) & 0xFFFFFFFFL;
    }

    @Override
    public long getLong(int index) {
        return ByteBufferUtil.swapLong(buf.getLong(index));
    }

    @Override
    public char getChar(int index) {
        return (char) getShort(index);
    }

    @Override
    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override
    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        buf.getBytes(index, dst, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        buf.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public ByteBuffer setBoolean(int index, boolean value) {
        buf.setBoolean(index, value);
        return this;
    }

    @Override
    public ByteBuffer setByte(int index, int value) {
        buf.setByte(index, value);
        return this;
    }

    @Override
    public ByteBuffer setShort(int index, int value) {
        buf.setShort(index, ByteBufferUtil.swapShort((short) value));
        return this;
    }

    @Override
    public ByteBuffer setMedium(int index, int value) {
        buf.setMedium(index, ByteBufferUtil.swapMedium(value));
        return this;
    }

    @Override
    public ByteBuffer setInt(int index, int value) {
        buf.setInt(index, ByteBufferUtil.swapInt(value));
        return this;
    }

    @Override
    public ByteBuffer setLong(int index, long value) {
        buf.setLong(index, ByteBufferUtil.swapLong(value));
        return this;
    }

    @Override
    public ByteBuffer setChar(int index, int value) {
        setShort(index, value);
        return this;
    }

    @Override
    public ByteBuffer setFloat(int index, float value) {
        setInt(index, Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public ByteBuffer setDouble(int index, double value) {
        setLong(index, Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int length) {
        buf.setBytes(index, src, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        buf.setZero(index, length);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return ByteBufferUtil.swapShort(buf.readShort());
    }

    @Override
    public int readUnsignedShort() {
        return readShort() & 0xFFFF;
    }

    @Override
    public int readMedium() {
        return ByteBufferUtil.swapMedium(buf.readMedium());
    }

    @Override
    public int readUnsignedMedium() {
        return readMedium() & 0xFFFFFF;
    }

    @Override
    public int readInt() {
        return ByteBufferUtil.swapInt(buf.readInt());
    }

    @Override
    public long readUnsignedInt() {
        return readInt() & 0xFFFFFFFFL;
    }

    @Override
    public long readLong() {
        return ByteBufferUtil.swapLong(buf.readLong());
    }

    @Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public ByteBuffer readBytes(int length) {
        return buf.readBytes(length).order(order());
    }

    @Override
    public ByteBuffer readSlice(int length) {
        return buf.readSlice(length).order(order);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int length) {
        buf.readBytes(dst, length);
        return this;
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer readBytes(byte[] dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer readBytes(java.nio.ByteBuffer dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuffer readBytes(OutputStream out, int length) throws IOException {
        buf.readBytes(out, length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public String readString(int length, Charset charset) {
        return null;
    }

    @Override
    public String readStringUTF8(int length) {
        return null;
    }

    @Override
    public ByteBuffer skipBytes(int length) {
        buf.skipBytes(length);
        return this;
    }

    @Override
    public ByteBuffer writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public ByteBuffer writeByte(int value) {
        buf.writeByte(value);
        return this;
    }

    @Override
    public ByteBuffer writeShort(int value) {
        buf.writeShort(ByteBufferUtil.swapShort((short) value));
        return this;
    }

    @Override
    public ByteBuffer writeMedium(int value) {
        buf.writeMedium(ByteBufferUtil.swapMedium(value));
        return this;
    }

    @Override
    public ByteBuffer writeInt(int value) {
        buf.writeInt(ByteBufferUtil.swapInt(value));
        return this;
    }

    @Override
    public ByteBuffer writeLong(long value) {
        buf.writeLong(ByteBufferUtil.swapLong(value));
        return this;
    }

    @Override
    public ByteBuffer writeChar(int value) {
        writeShort(value);
        return this;
    }

    @Override
    public ByteBuffer writeFloat(float value) {
        writeInt(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public ByteBuffer writeDouble(double value) {
        writeLong(Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src, int length) {
        buf.writeBytes(src, length);
        return this;
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer writeBytes(byte[] src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuffer writeBytes(byte[] src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuffer writeBytes(java.nio.ByteBuffer src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public ByteBuffer writeZero(int length) {
        buf.writeZero(length);
        return this;
    }

    @Override
    public ByteBuffer writeString(String string, Charset charset) {
        return null;
    }

    @Override
    public ByteBuffer writeStringUTF8(String string) {
        return null;
    }

    @Override
    public ByteBuffer writelnString(String string, Charset charset) {
        return null;
    }

    @Override
    public ByteBuffer writelnStringUTF8(String string) {
        return null;
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteBufferProcessor processor) {
        return buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufferProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteBufferProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufferProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuffer copy() {
        return buf.copy().order(order);
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        return buf.copy(index, length).order(order);
    }

    @Override
    public ByteBuffer slice() {
        return buf.slice().order(order);
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return buf.slice(index, length).order(order);
    }

    @Override
    public ByteBuffer duplicate() {
        return buf.duplicate().order(order);
    }

    @Override
    public int nioBufferCount() {
        return buf.nioBufferCount();
    }

    @Override
    public java.nio.ByteBuffer nioBuffer() {
        return buf.nioBuffer().order(order);
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        return buf.nioBuffer(index, length).order(order);
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        return nioBuffer(index, length);
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers() {
        java.nio.ByteBuffer[] nioBuffers = buf.nioBuffers();
        for (int i = 0; i < nioBuffers.length; i++) {
            nioBuffers[i] = nioBuffers[i].order(order);
        }
        return nioBuffers;
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        java.nio.ByteBuffer[] nioBuffers = buf.nioBuffers(index, length);
        for (int i = 0; i < nioBuffers.length; i++) {
            nioBuffers[i] = nioBuffers[i].order(order);
        }
        return nioBuffers;
    }

    @Override
    public boolean hasArray() {
        return buf.hasArray();
    }

    @Override
    public byte[] array() {
        return buf.array();
    }

    @Override
    public int arrayOffset() {
        return buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return buf.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buf.toString(index, length, charset);
    }

    @Override
    public int refCnt() {
        return buf.refCnt();
    }

    @Override
    public ByteBuffer retain() {
        buf.retain();
        return this;
    }

    @Override
    public InputStream asInputStream() {
        return buf.asInputStream();
    }

    @Override
    public int load(InputStream is) throws IOException {
        return buf.load(is);
    }

    @Override
    public int load(String fileName) throws IOException {
        return buf.load(fileName);
    }

    @Override
    public int store(OutputStream os) throws IOException {
        return buf.store(os);
    }

    @Override
    public int store(String fileName) throws IOException {
        return buf.store(fileName);
    }

    @Override
    public ByteBuffer retain(int increment) {
        buf.retain(increment);
        return this;
    }

    @Override
    public boolean release() {
        return buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ByteBuffer) {
            return ByteBufferUtil.equals(this, (ByteBuffer) obj);
        }
        return false;
    }

    @Override
    public int compareTo(ByteBuffer buffer) {
        return ByteBufferUtil.compare(this, buffer);
    }

    @Override
    public String toString() {
        return "Swapped(" + buf.toString() + ')';
    }
}
