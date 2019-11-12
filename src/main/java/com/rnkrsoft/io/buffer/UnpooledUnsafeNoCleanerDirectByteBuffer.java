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

import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

class UnpooledUnsafeNoCleanerDirectByteBuffer extends UnpooledUnsafeDirectByteBuffer {

    UnpooledUnsafeNoCleanerDirectByteBuffer(ByteBufferAllocator alloc, int initialCapacity, int maxCapacity) {
        super(alloc, initialCapacity, maxCapacity);
    }

    @Override
    protected java.nio.ByteBuffer allocateDirect(int initialCapacity) {
        return PlatformDependent.allocateDirectNoCleaner(initialCapacity);
    }

    java.nio.ByteBuffer reallocateDirect(java.nio.ByteBuffer oldBuffer, int initialCapacity) {
        return PlatformDependent.reallocateDirectNoCleaner(oldBuffer, initialCapacity);
    }

    @Override
    protected void freeDirect(java.nio.ByteBuffer buffer) {
        PlatformDependent.freeDirectNoCleaner(buffer);
    }

    @Override
    public ByteBuffer capacity(int newCapacity) {
        checkNewCapacity(newCapacity);

        int oldCapacity = capacity();
        if (newCapacity == oldCapacity) {
            return this;
        }

        java.nio.ByteBuffer newBuffer = reallocateDirect(buffer, newCapacity);

        if (newCapacity < oldCapacity) {
            if (readerIndex() < newCapacity) {
                if (writerIndex() > newCapacity) {
                    writerIndex(newCapacity);
                }
            } else {
                setIndex(newCapacity, newCapacity);
            }
        }
        setByteBuffer(newBuffer, false);
        return this;
    }
}
