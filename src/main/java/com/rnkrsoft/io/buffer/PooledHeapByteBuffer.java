/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to the License at:
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

import com.rnkrsoft.io.buffer.util.Recycler;
import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

class PooledHeapByteBuffer extends PooledByteBuffer<byte[]> {

    private static final Recycler<PooledHeapByteBuffer> RECYCLER = new Recycler<PooledHeapByteBuffer>() {
        @Override
        protected PooledHeapByteBuffer newObject(Handle handle) {
            return new PooledHeapByteBuffer(handle, 0);
        }
    };

    static PooledHeapByteBuffer newInstance(int maxCapacity) {
        PooledHeapByteBuffer buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    PooledHeapByteBuffer(Recycler.Handle recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    public final boolean isDirect() {
        return false;
    }

    @Override
    protected byte _getByte(int index) {
        return HeapByteBufferUtil.getByte(memory, idx(index));
    }

    @Override
    protected short _getShort(int index) {
        return HeapByteBufferUtil.getShort(memory, idx(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return HeapByteBufferUtil.getUnsignedMedium(memory, idx(index));
    }

    @Override
    protected int _getInt(int index) {
        return HeapByteBufferUtil.getInt(memory, idx(index));
    }

    @Override
    protected long _getLong(int index) {
        return HeapByteBufferUtil.getLong(memory, idx(index));
    }

    @Override
    public final ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory(memory, idx(index), dst.memoryAddress() + dstIndex, length);
        } else if (dst.hasArray()) {
            getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else {
            dst.setBytes(dstIndex, memory, idx(index), length);
        }
        return this;
    }

    @Override
    public final ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.length);
        System.arraycopy(memory, idx(index), dst, dstIndex, length);
        return this;
    }

    @Override
    public final ByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        checkIndex(index, dst.remaining());
        dst.put(memory, idx(index), dst.remaining());
        return this;
    }

    @Override
    public final ByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        checkIndex(index, length);
        out.write(memory, idx(index), length);
        return this;
    }

    @Override
    public final int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        checkIndex(index, length);
        index = idx(index);
        java.nio.ByteBuffer tmpBuf;
        if (internal) {
            tmpBuf = internalNioBuffer();
        } else {
            tmpBuf = java.nio.ByteBuffer.wrap(memory);
        }
        return out.write((java.nio.ByteBuffer) tmpBuf.clear().position(index).limit(index + length));
    }

    @Override
    public final int readBytes(GatheringByteChannel out, int length) throws IOException {
        checkReadableBytes(length);
        int readBytes = getBytes(readerIndex, out, length, true);
        readerIndex += readBytes;
        return readBytes;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufferUtil.setByte(memory, idx(index), value);
    }

    @Override
    protected void _setShort(int index, int value) {
        HeapByteBufferUtil.setShort(memory, idx(index), value);
    }

    @Override
    protected void _setMedium(int index, int   value) {
        HeapByteBufferUtil.setMedium(memory, idx(index), value);
    }

    @Override
    protected void _setInt(int index, int   value) {
        HeapByteBufferUtil.setInt(memory, idx(index), value);
    }

    @Override
    protected void _setLong(int index, long  value) {
        HeapByteBufferUtil.setLong(memory, idx(index), value);
    }

    @Override
    public final ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.hasMemoryAddress()) {
            PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, memory, idx(index), length);
        } else if (src.hasArray()) {
            setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
        } else {
            src.getBytes(srcIndex, memory, idx(index), length);
        }
        return this;
    }

    @Override
    public final ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.length);
        System.arraycopy(src, srcIndex, memory, idx(index), length);
        return this;
    }

    @Override
    public final ByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        int length = src.remaining();
        checkIndex(index, length);
        src.get(memory, idx(index), length);
        return this;
    }

    @Override
    public final int setBytes(int index, InputStream in, int length) throws IOException {
        checkIndex(index, length);
        return in.read(memory, idx(index), length);
    }

    @Override
    public final int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        checkIndex(index, length);
        index = idx(index);
        try {
            return in.read((java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length));
        } catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public final ByteBuffer copy(int index, int length) {
        checkIndex(index, length);
        ByteBuffer copy = alloc().heapBuffer(length, maxCapacity());
        copy.writeBytes(memory, idx(index), length);
        return copy;
    }

    @Override
    public final int nioBufferCount() {
        return 1;
    }

    @Override
    public final java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        return new java.nio.ByteBuffer[] { nioBuffer(index, length) };
    }

    @Override
    public final java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        index = idx(index);
        java.nio.ByteBuffer buf =  java.nio.ByteBuffer.wrap(memory, index, length);
        return buf.slice();
    }

    @Override
    public final java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        checkIndex(index, length);
        index = idx(index);
        return (java.nio.ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
    }

    @Override
    public final boolean hasArray() {
        return true;
    }

    @Override
    public final byte[] array() {
        ensureAccessible();
        return memory;
    }

    @Override
    public final int arrayOffset() {
        return offset;
    }

    @Override
    public final boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public final long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final java.nio.ByteBuffer newInternalNioBuffer(byte[] memory) {
        return java.nio.ByteBuffer.wrap(memory);
    }

    @Override
    protected Recycler<?> recycler() {
        return RECYCLER;
    }
}
