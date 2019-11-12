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
 * Special {@link SwappedByteBuffer} for {@link ByteBuffer}s that use unsafe to access the byte array.
 */
final class UnsafeHeapSwappedByteBuffer extends AbstractUnsafeSwappedByteBuffer {

    UnsafeHeapSwappedByteBuffer(AbstractByteBuffer buf) {
        super(buf);
    }

    private static int idx(ByteBuffer wrapped, int index) {
        return wrapped.arrayOffset() + index;
    }

    @Override
    protected long _getLong(AbstractByteBuffer wrapped, int index) {
        return PlatformDependent.getLong(wrapped.array(), idx(wrapped, index));
    }

    @Override
    protected int _getInt(AbstractByteBuffer wrapped, int index) {
        return PlatformDependent.getInt(wrapped.array(), idx(wrapped, index));
    }

    @Override
    protected short _getShort(AbstractByteBuffer wrapped, int index) {
        return PlatformDependent.getShort(wrapped.array(), idx(wrapped, index));
    }

    @Override
    protected void _setShort(AbstractByteBuffer wrapped, int index, short value) {
        PlatformDependent.putShort(wrapped.array(), idx(wrapped, index), value);
    }

    @Override
    protected void _setInt(AbstractByteBuffer wrapped, int index, int value) {
        PlatformDependent.putInt(wrapped.array(), idx(wrapped, index), value);
    }

    @Override
    protected void _setLong(AbstractByteBuffer wrapped, int index, long value) {
        PlatformDependent.putLong(wrapped.array(), idx(wrapped, index), value);
    }
}
