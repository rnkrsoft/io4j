package com.rnkrsoft.io.buffer.memory;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class MemoryChunk<T> {
    final T carrier;

    public MemoryChunk(T carrier) {
        this.carrier = carrier;
    }
}
