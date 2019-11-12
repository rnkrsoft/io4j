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

import com.rnkrsoft.io.buffer.util.ResourceLeakDetector;
import com.rnkrsoft.io.buffer.util.ResourceLeakTracker;
import com.rnkrsoft.io.buffer.util.internal.SystemPropertyUtil;
import com.rnkrsoft.io.buffer.util.internal.logging.InternalLogger;
import com.rnkrsoft.io.buffer.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class AdvancedLeakAwareByteBuffer extends SimpleLeakAwareByteBuffer {

    private static final String PROP_ACQUIRE_AND_RELEASE_ONLY = "io.netty.leakDetection.acquireAndReleaseOnly";
    private static final boolean ACQUIRE_AND_RELEASE_ONLY;

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AdvancedLeakAwareByteBuffer.class);

    static {
        ACQUIRE_AND_RELEASE_ONLY = SystemPropertyUtil.getBoolean(PROP_ACQUIRE_AND_RELEASE_ONLY, false);

        if (logger.isDebugEnabled()) {
            logger.debug("-D{}: {}", PROP_ACQUIRE_AND_RELEASE_ONLY, ACQUIRE_AND_RELEASE_ONLY);
        }

        ResourceLeakDetector.addExclusions(
                AdvancedLeakAwareByteBuffer.class, "recordLeakNonRefCountingOperation");
    }

    AdvancedLeakAwareByteBuffer(ByteBuffer buf, ResourceLeakTracker<ByteBuffer> leak) {
        super(buf, leak);
    }

    AdvancedLeakAwareByteBuffer(ByteBuffer wrapped, ByteBuffer trackedByteBuf, ResourceLeakTracker<ByteBuffer> leak) {
        super(wrapped, trackedByteBuf, leak);
    }

    static void recordLeakNonRefCountingOperation(ResourceLeakTracker<ByteBuffer> leak) {
        if (!ACQUIRE_AND_RELEASE_ONLY) {
            leak.record();
        }
    }

    @Override
    public ByteBuffer order(ByteOrder endianness) {
        recordLeakNonRefCountingOperation(leak);
        return super.order(endianness);
    }

    @Override
    public ByteBuffer slice() {
        recordLeakNonRefCountingOperation(leak);
        return super.slice();
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.slice(index, length);
    }

    @Override
    public ByteBuffer duplicate() {
        recordLeakNonRefCountingOperation(leak);
        return super.duplicate();
    }

    @Override
    public ByteBuffer readSlice(int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.readSlice(length);
    }

    @Override
    public ByteBuffer discardReadBytes() {
        recordLeakNonRefCountingOperation(leak);
        return super.discardReadBytes();
    }

    @Override
    public ByteBuffer discardSomeReadBytes() {
        recordLeakNonRefCountingOperation(leak);
        return super.discardSomeReadBytes();
    }

    @Override
    public ByteBuffer ensureWritable(int minWritableBytes) {
        recordLeakNonRefCountingOperation(leak);
        return super.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        recordLeakNonRefCountingOperation(leak);
        return super.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getUnsignedShort(index);
    }

    @Override
    public int getMedium(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getMedium(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getLong(index);
    }

    @Override
    public char getChar(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        recordLeakNonRefCountingOperation(leak);
        return super.getDouble(index);
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, dst, length);
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.getBytes(index, out, length);
    }

    @Override
    public ByteBuffer setBoolean(int index, boolean value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBoolean(index, value);
    }

    @Override
    public ByteBuffer setByte(int index, int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setByte(index, value);
    }

    @Override
    public ByteBuffer setShort(int index, int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setShort(index, value);
    }

    @Override
    public ByteBuffer setMedium(int index, int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setMedium(index, value);
    }

    @Override
    public ByteBuffer setInt(int index, int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setInt(index, value);
    }

    @Override
    public ByteBuffer setLong(int index, long value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setLong(index, value);
    }

    @Override
    public ByteBuffer setChar(int index, int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setChar(index, value);
    }

    @Override
    public ByteBuffer setFloat(int index, float value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setFloat(index, value);
    }

    @Override
    public ByteBuffer setDouble(int index, double value) {
        recordLeakNonRefCountingOperation(leak);
        return super.setDouble(index, value);
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, src);
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, src, length);
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, src);
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.setBytes(index, in, length);
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.setZero(index, length);
    }

    @Override
    public boolean readBoolean() {
        recordLeakNonRefCountingOperation(leak);
        return super.readBoolean();
    }

    @Override
    public byte readByte() {
        recordLeakNonRefCountingOperation(leak);
        return super.readByte();
    }

    @Override
    public short readUnsignedByte() {
        recordLeakNonRefCountingOperation(leak);
        return super.readUnsignedByte();
    }

    @Override
    public short readShort() {
        recordLeakNonRefCountingOperation(leak);
        return super.readShort();
    }

    @Override
    public int readUnsignedShort() {
        recordLeakNonRefCountingOperation(leak);
        return super.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        recordLeakNonRefCountingOperation(leak);
        return super.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        recordLeakNonRefCountingOperation(leak);
        return super.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        recordLeakNonRefCountingOperation(leak);
        return super.readInt();
    }

    @Override
    public long readUnsignedInt() {
        recordLeakNonRefCountingOperation(leak);
        return super.readUnsignedInt();
    }

    @Override
    public long readLong() {
        recordLeakNonRefCountingOperation(leak);
        return super.readLong();
    }

    @Override
    public char readChar() {
        recordLeakNonRefCountingOperation(leak);
        return super.readChar();
    }

    @Override
    public float readFloat() {
        recordLeakNonRefCountingOperation(leak);
        return super.readFloat();
    }

    @Override
    public double readDouble() {
        recordLeakNonRefCountingOperation(leak);
        return super.readDouble();
    }

    @Override
    public ByteBuffer readBytes(int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(length);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(dst);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(dst, length);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuffer readBytes(byte[] dst) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(dst);
    }

    @Override
    public ByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuffer readBytes(java.nio.ByteBuffer dst) {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(dst);
    }

    @Override
    public ByteBuffer readBytes(OutputStream out, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.readBytes(out, length);
    }

    @Override
    public ByteBuffer skipBytes(int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.skipBytes(length);
    }

    @Override
    public ByteBuffer writeBoolean(boolean value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBoolean(value);
    }

    @Override
    public ByteBuffer writeByte(int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeByte(value);
    }

    @Override
    public ByteBuffer writeShort(int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeShort(value);
    }

    @Override
    public ByteBuffer writeMedium(int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeMedium(value);
    }

    @Override
    public ByteBuffer writeInt(int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeInt(value);
    }

    @Override
    public ByteBuffer writeLong(long value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeLong(value);
    }

    @Override
    public ByteBuffer writeChar(int value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeChar(value);
    }

    @Override
    public ByteBuffer writeFloat(float value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeFloat(value);
    }

    @Override
    public ByteBuffer writeDouble(double value) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeDouble(value);
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(src);
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(src, length);
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuffer writeBytes(byte[] src) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(src);
    }

    @Override
    public ByteBuffer writeBytes(byte[] src, int srcIndex, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuffer writeBytes(java.nio.ByteBuffer src) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        recordLeakNonRefCountingOperation(leak);
        return super.writeBytes(in, length);
    }

    @Override
    public ByteBuffer writeZero(int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.writeZero(length);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        recordLeakNonRefCountingOperation(leak);
        return super.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        recordLeakNonRefCountingOperation(leak);
        return super.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        recordLeakNonRefCountingOperation(leak);
        return super.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        recordLeakNonRefCountingOperation(leak);
        return super.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteBufferProcessor processor) {
        recordLeakNonRefCountingOperation(leak);
        return super.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufferProcessor processor) {
        recordLeakNonRefCountingOperation(leak);
        return super.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteBufferProcessor processor) {
        recordLeakNonRefCountingOperation(leak);
        return super.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufferProcessor processor) {
        recordLeakNonRefCountingOperation(leak);
        return super.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuffer copy() {
        recordLeakNonRefCountingOperation(leak);
        return super.copy();
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.copy(index, length);
    }

    @Override
    public int nioBufferCount() {
        recordLeakNonRefCountingOperation(leak);
        return super.nioBufferCount();
    }

    @Override
    public java.nio.ByteBuffer nioBuffer() {
        recordLeakNonRefCountingOperation(leak);
        return super.nioBuffer();
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.nioBuffer(index, length);
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers() {
        recordLeakNonRefCountingOperation(leak);
        return super.nioBuffers();
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.nioBuffers(index, length);
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        recordLeakNonRefCountingOperation(leak);
        return super.internalNioBuffer(index, length);
    }

    @Override
    public String toString(Charset charset) {
        recordLeakNonRefCountingOperation(leak);
        return super.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        recordLeakNonRefCountingOperation(leak);
        return super.toString(index, length, charset);
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        recordLeakNonRefCountingOperation(leak);
        return super.capacity(newCapacity);
    }

    @Override
    public ByteBuffer retain() {
        leak.record();
        return super.retain();
    }

    @Override
    public ByteBuffer retain(int increment) {
        leak.record();
        return super.retain(increment);
    }

    @Override
    public boolean release() {
        leak.record();
        return super.release();
    }

    @Override
    public boolean release(int decrement) {
        leak.record();
        return super.release(decrement);
    }

    @Override
    protected AdvancedLeakAwareByteBuffer newLeakAwareByteBuf(
            ByteBuffer buf, ByteBuffer trackedByteBuf, ResourceLeakTracker<ByteBuffer> leakTracker) {
        return new AdvancedLeakAwareByteBuffer(buf, trackedByteBuf, leakTracker);
    }
}
