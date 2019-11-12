/*
 * Copyright 2012 The Netty Project
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

/**
 * Implementations are responsible to allocate buffers. Implementations of this interface are expected to be
 * thread-safe.
 */
public interface ByteBufferAllocator {

    ByteBufferAllocator DEFAULT = ByteBufferUtil.DEFAULT_ALLOCATOR;

    /**
     * Allocate a {@link ByteBuffer}. If it is a direct or heap buffer
     * depends on the actual implementation.
     */
    ByteBuffer buffer();

    /**
     * Allocate a {@link ByteBuffer} with the given initial capacity.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    ByteBuffer buffer(int initialCapacity);

    /**
     * Allocate a {@link ByteBuffer} with the given initial capacity and the given
     * maximal capacity. If it is a direct or heap buffer depends on the actual
     * implementation.
     */
    ByteBuffer buffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link ByteBuffer}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuffer ioBuffer();

    /**
     * Allocate a {@link ByteBuffer}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuffer ioBuffer(int initialCapacity);

    /**
     * Allocate a {@link ByteBuffer}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuffer ioBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a heap {@link ByteBuffer}.
     */
    ByteBuffer heapBuffer();

    /**
     * Allocate a heap {@link ByteBuffer} with the given initial capacity.
     */
    ByteBuffer heapBuffer(int initialCapacity);

    /**
     * Allocate a heap {@link ByteBuffer} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuffer heapBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a direct {@link ByteBuffer}.
     */
    ByteBuffer directBuffer();

    /**
     * Allocate a direct {@link ByteBuffer} with the given initial capacity.
     */
    ByteBuffer directBuffer(int initialCapacity);

    /**
     * Allocate a direct {@link ByteBuffer} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuffer directBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link CompositeByteBuffer}.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuffer compositeBuffer();

    /**
     * Allocate a {@link CompositeByteBuffer} with the given maximum number of components that can be stored in it.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuffer compositeBuffer(int maxNumComponents);

    /**
     * Allocate a heap {@link CompositeByteBuffer}.
     */
    CompositeByteBuffer compositeHeapBuffer();

    /**
     * Allocate a heap {@link CompositeByteBuffer} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuffer compositeHeapBuffer(int maxNumComponents);

    /**
     * Allocate a direct {@link CompositeByteBuffer}.
     */
    CompositeByteBuffer compositeDirectBuffer();

    /**
     * Allocate a direct {@link CompositeByteBuffer} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuffer compositeDirectBuffer(int maxNumComponents);

    /**
     * Returns {@code true} if direct {@link ByteBuffer}'s are pooled
     */
    boolean isDirectBufferPooled();
}
