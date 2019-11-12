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

import java.nio.ByteOrder;

/**
 * A {@link ByteBuffer} implementation that wraps another buffer to prevent a user from increasing or decreasing the
 * wrapped buffer's reference count.
 */
final class UnreleasableByteBuffer extends WrappedByteBuffer {

    private SwappedByteBuffer swappedBuf;

    UnreleasableByteBuffer(ByteBuffer buf) {
        super(buf);
    }

    @Override
    public ByteBuffer order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (endianness == order()) {
            return this;
        }

        SwappedByteBuffer swappedBuf = this.swappedBuf;
        if (swappedBuf == null) {
            this.swappedBuf = swappedBuf = new SwappedByteBuffer(this);
        }
        return swappedBuf;
    }

    @Override
    public ByteBuffer readSlice(int length) {
        return new UnreleasableByteBuffer(buf.readSlice(length));
    }

    @Override
    public ByteBuffer slice() {
        return new UnreleasableByteBuffer(buf.slice());
    }

    @Override
    public ByteBuffer slice(int index, int length) {
        return new UnreleasableByteBuffer(buf.slice(index, length));
    }

    @Override
    public ByteBuffer duplicate() {
        return new UnreleasableByteBuffer(buf.duplicate());
    }

    @Override
    public ByteBuffer retain(int increment) {
        return this;
    }

    @Override
    public ByteBuffer retain() {
        return this;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int decrement) {
        return false;
    }
}
