/*
* Copyright 2014 The Netty Project
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

/**
 * Special {@link SwappedByteBuffer} for {@link ByteBuffer}s that are backed by a {@code memoryAddress}.
 */
final class UnsafeDirectSwappedByteBuffer extends AbstractUnsafeSwappedByteBuffer {

    UnsafeDirectSwappedByteBuffer(AbstractByteBuffer buf) {
        super(buf);
    }

    private static long addr(AbstractByteBuffer wrapped, int index) {
        // We need to call wrapped.memoryAddress() everytime and NOT cache it as it may change if the buffer expand.
        // See:
        // - https://github.com/netty/netty/issues/2587
        // - https://github.com/netty/netty/issues/2580
        return wrapped.memoryAddress() + index;
    }

    @Override
    protected long _getLong(AbstractByteBuffer wrapped, int index) {
        return PlatformDependent.getLong(addr(wrapped, index));
    }

    @Override
    protected int _getInt(AbstractByteBuffer wrapped, int index) {
        return PlatformDependent.getInt(addr(wrapped, index));
    }

    @Override
    protected short _getShort(AbstractByteBuffer wrapped, int index) {
        return PlatformDependent.getShort(addr(wrapped, index));
    }

    @Override
    protected void _setShort(AbstractByteBuffer wrapped, int index, short value) {
        PlatformDependent.putShort(addr(wrapped, index), value);
    }

    @Override
    protected void _setInt(AbstractByteBuffer wrapped, int index, int value) {
        PlatformDependent.putInt(addr(wrapped, index), value);
    }

    @Override
    protected void _setLong(AbstractByteBuffer wrapped, int index, long value) {
        PlatformDependent.putLong(addr(wrapped, index), value);
    }
}
