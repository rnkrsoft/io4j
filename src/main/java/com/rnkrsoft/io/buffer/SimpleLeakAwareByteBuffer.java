/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.util.ResourceLeakDetector;
import com.rnkrsoft.io.buffer.util.ResourceLeakTracker;
import com.rnkrsoft.io.buffer.util.internal.ObjectUtil;

import java.nio.ByteOrder;

class SimpleLeakAwareByteBuffer extends WrappedByteBuffer {

    /**
     * This object's is associated with the {@link ResourceLeakTracker}. When {@link ResourceLeakTracker#close(Object)}
     * is called this object will be used as the argument. It is also assumed that this object is used when
     * {@link ResourceLeakDetector#track(Object)} is called to create {@link #leak}.
     */
    private final ByteBuffer trackedByteBuf;
    final ResourceLeakTracker<ByteBuffer> leak;

    SimpleLeakAwareByteBuffer(ByteBuffer wrapped, ByteBuffer trackedByteBuf, ResourceLeakTracker<ByteBuffer> leak) {
        super(wrapped);
        this.trackedByteBuf = ObjectUtil.checkNotNull(trackedByteBuf, "trackedByteBuf");
        this.leak = ObjectUtil.checkNotNull(leak, "leak");
    }

    SimpleLeakAwareByteBuffer(ByteBuffer wrapped, ResourceLeakTracker<ByteBuffer> leak) {
        this(wrapped, wrapped, leak);
    }

    @Override
    public ByteBuffer slice() {
        return newSharedLeakAwareByteBuf(super.slice());
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return newSharedLeakAwareByteBuf(super.slice(index, length));
    }

    @Override
    public ByteBuffer duplicate() {
        return newSharedLeakAwareByteBuf(super.duplicate());
    }

    @Override
    public ByteBuffer readSlice(int length) {
        return newSharedLeakAwareByteBuf(super.readSlice(length));
    }

    @Override
    public boolean release() {
        if (super.release()) {
            closeLeak();
            return true;
        }
        return false;
    }

    @Override
    public boolean release(int decrement) {
        if (super.release(decrement)) {
            closeLeak();
            return true;
        }
        return false;
    }

    private void closeLeak() {
        // Close the ResourceLeakTracker with the tracked ByteBuf as argument. This must be the same that was used when
        // calling DefaultResourceLeak.track(...).
        boolean closed = leak.close(trackedByteBuf);
        assert closed;
    }

    @Override
    public ByteBuffer order(ByteOrder endianness) {
        if (order() == endianness) {
            return this;
        } else {
            return newSharedLeakAwareByteBuf(super.order(endianness));
        }
    }

    private SimpleLeakAwareByteBuffer newSharedLeakAwareByteBuf(
            ByteBuffer wrapped) {
        return newLeakAwareByteBuf(wrapped, trackedByteBuf, leak);
    }

    protected SimpleLeakAwareByteBuffer newLeakAwareByteBuf(
            ByteBuffer buf, ByteBuffer trackedByteBuf, ResourceLeakTracker<ByteBuffer> leakTracker) {
        return new SimpleLeakAwareByteBuffer(buf, trackedByteBuf, leakTracker);
    }
}
