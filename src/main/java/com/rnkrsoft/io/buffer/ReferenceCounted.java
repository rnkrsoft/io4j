package com.rnkrsoft.io.buffer;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public interface ReferenceCounted {
    int refCnt();

    ReferenceCounted retain();

    ReferenceCounted retain(int increment);

    ReferenceCounted touch();

    ReferenceCounted touch(Object hint);

    boolean release();

    boolean release(int decrement);
}
