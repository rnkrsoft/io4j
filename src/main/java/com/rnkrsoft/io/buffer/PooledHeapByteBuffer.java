package com.rnkrsoft.io.buffer;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class PooledHeapByteBuffer extends PooledByteBuffer {
    protected PooledHeapByteBuffer(int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    public boolean isAutoExpand() {
        return false;
    }

    @Override
    public ByteBuffer autoExpand(boolean autoExpand) {
        return null;
    }

    public boolean isDirect() {
        return false;
    }

    @Override
    protected void deallocate() {

    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return null;
    }
}
