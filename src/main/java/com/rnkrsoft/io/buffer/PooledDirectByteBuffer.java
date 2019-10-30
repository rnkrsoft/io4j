package com.rnkrsoft.io.buffer;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class PooledDirectByteBuffer extends PooledByteBuffer {
    protected PooledDirectByteBuffer(int maxCapacity) {
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
        return true;
    }

    @Override
    protected void deallocate() {

    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return null;
    }
}
