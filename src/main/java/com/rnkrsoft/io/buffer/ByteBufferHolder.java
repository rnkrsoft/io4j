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

import com.rnkrsoft.io.buffer.util.ReferenceCounted;

/**
 * A packet which is send or receive.
 */
public interface ByteBufferHolder extends ReferenceCounted {

    /**
     * Return the data which is held by this {@link ByteBufferHolder}.
     */
    ByteBuffer content();

    /**
     * Create a deep copy of this {@link ByteBufferHolder}.
     */
    ByteBufferHolder copy();

    /**
     * Duplicate the {@link ByteBufferHolder}. Be aware that this will not automatically call {@link #retain()}.
     */
    ByteBufferHolder duplicate();

    @Override
    ByteBufferHolder retain();

    @Override
    ByteBufferHolder retain(int increment);
}
