package com.rnkrsoft.io.buffer;


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

    public int arrayOffset() {
        return offset;
    }

    public boolean hasMemoryAddress() {
        return false;
    }

    public long memoryAddress() {
        return 0;
    }

}
