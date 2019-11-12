package com.rnkrsoft.io.buffer;


import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by rnkrsoft on 2019/10/30.
 * 一个基于堆字节数组的非池化字节缓冲区
 */
class UnpooledHeapByteBuffer extends UnpooledByteBuffer<byte[]>{
    java.nio.ByteBuffer tmpNioBuf;

    UnpooledHeapByteBuffer(int initCapacity, int maxCapacity) {
        this(new byte[initCapacity], 0, 0, maxCapacity);
    }

    UnpooledHeapByteBuffer(byte[] initialArray, int readerIndex, int maxCapacity) {
        super(maxCapacity);
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(MessageFormatter.format( "initialCapacity({}) > maxCapacity({})", initialArray.length, maxCapacity));
        }

        setArray(initialArray);
        setIndex(readerIndex, initialArray.length);
    }
    UnpooledHeapByteBuffer(byte[] initialArray, int readerIndex, int writerIndex, int maxCapacity) {
        super(maxCapacity);
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(MessageFormatter.format( "initialCapacity({}) > maxCapacity({})", initialArray.length, maxCapacity));
        }

        setArray(initialArray);
        setIndex(readerIndex, writerIndex);
    }


    public int capacity() {
        return carrier.length;
    }

    public ByteBuffer capacity(int newCapacity) {
        if (newCapacity < 0 || newCapacity > maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity);
        }

        int oldCapacity = carrier.length;
        if (newCapacity > oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            System.arraycopy(carrier, 0, newArray, 0, carrier.length);
            setArray(newArray);
        } else if (newCapacity < oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            int readerIndex = readerIndex();
            if (readerIndex < newCapacity) {
                int writerIndex = writerIndex();
                if (writerIndex > newCapacity) {
                    writerIndex(writerIndex = newCapacity);
                }
                System.arraycopy(carrier, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
            } else {
                setIndex(newCapacity, newCapacity);
            }
            setArray(newArray);
        }
        return this;
    }


    public final int nioBufferCount() {
        return 1;
    }


    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        return java.nio.ByteBuffer.wrap(carrier, index, length).slice();
    }

    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        return (java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
    }

    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[]{nioBuffer(index, length)};
    }

    public final boolean isDirect() {
        return false;
    }


    @Override
    protected void deallocate() {

    }

    public boolean hasArray() {
        return true;
    }

    private void setArray(byte[] initialArray) {
        this.carrier = initialArray;
        tmpNioBuf = null;
    }

    public byte[] array() {
        return this.carrier;
    }

    public ReferenceCounted touch(Object hint) {
        return null;
    }





    java.nio.ByteBuffer internalNioBuffer() {
        java.nio.ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = java.nio.ByteBuffer.wrap(carrier);
        }
        return tmpNioBuf;
    }

    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasMemoryAddress()) {
            //fixme
//            copyMemory(array(), index, dst.memoryAddress() + dstIndex, length);
        } else if (dst.hasArray()) {
            getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else {
            dst.setBytes(dstIndex, array(), index, length);
        }
        return this;
    }

    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.length);
        System.arraycopy(array(), index, dst, dstIndex, length);
        return this;
    }


    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        dst.put(array(), index, dst.remaining());
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return getBytes(index, out, position, length, false);
    }

    int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = java.nio.ByteBuffer.wrap(array());
        }
        return out.write((java.nio.ByteBuffer) tmpBuf.clear().position(index).limit(index + length));
    }

    int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = java.nio.ByteBuffer.wrap(array());
        }
        return out.write((java.nio.ByteBuffer) tmpBuf.clear().position(index).limit(index + length), position);
    }
    @Override
    protected byte getByte0(int index) {
        return HeapByteBuffers.getByte(carrier, index);
    }

    @Override
    protected short getShort0(int index) {
        return HeapByteBuffers.getShort(carrier, index);
    }

    @Override
    protected int getInt0(int index) {
        return HeapByteBuffers.getInt(carrier, index);
    }

    @Override
    protected long getLong0(int index) {
        return HeapByteBuffers.getLong(carrier, index);
    }

    @Override
    protected void setByte0(int index, byte value) {
        HeapByteBuffers.setByte(carrier, index, value);
    }

    @Override
    protected void setShort0(int index, short value) {
        HeapByteBuffers.setShort(carrier, index, value);
    }

    @Override
    protected void setMedium0(int index, int value) {
        HeapByteBuffers.setMedium(carrier, index, value);
    }

    @Override
    protected void setInt0(int index, int value) {
        HeapByteBuffers.setInt(carrier, index, value);
    }

    @Override
    protected void setLong0(int index, long value) {
        HeapByteBuffers.setLong(carrier, index, value);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return HeapByteBuffers.getUnsignedMedium(carrier, index);
    }

}
