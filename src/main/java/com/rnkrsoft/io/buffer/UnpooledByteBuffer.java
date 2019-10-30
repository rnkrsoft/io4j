package com.rnkrsoft.io.buffer;

import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public abstract class UnpooledByteBuffer<T> extends AbstractReferenceCountedByteBuffer {
    protected T carrier;
    protected int offset;
    protected int length;

    protected UnpooledByteBuffer(int maxCapacity) {
        super(maxCapacity);
    }

    public ByteBuffer copy(int index, int length) {
        return null;
    }

    public ByteBuffer slice(int index, int length) {
        return null;
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



}
