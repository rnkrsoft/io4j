package com.rnkrsoft.io.buffer;


import com.rnkrsoft.io.buffer.exception.IllegalReferenceCountException;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
abstract class AbstractReferenceCountedByteBuffer extends AbstractByteBuffer {
    static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuffer> refCntUpdater;

    static {
        AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuffer> updater = null;
        if (updater == null) {
            updater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuffer.class, "refCnt");
        }
        refCntUpdater = updater;
    }

    volatile int refCnt = 1;

    protected AbstractReferenceCountedByteBuffer(int maxCapacity) {
        super(maxCapacity);
    }

    public int refCnt() {
        return this.refCnt;
    }

    protected final void setRefCnt(int refCnt) {
        this.refCnt = refCnt;
    }

    public ReferenceCounted retain() {
        for (; ; ) {
            int refCnt = this.refCnt;
            final int nextCnt = refCnt + 1;

            // Ensure we not resurrect (which means the refCnt was 0) and also that we encountered an overflow.
            if (nextCnt <= 1) {
                throw new IllegalReferenceCountException(refCnt, 1);
            }
            if (refCntUpdater.compareAndSet(this, refCnt, nextCnt)) {
                break;
            }
        }
        return this;
    }

    public ReferenceCounted retain(int increment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("increment: " + increment + " (expected: > 0)");
        }

        for (; ; ) {
            int refCnt = this.refCnt;
            final int nextCnt = refCnt + increment;

            // Ensure we not resurrect (which means the refCnt was 0) and also that we encountered an overflow.
            if (nextCnt <= increment) {
                throw new IllegalReferenceCountException(refCnt, increment);
            }
            if (refCntUpdater.compareAndSet(this, refCnt, nextCnt)) {
                break;
            }
        }
        return this;
    }

    public ReferenceCounted touch() {
        return touch(null);
    }

    public boolean release() {
        for (; ; ) {
            int refCnt = this.refCnt;
            if (refCnt == 0) {
                throw new IllegalReferenceCountException(0, -1);
            }

            if (refCntUpdater.compareAndSet(this, refCnt, refCnt - 1)) {
                if (refCnt == 1) {
                    deallocate();
                    return true;
                }
                return false;
            }
        }
    }

    public boolean release(int decrement) {
        if (decrement <= 0) {
            throw new IllegalArgumentException("decrement: " + decrement + " (expected: > 0)");
        }

        for (; ; ) {
            int refCnt = this.refCnt;
            if (refCnt < decrement) {
                throw new IllegalReferenceCountException(refCnt, -decrement);
            }

            if (refCntUpdater.compareAndSet(this, refCnt, refCnt - decrement)) {
                if (refCnt == decrement) {
                    deallocate();
                    return true;
                }
                return false;
            }
        }
    }


    protected abstract void deallocate();
}
