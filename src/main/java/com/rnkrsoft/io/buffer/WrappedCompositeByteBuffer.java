/*
 * Copyright 2016 The Netty Project
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
import java.util.Iterator;
import java.util.List;

class WrappedCompositeByteBuffer extends CompositeByteBuffer {

    private final CompositeByteBuffer wrapped;

    WrappedCompositeByteBuffer(CompositeByteBuffer wrapped) {
        super(wrapped.alloc());
        this.wrapped = wrapped;
    }

    @Override
    public boolean release() {
        return wrapped.release();
    }

    @Override
    public boolean release(int decrement) {
        return wrapped.release(decrement);
    }

    @Override
    public final int maxCapacity() {
        return wrapped.maxCapacity();
    }

    @Override
    public final int readerIndex() {
        return wrapped.readerIndex();
    }

    @Override
    public final int writerIndex() {
        return wrapped.writerIndex();
    }

    @Override
    public final boolean isReadable() {
        return wrapped.isReadable();
    }

    @Override
    public final boolean isReadable(int numBytes) {
        return wrapped.isReadable(numBytes);
    }

    @Override
    public final boolean isWritable() {
        return wrapped.isWritable();
    }

    @Override
    public final boolean isWritable(int numBytes) {
        return wrapped.isWritable(numBytes);
    }

    @Override
    public final int readableBytesLength() {
        return wrapped.readableBytesLength();
    }

    @Override
    public final int writableBytesLength() {
        return wrapped.writableBytesLength();
    }

    @Override
    public final int maxWritableBytesLength() {
        return wrapped.maxWritableBytesLength();
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return wrapped.ensureWritable(minWritableBytes, force);
    }

    @Override
    public ByteBuffer order(ByteOrder endianness) {
        return wrapped.order(endianness);
    }

    @Override
    public boolean getBoolean(int index) {
        return wrapped.getBoolean(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return wrapped.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return wrapped.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return wrapped.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return wrapped.getUnsignedMedium(index);
    }

    @Override
    public int getMedium(int index) {
        return wrapped.getMedium(index);
    }

    @Override
    public int getInt(int index) {
        return wrapped.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return wrapped.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        return wrapped.getLong(index);
    }

    @Override
    public char getChar(int index) {
        return wrapped.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return wrapped.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return wrapped.getDouble(index);
    }

    @Override
    public byte readByte() {
        return wrapped.readByte();
    }

    @Override
    public boolean readBoolean() {
        return wrapped.readBoolean();
    }

    @Override
    public short readUnsignedByte() {
        return wrapped.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return wrapped.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return wrapped.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return wrapped.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return wrapped.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        return wrapped.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return wrapped.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return wrapped.readLong();
    }

    @Override
    public char readChar() {
        return wrapped.readChar();
    }

    @Override
    public float readFloat() {
        return wrapped.readFloat();
    }

    @Override
    public double readDouble() {
        return wrapped.readDouble();
    }

    @Override
    public ByteBuffer readBytes(int length) {
        return wrapped.readBytes(length);
    }

    @Override
    public ByteBuffer slice() {
        return wrapped.slice();
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return wrapped.slice(index, length);
    }

    @Override
    public java.nio.ByteBuffer nioBuffer() {
        return wrapped.nioBuffer();
    }

    @Override
    public String toString(Charset charset) {
        return wrapped.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return wrapped.toString(index, length, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return wrapped.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return wrapped.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return wrapped.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return wrapped.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteBufferProcessor processor) {
        return wrapped.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufferProcessor processor) {
        return wrapped.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteBufferProcessor processor) {
        return wrapped.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufferProcessor processor) {
        return wrapped.forEachByteDesc(index, length, processor);
    }

    @Override
    public final int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public final int compareTo(ByteBuffer that) {
        return wrapped.compareTo(that);
    }

    @Override
    public final int refCnt() {
        return wrapped.refCnt();
    }

    @Override
    public ByteBuffer duplicate() {
        return wrapped.duplicate();
    }

    @Override
    public ByteBuffer readSlice(int length) {
        return wrapped.readSlice(length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return wrapped.readBytes(out, length);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return wrapped.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return wrapped.writeBytes(in, length);
    }

    @Override
    public ByteBuffer copy() {
        return wrapped.copy();
    }

    @Override
    public CompositeByteBuffer addComponent(ByteBuffer buffer) {
        wrapped.addComponent(buffer);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponents(ByteBuffer... buffers) {
        wrapped.addComponents(buffers);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponents(Iterable<ByteBuffer> buffers) {
        wrapped.addComponents(buffers);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponent(int cIndex, ByteBuffer buffer) {
        wrapped.addComponent(cIndex, buffer);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponents(int cIndex, ByteBuffer... buffers) {
        wrapped.addComponents(cIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponents(int cIndex, Iterable<ByteBuffer> buffers) {
        wrapped.addComponents(cIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponent(boolean increaseWriterIndex, ByteBuffer buffer) {
        wrapped.addComponent(increaseWriterIndex, buffer);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponents(boolean increaseWriterIndex, ByteBuffer... buffers) {
        wrapped.addComponents(increaseWriterIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponents(boolean increaseWriterIndex, Iterable<ByteBuffer> buffers) {
        wrapped.addComponents(increaseWriterIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuffer addComponent(boolean increaseWriterIndex, int cIndex, ByteBuffer buffer) {
        wrapped.addComponent(increaseWriterIndex, cIndex, buffer);
        return this;
    }

    @Override
    public CompositeByteBuffer removeComponent(int cIndex) {
        wrapped.removeComponent(cIndex);
        return this;
    }

    @Override
    public CompositeByteBuffer removeComponents(int cIndex, int numComponents) {
        wrapped.removeComponents(cIndex, numComponents);
        return this;
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return wrapped.iterator();
    }

    @Override
    public List<ByteBuffer> decompose(int offset, int length) {
        return wrapped.decompose(offset, length);
    }

    @Override
    public final boolean isDirect() {
        return wrapped.isDirect();
    }

    @Override
    public final boolean hasArray() {
        return wrapped.hasArray();
    }

    @Override
    public final byte[] array() {
        return wrapped.array();
    }

    @Override
    public final int arrayOffset() {
        return wrapped.arrayOffset();
    }

    @Override
    public final boolean hasMemoryAddress() {
        return wrapped.hasMemoryAddress();
    }

    @Override
    public final long memoryAddress() {
        return wrapped.memoryAddress();
    }

    @Override
    public final int capacity() {
        return wrapped.capacity();
    }

    @Override
    public CompositeByteBuffer capacity(int newCapacity) {
        wrapped.capacity(newCapacity);
        return this;
    }

    @Override
    public final ByteBufferAllocator alloc() {
        return wrapped.alloc();
    }

    @Override
    public final ByteOrder order() {
        return wrapped.order();
    }

    @Override
    public final int numComponents() {
        return wrapped.numComponents();
    }

    @Override
    public final int maxNumComponents() {
        return wrapped.maxNumComponents();
    }

    @Override
    public final int toComponentIndex(int offset) {
        return wrapped.toComponentIndex(offset);
    }

    @Override
    public final int toByteIndex(int cIndex) {
        return wrapped.toByteIndex(cIndex);
    }

    @Override
    public byte getByte(int index) {
        return wrapped.getByte(index);
    }

    @Override
    protected final byte _getByte(int index) {
        return wrapped._getByte(index);
    }

    @Override
    protected final short _getShort(int index) {
        return wrapped._getShort(index);
    }

    @Override
    protected final int _getUnsignedMedium(int index) {
        return wrapped._getUnsignedMedium(index);
    }

    @Override
    protected final int _getInt(int index) {
        return wrapped._getInt(index);
    }

    @Override
    protected final long _getLong(int index) {
        return wrapped._getLong(index);
    }

    @Override
    public CompositeByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        wrapped.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        wrapped.getBytes(index, dst);
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        wrapped.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return wrapped.getBytes(index, out, length);
    }

    @Override
    public CompositeByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        wrapped.getBytes(index, out, length);
        return this;
    }

    @Override
    public CompositeByteBuffer setByte(int index, int value) {
        wrapped.setByte(index, value);
        return this;
    }

    @Override
    protected final void _setByte(int index, int value) {
        wrapped._setByte(index, value);
    }

    @Override
    public CompositeByteBuffer setShort(int index, int value) {
        wrapped.setShort(index, value);
        return this;
    }

    @Override
    protected final void _setShort(int index, int value) {
        wrapped._setShort(index, value);
    }

    @Override
    public CompositeByteBuffer setMedium(int index, int value) {
        wrapped.setMedium(index, value);
        return this;
    }

    @Override
    protected final void _setMedium(int index, int value) {
        wrapped._setMedium(index, value);
    }

    @Override
    public CompositeByteBuffer setInt(int index, int value) {
        wrapped.setInt(index, value);
        return this;
    }

    @Override
    protected final void _setInt(int index, int value) {
        wrapped._setInt(index, value);
    }

    @Override
    public CompositeByteBuffer setLong(int index, long value) {
        wrapped.setLong(index, value);
        return this;
    }

    @Override
    protected final void _setLong(int index, long value) {
        wrapped._setLong(index, value);
    }

    @Override
    public CompositeByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        wrapped.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        wrapped.setBytes(index, src);
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        wrapped.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return wrapped.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return wrapped.setBytes(index, in, length);
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        return wrapped.copy(index, length);
    }

    @Override
    public final ByteBuffer component(int cIndex) {
        return wrapped.component(cIndex);
    }

    @Override
    public final ByteBuffer componentAtOffset(int offset) {
        return wrapped.componentAtOffset(offset);
    }

    @Override
    public final ByteBuffer internalComponent(int cIndex) {
        return wrapped.internalComponent(cIndex);
    }

    @Override
    public final ByteBuffer internalComponentAtOffset(int offset) {
        return wrapped.internalComponentAtOffset(offset);
    }

    @Override
    public int nioBufferCount() {
        return wrapped.nioBufferCount();
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        return wrapped.internalNioBuffer(index, length);
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        return wrapped.nioBuffer(index, length);
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return wrapped.nioBuffers(index, length);
    }

    @Override
    public CompositeByteBuffer consolidate() {
        wrapped.consolidate();
        return this;
    }

    @Override
    public CompositeByteBuffer consolidate(int cIndex, int numComponents) {
        wrapped.consolidate(cIndex, numComponents);
        return this;
    }

    @Override
    public CompositeByteBuffer discardReadComponents() {
        wrapped.discardReadComponents();
        return this;
    }

    @Override
    public CompositeByteBuffer discardReadBytes() {
        wrapped.discardReadBytes();
        return this;
    }

    @Override
    public final String toString() {
        return wrapped.toString();
    }

    @Override
    public final CompositeByteBuffer readerIndex(int readerIndex) {
        wrapped.readerIndex(readerIndex);
        return this;
    }

    @Override
    public final CompositeByteBuffer writerIndex(int writerIndex) {
        wrapped.writerIndex(writerIndex);
        return this;
    }

    @Override
    public final CompositeByteBuffer setIndex(int readerIndex, int writerIndex) {
        wrapped.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public final CompositeByteBuffer clear() {
        wrapped.clear();
        return this;
    }

    @Override
    public final CompositeByteBuffer markReaderIndex() {
        wrapped.markReaderIndex();
        return this;
    }

    @Override
    public final CompositeByteBuffer resetReaderIndex() {
        wrapped.resetReaderIndex();
        return this;
    }

    @Override
    public final CompositeByteBuffer markWriterIndex() {
        wrapped.markWriterIndex();
        return this;
    }

    @Override
    public final CompositeByteBuffer resetWriterIndex() {
        wrapped.resetWriterIndex();
        return this;
    }

    @Override
    public CompositeByteBuffer ensureWritable(int minWritableBytes) {
        wrapped.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, ByteBuffer dst) {
        wrapped.getBytes(index, dst);
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        wrapped.getBytes(index, dst, length);
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, byte[] dst) {
        wrapped.getBytes(index, dst);
        return this;
    }

    @Override
    public CompositeByteBuffer setBoolean(int index, boolean value) {
        wrapped.setBoolean(index, value);
        return this;
    }

    @Override
    public CompositeByteBuffer setChar(int index, int value) {
        wrapped.setChar(index, value);
        return this;
    }

    @Override
    public CompositeByteBuffer setFloat(int index, float value) {
        wrapped.setFloat(index, value);
        return this;
    }

    @Override
    public CompositeByteBuffer setDouble(int index, double value) {
        wrapped.setDouble(index, value);
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, ByteBuffer src) {
        wrapped.setBytes(index, src);
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, ByteBuffer src, int length) {
        wrapped.setBytes(index, src, length);
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, byte[] src) {
        wrapped.setBytes(index, src);
        return this;
    }

    @Override
    public CompositeByteBuffer setZero(int index, int length) {
        wrapped.setZero(index, length);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(ByteBuffer dst) {
        wrapped.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(ByteBuffer dst, int length) {
        wrapped.readBytes(dst, length);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        wrapped.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(byte[] dst) {
        wrapped.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        wrapped.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(java.nio.ByteBuffer dst) {
        wrapped.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuffer readBytes(OutputStream out, int length) throws IOException {
        wrapped.readBytes(out, length);
        return this;
    }

    @Override
    public CompositeByteBuffer skipBytes(int length) {
        wrapped.skipBytes(length);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBoolean(boolean value) {
        wrapped.writeBoolean(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeByte(int value) {
        wrapped.writeByte(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeShort(int value) {
        wrapped.writeShort(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeMedium(int value) {
        wrapped.writeMedium(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeInt(int value) {
        wrapped.writeInt(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeLong(long value) {
        wrapped.writeLong(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeChar(int value) {
        wrapped.writeChar(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeFloat(float value) {
        wrapped.writeFloat(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeDouble(double value) {
        wrapped.writeDouble(value);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBytes(ByteBuffer src) {
        wrapped.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBytes(ByteBuffer src, int length) {
        wrapped.writeBytes(src, length);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length) {
        wrapped.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBytes(byte[] src) {
        wrapped.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBytes(byte[] src, int srcIndex, int length) {
        wrapped.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuffer writeBytes(java.nio.ByteBuffer src) {
        wrapped.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuffer writeZero(int length) {
        wrapped.writeZero(length);
        return this;
    }

    @Override
    public CompositeByteBuffer retain(int increment) {
        wrapped.retain(increment);
        return this;
    }

    @Override
    public CompositeByteBuffer retain() {
        wrapped.retain();
        return this;
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers() {
        return wrapped.nioBuffers();
    }

    @Override
    public CompositeByteBuffer discardSomeReadBytes() {
        wrapped.discardSomeReadBytes();
        return this;
    }

    @Override
    public final void deallocate() {
        wrapped.deallocate();
    }

    @Override
    public final ByteBuffer unwrap() {
        return wrapped;
    }
}
