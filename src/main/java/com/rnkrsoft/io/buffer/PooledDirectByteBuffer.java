package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.process.ByteProcessor;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class PooledDirectByteBuffer extends PooledByteBuffer {
    protected PooledDirectByteBuffer(int initCapacity, int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    protected byte getByte0(int index) {
        return 0;
    }

    @Override
    protected short getShort0(int index) {
        return 0;
    }

    @Override
    protected int getInt0(int index) {
        return 0;
    }

    @Override
    protected long getLong0(int index) {
        return 0;
    }

    @Override
    protected void setByte0(int index, byte value) {

    }


    @Override
    protected void setShort0(int index, short value) {

    }

    @Override
    protected void setMedium0(int index, int value) {

    }

    @Override
    protected void setInt0(int index, int value) {

    }

    @Override
    protected void setLong0(int index, long value) {

    }


    public boolean isDirect() {
        return true;
    }

    @Override
    protected void deallocate() {

    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return null;
    }

    @Override
    public int getUnsignedMedium(int index) {
        return 0;
    }

    @Override
    public ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        return null;
    }

    @Override
    public ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        return null;
    }

    @Override
    public ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        return null;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return 0;
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return 0;
    }
}
