package com.rnkrsoft.io.buffer;


import com.rnkrsoft.io.buffer.process.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
abstract class AbstractByteBuffer implements ByteBuffer {

    protected int readerIndex;
    protected int writerIndex;
    protected int markedReaderIndex;
    protected int markedWriterIndex;
    protected int maxCapacity;

    protected AbstractByteBuffer(int maxCapacity) {
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("maxCapacity: " + maxCapacity + " (expected: >= 0)");
        }
        this.maxCapacity = maxCapacity;
    }
    @Override
    public int writeString(String string, Charset charset) {
        int written = setCharSequence(writerIndex, string, charset);
        writerIndex += written;
        return written;
    }
    @Override
    public int writeStringUTF8(String string) {
        return writeString(string, Charset.forName("UTF-8"));
    }

    @Override
    public String readString(int length, Charset charset) {
        String string = getString(readerIndex, length, charset);
        readerIndex += length;
        return string;
    }

    @Override
    public String readStringUTF8(int length) {
        return readString(length, Charset.forName("UTF-8"));
    }


    @Override
    public int writelnString(String string, Charset charset) {
        return writeString(string + "\n", charset);
    }

    @Override
    public int writelnStringUTF8(String string) {
        return writeStringUTF8(string + "\n");
    }

    @Override
    public int load(String fileName) throws IOException {
        return 0;
    }

    @Override
    public int load(InputStream is) throws IOException {
        return 0;
    }

    @Override
    public int store(OutputStream os) throws IOException {
        return 0;
    }

    @Override
    public int store(String fileName) throws IOException {
        return 0;
    }

    @Override
    public InputStream asInputStream() {
        return null;
    }

    public boolean getBoolean(int index) {
        return false;
    }

    public byte getByte(int index) {
        return 0;
    }

    public short getUnsignedByte(int index) {
        return 0;
    }

    public short getShort(int index) {
        return 0;
    }

    public short getShortLE(int index) {
        return 0;
    }

    public int getUnsignedShort(int index) {
        return 0;
    }

    public int getUnsignedShortLE(int index) {
        return 0;
    }

    public int getMedium(int index) {
        return 0;
    }

    public int getMediumLE(int index) {
        return 0;
    }

    public int getUnsignedMedium(int index) {
        return 0;
    }

    public int getUnsignedMediumLE(int index) {
        return 0;
    }

    public int getInt(int index) {
        return 0;
    }

    public int getIntLE(int index) {
        return 0;
    }

    public long getUnsignedInt(int index) {
        return 0;
    }

    public long getUnsignedIntLE(int index) {
        return 0;
    }

    public long getLong(int index) {
        return 0;
    }

    public long getLongLE(int index) {
        return 0;
    }

    public char getChar(int index) {
        return 0;
    }

    public float getFloat(int index) {
        return 0;
    }

    public double getDouble(int index) {
        return 0;
    }

    public ByteBuffer getBytes(int index, ByteBuffer dst) {
        return null;
    }

    public ByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        return null;
    }

    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        return null;
    }

    public ByteBuffer getBytes(int index, byte[] dst) {
        return null;
    }

    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        return null;
    }

    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        return null;
    }

    public ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        return null;
    }

    public ByteBuffer getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return null;
    }

    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return 0;
    }

    public String getString(int index, int length, Charset charset) {
        return null;
    }

    public boolean readBoolean() {
        return false;
    }

    public byte readByte() {
        return 0;
    }

    public short readUnsignedByte() {
        return 0;
    }

    public short readShort() {
        return 0;
    }

    public short readShortLE() {
        return 0;
    }

    public int readUnsignedShort() {
        return 0;
    }

    public int readUnsignedShortLE() {
        return 0;
    }

    public int readMedium() {
        return 0;
    }

    public int readMediumLE() {
        return 0;
    }

    public int readUnsignedMedium() {
        return 0;
    }

    public int readUnsignedMediumLE() {
        return 0;
    }

    public int readInt() {
        return 0;
    }

    public int readIntLE() {
        return 0;
    }

    public long readUnsignedInt() {
        return 0;
    }

    public long readUnsignedIntLE() {
        return 0;
    }

    public long readLong() {
        return 0;
    }

    public long readLongLE() {
        return 0;
    }

    public char readChar() {
        return 0;
    }

    public float readFloat() {
        return 0;
    }

    public double readDouble() {
        return 0;
    }

    public ByteBuffer readBytes(int length) {
        return null;
    }

    public ByteBuffer readSlice(int length) {
        return null;
    }

    public ByteBuffer readRetainedSlice(int length) {
        return null;
    }

    public ByteBuffer readBytes(ByteBuffer dst) {
        return null;
    }

    public ByteBuffer readBytes(ByteBuffer dst, int length) {
        return null;
    }

    public ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        return null;
    }

    public ByteBuffer readBytes(byte[] dst) {
        return null;
    }

    public ByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        return null;
    }

    public ByteBuffer readBytes(java.nio.ByteBuffer dst) {
        return null;
    }

    public ByteBuffer readBytes(OutputStream out, int length) throws IOException {
        return null;
    }

    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return 0;
    }

    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return 0;
    }

    public ByteBuffer skipBytes(int length) {
        return null;
    }

    public ByteBuffer setBoolean(int index, boolean value) {
        return null;
    }

    public ByteBuffer setByte(int index, int value) {
        return null;
    }

    public ByteBuffer setShort(int index, int value) {
        return null;
    }

    public ByteBuffer setShortLE(int index, int value) {
        return null;
    }

    public ByteBuffer setMedium(int index, int value) {
        return null;
    }

    public ByteBuffer setMediumLE(int index, int value) {
        return null;
    }

    public ByteBuffer setIntLE(int index, int value) {
        return null;
    }

    public ByteBuffer setInt(int index, int value) {
        return null;
    }

    public ByteBuffer setLong(int index, long value) {
        return null;
    }

    public ByteBuffer setChar(int index, int value) {
        return null;
    }

    public ByteBuffer setLongLE(int index, long value) {
        return null;
    }

    public ByteBuffer setFloat(int index, float value) {
        return null;
    }

    public ByteBuffer setDouble(int index, double value) {
        return null;
    }

    public ByteBuffer setBytes(int index, ByteBuffer src) {
        return null;
    }

    public ByteBuffer setBytes(int index, ByteBuffer src, int length) {
        return null;
    }

    public ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        return null;
    }

    public ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        return null;
    }

    public ByteBuffer setBytes(int index, byte[] src) {
        return null;
    }

    public int setBytes(int index, InputStream in, int length) throws IOException {
        return 0;
    }

    public ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        return null;
    }

    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return 0;
    }

    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return 0;
    }

    public ByteBuffer setZero(int index, int length) {
        return null;
    }

    public ByteBuffer writeBoolean(boolean value) {
        return null;
    }

    public ByteBuffer writeByte(int value) {
        return null;
    }

    public ByteBuffer writeShortLE(int value) {
        return null;
    }

    public ByteBuffer writeShort(int value) {
        return null;
    }

    public ByteBuffer writeMedium(int value) {
        return null;
    }

    public ByteBuffer writeMediumLE(int value) {
        return null;
    }

    public ByteBuffer writeInt(int value) {
        return null;
    }

    public ByteBuffer writeIntLE(int value) {
        return null;
    }

    public ByteBuffer writeLongLE(long value) {
        return null;
    }

    public ByteBuffer writeLong(long value) {
        return null;
    }

    public ByteBuffer writeChar(int value) {
        return null;
    }

    public ByteBuffer writeFloat(float value) {
        return null;
    }

    public ByteBuffer writeDouble(double value) {
        return null;
    }

    public ByteBuffer writeBytes(ByteBuffer src) {
        return null;
    }

    public ByteBuffer writeBytes(ByteBuffer src, int length) {
        return null;
    }

    public ByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length) {
        return null;
    }

    public ByteBuffer writeBytes(byte[] src) {
        return null;
    }

    public ByteBuffer writeBytes(byte[] src, int srcIndex, int length) {
        return null;
    }

    public ByteBuffer writeBytes(java.nio.ByteBuffer src) {
        return null;
    }

    public ByteBuffer writeBytes(InputStream in, int length) throws IOException {
        return null;
    }

    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return 0;
    }

    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return 0;
    }

    public ByteBuffer writeZero(int length) {
        return null;
    }


    public int compareTo(ByteBuffer o) {
        return 0;
    }

    public ByteBuffer readerIndex(int readerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", readerIndex, writerIndex));
        }
        this.readerIndex = readerIndex;
        return this;
    }

    public ByteBuffer writerIndex(int writerIndex) {
        if (writerIndex < readerIndex || writerIndex > capacity()) {
            throw new IndexOutOfBoundsException(String.format(
                    "writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))",
                    writerIndex, readerIndex, capacity()));
        }
        this.writerIndex = writerIndex;
        return this;
    }

    public ByteBuffer setIndex(int readerIndex, int writerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity()) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))",
                    readerIndex, writerIndex, capacity()));
        }
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
        return this;
    }

    public ByteBuffer clear() {
        readerIndex = writerIndex = 0;
        return this;
    }
    public String toString(int index, int length, Charset charset) {
        return null;
    }
    protected final void discardMarks() {
        markedReaderIndex = markedWriterIndex = 0;
    }

    public int maxCapacity() {
        return this.maxCapacity;
    }

    public boolean isReadOnly() {
        return false;
    }

    public ByteBuffer readOnly(boolean readOnly) {
        return this;
    }

    public boolean isReadable() {
        return writerIndex > readerIndex;
    }

    public boolean isWritable() {
        return capacity() > writerIndex;
    }

    public int readerIndex() {
        return this.readerIndex;
    }

    public int writerIndex() {
        return this.writerIndex;
    }

    public int readableBytes() {
        return this.writerIndex - this.readerIndex;
    }

    public int writableBytes() {
        return capacity() - this.writerIndex;
    }

    public int maxWritableBytes() {
        return this.maxCapacity - this.writerIndex;
    }

    public ByteBuffer markReaderIndex() {
        this.markedReaderIndex = this.readerIndex;
        return this;
    }

    public ByteBuffer resetReaderIndex() {
        readerIndex(this.markedReaderIndex);
        return this;
    }

    public ByteBuffer markWriterIndex() {
        this.markedWriterIndex = this.writerIndex;
        return this;
    }

    public ByteBuffer resetWriterIndex() {
        this.writerIndex = this.markedWriterIndex;
        return this;
    }

    public ByteBuffer discardReadBytes() {
        return null;
    }

    public ByteBuffer discardSomeReadBytes() {
        return null;
    }

    public ByteBuffer ensureWritable(int minWritableBytes) {
        return null;
    }

    public int ensureWritable(int minWritableBytes, boolean force) {
        return 0;
    }

    public int indexOf(int fromIndex, int toIndex, byte value) {
        return ByteBufferFinder.indexOf(this, fromIndex, toIndex, value);
    }

    public int bytesBefore(byte value) {
        return 0;
    }

    public int bytesBefore(int length, byte value) {
        return 0;
    }

    public int bytesBefore(int index, int length, byte value) {
        return 0;
    }

    public int forEachByte(ByteProcessor processor) {
        return 0;
    }

    public int forEachByte(int index, int length, ByteProcessor processor) {
        return 0;
    }

    public int forEachByteDesc(ByteProcessor processor) {
        return 0;
    }

    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return 0;
    }

    public ByteBuffer copy() {
        return copy(readerIndex, readableBytes());
    }

    public ByteBuffer slice() {
        return slice(readerIndex, readableBytes());
    }

    public ByteBuffer retainedSlice() {
        return null;
    }

    public ByteBuffer retainedSlice(int index, int length) {
        return null;
    }

    public ByteBuffer duplicate() {
        return null;
    }

    public ByteBuffer retainedDuplicate() {
        return null;
    }

    public int nioBufferCount() {
        return 0;
    }

    public java.nio.ByteBuffer nioBuffer() {
        return null;
    }

    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        return null;
    }

    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        return null;
    }

    public java.nio.ByteBuffer[] nioBuffers() {
        return new java.nio.ByteBuffer[0];
    }

    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[0];
    }

    public String toString(Charset charset) {
        return toString(readerIndex, readableBytes(), charset);
    }

    protected void checkIndex0(int index, int fieldLength) {
        if (isOutOfBounds(index, fieldLength, capacity())) {
            throw new IndexOutOfBoundsException(String.format("index: %d, length: %d (expected: range(0, %d))", index, fieldLength, capacity()));
        }
    }

    protected final void checkIndex(int index) {
        checkIndex(index, 1);
    }

    protected final void checkIndex(int index, int fieldLength) {
        checkIndex0(index, fieldLength);
    }

    protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
        checkIndex(index, length);
        if (isOutOfBounds(srcIndex, length, srcCapacity)) {
            throw new IndexOutOfBoundsException(String.format(
                    "srcIndex: %d, length: %d (expected: range(0, %d))", srcIndex, length, srcCapacity));
        }
    }

    protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
        checkIndex(index, length);
        if (isOutOfBounds(dstIndex, length, dstCapacity)) {
            throw new IndexOutOfBoundsException(String.format(
                    "dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dstCapacity));
        }
    }


    protected final void checkReadableBytes(int minimumReadableBytes) {
        if (minimumReadableBytes < 0) {
            throw new IllegalArgumentException("minimumReadableBytes: " + minimumReadableBytes + " (expected: >= 0)");
        }
        checkReadableBytes0(minimumReadableBytes);
    }

    protected void checkReadableBytes0(int minimumReadableBytes) {
        if (readerIndex > writerIndex - minimumReadableBytes) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s",
                    readerIndex, minimumReadableBytes, writerIndex, this));
        }
    }



    protected static boolean isOutOfBounds(int index, int length, int capacity) {
        return (index | length | (index + length) | (capacity - (index + length))) < 0;
    }

}
