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
    protected final int maxCapacity;

    protected AbstractByteBuffer(int maxCapacity) {
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("maxCapacity: " + maxCapacity + " (expected: >= 0)");
        }
        this.maxCapacity = maxCapacity;
    }
    @Override
    public int writeString(String string, Charset charset) {
        int length = (int) (string.length() * charset.newEncoder().maxBytesPerChar());
        ensureWritable0(length);
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
    public java.nio.ByteBuffer nioBuffer() {
        return nioBuffer(readerIndex, readableBytesLength());
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers() {
        return nioBuffers(readerIndex, readableBytesLength());
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
        return getByte(index) == 1;
    }

    public byte getByte(int index) {
        checkIndex(index);
        return getByte0(index);
    }


    public short getUnsignedByte(int index) {
        return 0;
    }

    public short getShort(int index) {
        checkIndex(index);
        return getShort0(index);
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
        int value = getUnsignedMedium(index);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    public int getMediumLE(int index) {
        return 0;
    }

    public int getUnsignedMediumLE(int index) {
        return 0;
    }

    public int getInt(int index) {
        checkIndex(index);
        return getInt0(index);
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
        checkIndex(index);
        return getLong0(index);
    }

    public long getLongLE(int index) {
        return 0;
    }

    public char getChar(int index) {
        return (char) getShort(index);
    }

    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    public ByteBuffer getBytes(int index, ByteBuffer dst) {
        getBytes(index, dst, dst.writableBytesLength());
        return this;
    }

    public ByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        getBytes(index, dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
        return this;
    }

    public ByteBuffer getBytes(int index, byte[] dst) {
        getBytes(index, dst, 0, dst.length);
        return this;
    }



    public int getBytes(int index, OutputStream out, int length) throws IOException {
        out.write(array(), index, length);
        return length;
    }


    public String getString(int index, int length, Charset charset) {
        if (charset == Charset.forName("US-ASCII") || charset == Charset.forName("ISO_8859_1")){
            return null;
        }else{
            return toString(index, length, charset);
        }
    }

    public boolean readBoolean() {
        return readByte() == 1;
    }

    public byte readByte() {
        checkReadableBytes0(1);
        int i = readerIndex;
        byte v = getByte0(i);
        readerIndex = i + 1;
        return v;
    }

    public short readUnsignedByte() {
        return 0;
    }

    public short readShort() {
        checkReadableBytes0(2);
        short v = getShort0(readerIndex);
        readerIndex += 2;
        return v;
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
        checkReadableBytes0(3);
        int v = getMedium(readerIndex);
        readerIndex += 3;
        return v;
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
        checkReadableBytes0(4);
        int v = getInt0(readerIndex);
        readerIndex += 4;
        return v;
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
        checkReadableBytes0(8);
        long v = getLong0(readerIndex);
        readerIndex += 8;
        return v;
    }

    public long readLongLE() {
        return 0;
    }

    public char readChar() {
        return (char) readShort();
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
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

    public ByteBuffer setByte(int index, byte value) {
        checkIndex(index);
        setByte0(index, value);
        return this;
    }



    public ByteBuffer setShort(int index, short value) {
        checkIndex(index);
        setShort0(index, value);
        return this;
    }



    public ByteBuffer setShortLE(int index, int value) {
        return null;
    }

    public ByteBuffer setMedium(int index, int value) {
        checkIndex(index);
        setMedium0(index, value);
        return this;
    }


    public ByteBuffer setMediumLE(int index, int value) {
        return null;
    }

    public ByteBuffer setIntLE(int index, int value) {
        return null;
    }

    public ByteBuffer setInt(int index, int value) {
        checkIndex(index);
        setInt0(index, value);
        return this;
    }



    public ByteBuffer setChar(int index, char value) {
        checkIndex(index);
        setInt0(index, value);
        return this;
    }

    public ByteBuffer setLong(int index, long value) {
        checkIndex(index);
        setLong0(index, value);
        return this;
    }


    public ByteBuffer setLongLE(int index, long value) {
        return null;
    }


    public ByteBuffer setFloat(int index, float value) {
        return setInt(index, Float.floatToRawIntBits(value));
    }


    public ByteBuffer setDouble(int index, double value) {
        return setLong(index, Double.doubleToRawLongBits(value));
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

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
       return ByteBufferUtilities.writeUtf8(this, index, sequence, sequence.length());
    }

    @Override
    public ByteBuffer setZero(int index, int length) {
        if (length == 0) {
            return this;
        }

        checkIndex(index, length);

        int nLong = length >>> 3;
        int nBytes = length & 7;
        for (int i = nLong; i > 0; i --) {
            setLong0(index, 0);
            index += 8;
        }
        if (nBytes == 4) {
            setInt0(index, 0);
            // Not need to update the index as we not will use it after this.
        } else if (nBytes < 4) {
            for (int i = nBytes; i > 0; i --) {
                setByte0(index, (byte) 0);
                index ++;
            }
        } else {
            setInt0(index, 0);
            index += 4;
            for (int i = nBytes - 4; i > 0; i --) {
                setByte0(index, (byte) 0);
                index ++;
            }
        }
        return this;
    }

    public ByteBuffer writeBoolean(boolean value) {
        writeByte(value ? (byte)1 : (byte)0);
        return this;
    }

    public ByteBuffer writeByte(byte value) {
        ensureWritable0(1);
        setByte0(writerIndex, value);
        writerIndex++;
        return this;
    }

    public ByteBuffer writeShortLE(int value) {
        return null;
    }

    public ByteBuffer writeShort(short value) {
        ensureWritable0(2);
        setShort0(writerIndex, value);
        writerIndex += 2;
        return this;
    }

    public ByteBuffer writeMedium(int value) {
        ensureWritable0(3);
        setMedium0(writerIndex, value);
        writerIndex += 3;
        return this;
    }

    public ByteBuffer writeMediumLE(int value) {
        return null;
    }

    public ByteBuffer writeInt(int value) {
        ensureWritable0(4);
        setInt0(writerIndex, value);
        writerIndex += 4;
        return this;
    }

    public ByteBuffer writeIntLE(int value) {
        return null;
    }

    public ByteBuffer writeLongLE(long value) {
        return null;
    }

    public ByteBuffer writeLong(long value) {
        ensureWritable0(8);
        setLong0(writerIndex, value);
        writerIndex += 8;
        return this;
    }

    public ByteBuffer writeChar(char value) {
        writeShort((short) value);
        return this;
    }

    public ByteBuffer writeFloat(float value) {
        ensureWritable0(4);
        setFloat(writerIndex, value);
        writerIndex += 4;
        return this;
    }

    public ByteBuffer writeDouble(double value) {
        ensureWritable0(8);
        setDouble(writerIndex, value);
        writerIndex += 8;
        return this;
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
        if (length == 0){
            //fixme
            return "";
        }
        //TODO 使用一个缓存池来进行操作
        final byte[] array;
        final int offset;
        if (hasArray()){//载体是数组的直接使用数组引用构建字符串
            array = this.array();
            offset = this.arrayOffset() + index;
        }else{//载体不是数组，则为JDK ByteBuffer，需要使用缓存来保存1024字节以下的字节数组缓存，避免在堆中反复回收
            array = ByteBuffers.getBytes(length);
            offset = 0;
            //使用获取方法获取字节数组内容
            getBytes(index, array, 0, length);
        }
        if(charset == Charset.forName("US-ASCII")){
            return new String(array, 0, offset, length);
        }
        return new String(array, offset, length, charset);
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

    public int readableBytesLength() {
        return this.writerIndex - this.readerIndex;
    }

    public int writableBytesLength() {
        return capacity() - this.writerIndex;
    }

    public int maxWritableBytesLength() {
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


    public int ensureWritable(int minWritableBytes, boolean force) {
        return 0;
    }

    public ByteBuffer ensureWritable(int minWritableBytes) {
        if (minWritableBytes <= 0){
            throw new IllegalArgumentException("新容量为0或者负数");
        }
        ensureWritable0(minWritableBytes);
        return this;
    }

    final void ensureWritable0(int minWritableBytes) {
        if (minWritableBytes <= writableBytesLength()) {
            return;
        }
        //按照一定的分段函数进行计算新的容量
        int newCapacity = ByteBufferUtilities.calcNewCapacity(capacity(), writerIndex, minWritableBytes, maxCapacity);
        //调整容量
        capacity(newCapacity);
    }
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return ByteBufferFinder.indexOf(this, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte[] value) {
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
        return forEachByte(0, capacity(), processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        int start = index;
        int end = index + length;
        for (; start < end; ++start) {
            if (!processor.process(getByte0(start))) {
                return start;
            }
        }

        return -1;
    }

    @Override
    public int forEachByte(final int index, int length, ByteProcessor[] processors) {
        int processorSize = processors.length;
        int firstMatchedIndex = -1;
        int currentProcessIndex = 0;
        for (int i = index; i < length; i++) {
            if (processors[currentProcessIndex].process(getByte0(i))){
                if (currentProcessIndex == 0){
                    firstMatchedIndex = i;
                }
                currentProcessIndex++;
                if (currentProcessIndex == processorSize){
                    return firstMatchedIndex;
                }
            }
        }
        return -1;
    }


    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return 0;
    }


    public int forEachByteDesc(ByteProcessor processor) {
        return forEachByteDesc(0, capacity(), processor);
    }

    public ByteBuffer copy() {
        return copy(readerIndex, readableBytesLength());
    }

    public ByteBuffer slice() {
        return slice(readerIndex, readableBytesLength());
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

    public String toString(Charset charset) {
        return toString(readerIndex, readableBytesLength(), charset);
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





    /*=========================================================================================================*/
    protected abstract byte getByte0(int index);
    protected abstract short getShort0(int index);
//    protected abstract int getMedium0(int index);
    protected abstract int getInt0(int index);
    protected abstract long getLong0(int index);

    protected abstract void setByte0(int index, byte value);
    protected abstract void setShort0(int index, short value);
    protected abstract void setMedium0(int index, int value);
    protected abstract void setInt0(int index, int value);
    protected abstract void setLong0(int index, long value);
}
