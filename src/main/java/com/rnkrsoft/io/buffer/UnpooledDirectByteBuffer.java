package com.rnkrsoft.io.buffer;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class UnpooledDirectByteBuffer extends UnpooledByteBuffer<java.nio.ByteBuffer> {
    //基于nio中的直接字节缓存
    java.nio.ByteBuffer tmpNioBuf;
    UnpooledDirectByteBuffer(int initCapacity, int maxCapacity) {
        super(maxCapacity);
        if (initCapacity > maxCapacity) {
            throw new IllegalArgumentException(MessageFormatter.format("initCapacity({}) > maxCapacity({})", initCapacity, maxCapacity));
        }
        //初始化载体
        initCarrier(java.nio.ByteBuffer.allocateDirect(initCapacity), false);
    }

    UnpooledDirectByteBuffer(byte[] initialArray, int readerIndex, int maxCapacity) {
        super(maxCapacity);
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(MessageFormatter.format( "initialCapacity({}) > maxCapacity({})", initialArray.length, maxCapacity));
        }

        initCarrier(java.nio.ByteBuffer.allocateDirect(initialArray.length), false);
        setBytes(0, initialArray);
        setIndex(readerIndex, initialArray.length);
    }
    UnpooledDirectByteBuffer(byte[] initialArray, int readerIndex, int writerIndex, int maxCapacity) {
        super(maxCapacity);
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(MessageFormatter.format( "initialCapacity({}) > maxCapacity({})", initialArray.length, maxCapacity));
        }

        initCarrier(java.nio.ByteBuffer.allocateDirect(initialArray.length), false);
        setBytes(0, initialArray);
        setIndex(readerIndex, writerIndex);
    }



    /**
     * 初始化载体
     * @param buffer 使用jdk ByteBuffer实例初始化载体
     * @param tryFree 是否进行释放操作
     */
    void initCarrier(java.nio.ByteBuffer buffer, boolean tryFree) {
        if (tryFree) {
            java.nio.ByteBuffer oldBuffer = this.carrier;
            if (oldBuffer != null) {
//                if (doNotFree) {
//                    doNotFree = false;
//                } else {
//                    freeDirect(oldBuffer);
//                }
            }
        }

        this.carrier = buffer;
        this.tmpNioBuf = null;
    }

    public int capacity() {
        return carrier.remaining();
    }

    public ByteBuffer capacity(int newCapacity) {
        return null;
    }


    public int nioBufferCount() {
        return 1;
    }

    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        return ((java.nio.ByteBuffer) carrier.duplicate().position(index).limit(index + length)).slice();
    }

    java.nio.ByteBuffer internalNioBuffer() {
        java.nio.ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = carrier.duplicate();
        }
        return tmpNioBuf;
    }
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        checkIndex(index, length);
        return (java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
    }

    @Override
    protected byte getByte0(int index) {
        return carrier.get(index);
    }

    @Override
    protected short getShort0(int index) {
        return carrier.getShort(index);
    }

    @Override
    protected int getInt0(int index) {
        return carrier.getInt(index);
    }

    @Override
    protected long getLong0(int index) {
        return carrier.getLong(index);
    }

    @Override
    protected void setByte0(int index, byte value) {
        carrier.put(index, value);
    }


    @Override
    protected void setShort0(int index, short value) {
        carrier.putShort(index, value);
    }

    @Override
    protected void setMedium0(int index, int value) {
        setByte(index, (byte) (value >>> 16));
        setByte(index + 1, (byte) (value >>> 8));
        setByte(index + 2, (byte) (value));
    }

    @Override
    protected void setInt0(int index, int value) {
        carrier.putInt(index, value);
    }

    @Override
    protected void setLong0(int index, long value) {
        carrier.putLong(index, value);
    }

    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[]{ carrier};
    }


    public boolean isDirect() {
        return true;
    }

    public boolean hasArray() {
        return false;
    }

    public byte[] array() {
       throw new UnsupportedOperationException("direct buffer is unsupported array()");
    }

    @Override
    public int arrayOffset() {
       throw new UnsupportedOperationException("direct buffer is unsupported arrayOffset()");
    }

    @Override
    protected void deallocate() {

    }

    public ReferenceCounted touch(Object hint) {
        return null;
    }

    @Override
    public int getUnsignedMedium(int index) {
        return 0;
    }

    void getBytes(int index, java.nio.ByteBuffer dst, boolean internal) {
        checkIndex(index, dst.remaining());

        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = carrier.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + dst.remaining());
        dst.put(tmpBuf);
    }

    void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
        checkDstIndex(index, length, dstIndex, dst.length);

        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = carrier.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + length);
        tmpBuf.get(dst, dstIndex, length);
    }

    int getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
        if (length == 0) {
            return 0;
        }
        //fixme
        return 1;
    }

    int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        if (length == 0) {
            return 0;
        }

        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = carrier.duplicate();
        }
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        if (length == 0) {
            return 0;
        }

        java.nio.ByteBuffer tmpBuf = internal ? internalNioBuffer() : carrier.duplicate();
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf, position);
    }
    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasArray()) {
            getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else if (dst.nioBufferCount() > 0) {
            for (java.nio.ByteBuffer bb: dst.nioBuffers(dstIndex, length)) {
                int bbLen = bb.remaining();
                getBytes(index, bb);
                index += bbLen;
            }
        } else {
            dst.setBytes(dstIndex, this, index, length);
        }
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        getBytes(index, dst, dstIndex, length, false);
        return this;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        getBytes(index, dst, false);
        return this;
    }

    @Override
    public int getBytes(int index, OutputStream out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return getBytes(index, out, position, length, false);
    }
}
