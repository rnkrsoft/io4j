package com.rnkrsoft.io.buffer;

import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public abstract class PooledByteBuffer extends AbstractReferenceCountedByteBuffer {
    protected PooledByteBuffer(int maxCapacity) {
        super(maxCapacity);
    }

    public int capacity() {
        return 0;
    }

    public ByteBuffer capacity(int newCapacity) {
        return null;
    }

    public boolean isDirect() {
        return false;
    }

    public ByteBuffer copy(int index, int length) {
        return null;
    }

    public ByteBuffer slice(int index, int length) {
        return null;
    }

    public boolean hasArray() {
        return false;
    }

    public byte[] array() {
        return new byte[0];
    }

    public int arrayOffset() {
        return 0;
    }

    public boolean hasMemoryAddress() {
        return false;
    }

    public long memoryAddress() {
        return 0;
    }

    public String toString(int index, int length, Charset charset) {
        return null;
    }
}
