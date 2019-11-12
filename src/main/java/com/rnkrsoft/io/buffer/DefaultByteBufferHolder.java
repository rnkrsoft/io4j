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

import com.rnkrsoft.io.buffer.util.IllegalReferenceCountException;
import com.rnkrsoft.io.buffer.util.internal.StringUtil;

/**
 * Default implementation of a {@link ByteBufferHolder} that holds it's data in a {@link ByteBuffer}.
 *
 */
public class DefaultByteBufferHolder implements ByteBufferHolder {

    private final ByteBuffer data;

    public DefaultByteBufferHolder(ByteBuffer data) {
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.data = data;
    }

    @Override
    public ByteBuffer content() {
        if (data.refCnt() <= 0) {
            throw new IllegalReferenceCountException(data.refCnt());
        }
        return data;
    }

    @Override
    public ByteBufferHolder copy() {
        return new DefaultByteBufferHolder(data.copy());
    }

    @Override
    public ByteBufferHolder duplicate() {
        return new DefaultByteBufferHolder(data.duplicate());
    }

    @Override
    public int refCnt() {
        return data.refCnt();
    }

    @Override
    public ByteBufferHolder retain() {
        data.retain();
        return this;
    }

    @Override
    public ByteBufferHolder retain(int increment) {
        data.retain(increment);
        return this;
    }

    @Override
    public boolean release() {
        return data.release();
    }

    @Override
    public boolean release(int decrement) {
        return data.release(decrement);
    }

    /**
     * Return {@link ByteBuffer#toString()} without checking the reference count first. This is useful to implement
     * {@link #toString()}.
     */
    protected final String contentToString() {
        return data.toString();
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + contentToString() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ByteBufferHolder) {
            return data.equals(((ByteBufferHolder) o).content());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
