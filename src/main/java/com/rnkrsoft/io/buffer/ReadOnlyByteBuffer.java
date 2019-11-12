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
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

/**
 * A derived buffer which forbids any write requests to its parent.  It is
 * recommended to use {@link Unpooled#unmodifiableBuffer(ByteBuffer)}
 * instead of calling the constructor explicitly.
 *
 * @deprecated Do not use.
 */
@Deprecated
public class ReadOnlyByteBuffer extends AbstractDerivedByteBuffer {

    private final ByteBuffer buffer;

    public ReadOnlyByteBuffer(ByteBuffer buffer) {
        super(buffer.maxCapacity());

        if (buffer instanceof ReadOnlyByteBuffer || buffer instanceof DuplicatedByteBuffer) {
            this.buffer = buffer.unwrap();
        } else {
            this.buffer = buffer;
        }
        setIndex(buffer.readerIndex(), buffer.writerIndex());
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isWritable(int numBytes) {
        return false;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return 1;
    }

    @Override
    public ByteBuffer ensureWritable(int minWritableBytes) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer unwrap() {
        return buffer;
    }

    @Override
    public ByteBufferAllocator alloc() {
        return unwrap().alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return unwrap().order();
    }

    @Override
    public boolean isDirect() {
        return unwrap().isDirect();
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int arrayOffset() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public boolean hasMemoryAddress() {
        return unwrap().hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return unwrap().memoryAddress();
    }

    @Override
    public ByteBuffer discardReadBytes() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, InputStream in, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length)
            throws IOException {
        return unwrap().getBytes(index, out, length);
    }

    @Override
    public ByteBuffer getBytes(int index, OutputStream out, int length)
            throws IOException {
        unwrap().getBytes(index, out, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        unwrap().getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        unwrap().getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        unwrap().getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuffer duplicate() {
        return new ReadOnlyByteBuffer(this);
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        return unwrap().copy(index, length);
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return Unpooled.unmodifiableBuffer(unwrap().slice(index, length));
    }

    @Override
    public byte getByte(int index) {
        return unwrap().getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return unwrap().getByte(index);
    }

    @Override
    public short getShort(int index) {
        return unwrap().getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return unwrap().getShort(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return unwrap().getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return unwrap().getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        return unwrap().getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return unwrap().getInt(index);
    }

    @Override
    public long getLong(int index) {
        return unwrap().getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return unwrap().getLong(index);
    }

    @Override
    public int nioBufferCount() {
        return unwrap().nioBufferCount();
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        return unwrap().nioBuffer(index, length).asReadOnlyBuffer();
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return unwrap().nioBuffers(index, length);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufferProcessor processor) {
        return unwrap().forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufferProcessor processor) {
        return unwrap().forEachByteDesc(index, length, processor);
    }

    @Override
    public int capacity() {
        return unwrap().capacity();
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }
}
