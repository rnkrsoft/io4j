package com.rnkrsoft.io.buffer;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class UnpooledDirectByteBuffer extends UnpooledByteBuffer {
    boolean autoExpand;
    protected UnpooledDirectByteBuffer(int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    public boolean isAutoExpand() {
        return autoExpand;
    }

    @Override
    public ByteBuffer autoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
        return this;
    }

    public int capacity() {
        return 0;
    }

    public ByteBuffer capacity(int newCapacity) {
        return null;
    }

    public boolean isDirect() {
        return true;
    }

    public boolean hasArray() {
        return false;
    }

    @Override
    protected void deallocate() {

    }

    public ReferenceCounted touch(Object hint) {
        return null;
    }
}
