/*
 * Copyright 2016 The Netty Project
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


import com.rnkrsoft.io.buffer.util.ResourceLeakTracker;
import com.rnkrsoft.io.buffer.util.internal.ObjectUtil;

import java.nio.ByteOrder;

class SimpleLeakAwareCompositeByteBuffer extends WrappedCompositeByteBuffer {

    final ResourceLeakTracker<ByteBuffer> leak;

    SimpleLeakAwareCompositeByteBuffer(CompositeByteBuffer wrapped, ResourceLeakTracker<ByteBuffer> leak) {
        super(wrapped);
        this.leak = ObjectUtil.checkNotNull(leak, "leak");
    }

    @Override
    public boolean release() {
        // Call unwrap() before just in case that super.release() will change the ByteBuf instance that is returned
        // by unwrap().
        ByteBuffer unwrapped = unwrap();
        if (super.release()) {
            closeLeak(unwrapped);
            return true;
        }
        return false;
    }

    @Override
    public boolean release(int decrement) {
        // Call unwrap() before just in case that super.release() will change the ByteBuf instance that is returned
        // by unwrap().
        ByteBuffer unwrapped = unwrap();
        if (super.release(decrement)) {
            closeLeak(unwrapped);
            return true;
        }
        return false;
    }

    private void closeLeak(ByteBuffer trackedByteBuf) {
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
            return newLeakAwareByteBuf(super.order(endianness));
        }
    }

    @Override
    public ByteBuffer slice() {
        return newLeakAwareByteBuf(super.slice());
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return newLeakAwareByteBuf(super.slice(index, length));
    }

    @Override
    public ByteBuffer duplicate() {
        return newLeakAwareByteBuf(super.duplicate());
    }

    @Override
    public ByteBuffer readSlice(int length) {
        return newLeakAwareByteBuf(super.readSlice(length));
    }

    private SimpleLeakAwareByteBuffer newLeakAwareByteBuf(ByteBuffer wrapped) {
        return newLeakAwareByteBuf(wrapped, unwrap(), leak);
    }

    protected SimpleLeakAwareByteBuffer newLeakAwareByteBuf(
            ByteBuffer wrapped, ByteBuffer trackedByteBuf, ResourceLeakTracker<ByteBuffer> leakTracker) {
        return new SimpleLeakAwareByteBuffer(wrapped, trackedByteBuf, leakTracker);
    }
}
