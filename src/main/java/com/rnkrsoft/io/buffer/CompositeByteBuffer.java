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

import com.rnkrsoft.io.buffer.util.internal.EmptyArrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static com.rnkrsoft.io.buffer.util.internal.ObjectUtil.checkNotNull;

/**
 * A virtual buffer which shows multiple buffers as a single merged buffer.  It is recommended to use
 * {@link ByteBufferAllocator#compositeBuffer()} or {@link Unpooled#wrappedBuffer(ByteBuffer...)} instead of calling the
 * constructor explicitly.
 */
class CompositeByteBuffer extends AbstractReferenceCountedByteBuffer implements Iterable<ByteBuffer> {

    private static final java.nio.ByteBuffer EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
    private static final Iterator<ByteBuffer> EMPTY_ITERATOR = Collections.<ByteBuffer>emptyList().iterator();

    private final ByteBufferAllocator alloc;
    private final boolean direct;
    private final ComponentList components;
    private final int maxNumComponents;

    private boolean freed;

    public CompositeByteBuffer(ByteBufferAllocator alloc, boolean direct, int maxNumComponents) {
        super(AbstractByteBufferAllocator.DEFAULT_MAX_CAPACITY);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        this.alloc = alloc;
        this.direct = direct;
        this.maxNumComponents = maxNumComponents;
        components = newList(maxNumComponents);
    }

    public CompositeByteBuffer(ByteBufferAllocator alloc, boolean direct, int maxNumComponents, ByteBuffer... buffers) {
        this(alloc, direct, maxNumComponents, buffers, 0, buffers.length);
    }

    CompositeByteBuffer(
            ByteBufferAllocator alloc, boolean direct, int maxNumComponents, ByteBuffer[] buffers, int offset, int len) {
        super(AbstractByteBufferAllocator.DEFAULT_MAX_CAPACITY);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (maxNumComponents < 2) {
            throw new IllegalArgumentException(
                    "maxNumComponents: " + maxNumComponents + " (expected: >= 2)");
        }

        this.alloc = alloc;
        this.direct = direct;
        this.maxNumComponents = maxNumComponents;
        components = newList(maxNumComponents);

        addComponents0(false, 0, buffers, offset, len);
        consolidateIfNeeded();
        setIndex(0, capacity());
    }

    public CompositeByteBuffer(
            ByteBufferAllocator alloc, boolean direct, int maxNumComponents, Iterable<ByteBuffer> buffers) {
        super(AbstractByteBufferAllocator.DEFAULT_MAX_CAPACITY);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (maxNumComponents < 2) {
            throw new IllegalArgumentException(
                    "maxNumComponents: " + maxNumComponents + " (expected: >= 2)");
        }

        this.alloc = alloc;
        this.direct = direct;
        this.maxNumComponents = maxNumComponents;
        components = newList(maxNumComponents);

        addComponents0(false, 0, buffers);
        consolidateIfNeeded();
        setIndex(0, capacity());
    }

    private static ComponentList newList(int maxNumComponents) {
        return new ComponentList(Math.min(AbstractByteBufferAllocator.DEFAULT_MAX_COMPONENTS, maxNumComponents));
    }

    // Special constructor used by WrappedCompositeByteBuffer
    CompositeByteBuffer(ByteBufferAllocator alloc) {
        super(Integer.MAX_VALUE);
        this.alloc = alloc;
        direct = false;
        maxNumComponents = 0;
        components = null;
    }

    /**
     * Add the given {@link ByteBuffer}.
     * <p>
     * Be aware that this method does not increase the {@code writerIndex} of the {@link CompositeByteBuffer}.
     * If you need to have it increased use {@link #addComponent(boolean, ByteBuffer)}.
     * <p>
     * {@link ByteBuffer#release()} ownership of {@code buffer} is transfered to this {@link CompositeByteBuffer}.
     * @param buffer the {@link ByteBuffer} to add. {@link ByteBuffer#release()} ownership is transfered to this
     * {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponent(ByteBuffer buffer) {
        return addComponent(false, buffer);
    }

    /**
     * Add the given {@link ByteBuffer}s.
     * <p>
     * Be aware that this method does not increase the {@code writerIndex} of the {@link CompositeByteBuffer}.
     * If you need to have it increased use {@link #addComponents(boolean, ByteBuffer[])}.
     * <p>
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects in {@code buffers} is transfered to this
     * {@link CompositeByteBuffer}.
     * @param buffers the {@link ByteBuffer}s to add. {@link ByteBuffer#release()} ownership of all {@link ByteBuffer#release()}
     * ownership of all {@link ByteBuffer} objects is transfered to this {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponents(ByteBuffer... buffers) {
        return addComponents(false, buffers);
    }

    /**
     * Add the given {@link ByteBuffer}s.
     * <p>
     * Be aware that this method does not increase the {@code writerIndex} of the {@link CompositeByteBuffer}.
     * If you need to have it increased use {@link #addComponents(boolean, Iterable)}.
     * <p>
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects in {@code buffers} is transfered to this
     * {@link CompositeByteBuffer}.
     * @param buffers the {@link ByteBuffer}s to add. {@link ByteBuffer#release()} ownership of all {@link ByteBuffer#release()}
     * ownership of all {@link ByteBuffer} objects is transfered to this {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponents(Iterable<ByteBuffer> buffers) {
        return addComponents(false, buffers);
    }

    /**
     * Add the given {@link ByteBuffer} on the specific index.
     * <p>
     * Be aware that this method does not increase the {@code writerIndex} of the {@link CompositeByteBuffer}.
     * If you need to have it increased use {@link #addComponent(boolean, int, ByteBuffer)}.
     * <p>
     * {@link ByteBuffer#release()} ownership of {@code buffer} is transfered to this {@link CompositeByteBuffer}.
     * @param cIndex the index on which the {@link ByteBuffer} will be added.
     * @param buffer the {@link ByteBuffer} to add. {@link ByteBuffer#release()} ownership is transfered to this
     * {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponent(int cIndex, ByteBuffer buffer) {
        return addComponent(false, cIndex, buffer);
    }

    /**
     * Add the given {@link ByteBuffer} and increase the {@code writerIndex} if {@code increaseWriterIndex} is
     * {@code true}.
     *
     * {@link ByteBuffer#release()} ownership of {@code buffer} is transfered to this {@link CompositeByteBuffer}.
     * @param buffer the {@link ByteBuffer} to add. {@link ByteBuffer#release()} ownership is transfered to this
     * {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponent(boolean increaseWriterIndex, ByteBuffer buffer) {
        checkNotNull(buffer, "buffer");
        addComponent0(increaseWriterIndex, components.size(), buffer);
        consolidateIfNeeded();
        return this;
    }

    /**
     * Add the given {@link ByteBuffer}s and increase the {@code writerIndex} if {@code increaseWriterIndex} is
     * {@code true}.
     *
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects in {@code buffers} is transfered to this
     * {@link CompositeByteBuffer}.
     * @param buffers the {@link ByteBuffer}s to add. {@link ByteBuffer#release()} ownership of all {@link ByteBuffer#release()}
     * ownership of all {@link ByteBuffer} objects is transfered to this {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponents(boolean increaseWriterIndex, ByteBuffer... buffers) {
        addComponents0(increaseWriterIndex, components.size(), buffers, 0, buffers.length);
        consolidateIfNeeded();
        return this;
    }

    /**
     * Add the given {@link ByteBuffer}s and increase the {@code writerIndex} if {@code increaseWriterIndex} is
     * {@code true}.
     *
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects in {@code buffers} is transfered to this
     * {@link CompositeByteBuffer}.
     * @param buffers the {@link ByteBuffer}s to add. {@link ByteBuffer#release()} ownership of all {@link ByteBuffer#release()}
     * ownership of all {@link ByteBuffer} objects is transfered to this {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponents(boolean increaseWriterIndex, Iterable<ByteBuffer> buffers) {
        addComponents0(increaseWriterIndex, components.size(), buffers);
        consolidateIfNeeded();
        return this;
    }

    /**
     * Add the given {@link ByteBuffer} on the specific index and increase the {@code writerIndex}
     * if {@code increaseWriterIndex} is {@code true}.
     *
     * {@link ByteBuffer#release()} ownership of {@code buffer} is transfered to this {@link CompositeByteBuffer}.
     * @param cIndex the index on which the {@link ByteBuffer} will be added.
     * @param buffer the {@link ByteBuffer} to add. {@link ByteBuffer#release()} ownership is transfered to this
     * {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponent(boolean increaseWriterIndex, int cIndex, ByteBuffer buffer) {
        checkNotNull(buffer, "buffer");
        addComponent0(increaseWriterIndex, cIndex, buffer);
        consolidateIfNeeded();
        return this;
    }

    /**
     * Precondition is that {@code buffer != null}.
     */
    private int addComponent0(boolean increaseWriterIndex, int cIndex, ByteBuffer buffer) {
        assert buffer != null;
        boolean wasAdded = false;
        try {
            checkComponentIndex(cIndex);

            int readableBytes = buffer.readableBytes();

            // No need to consolidate - just add a component to the list.
            @SuppressWarnings("deprecation")
            Component c = new Component(buffer.order(ByteOrder.BIG_ENDIAN).slice());
            if (cIndex == components.size()) {
                wasAdded = components.add(c);
                if (cIndex == 0) {
                    c.endOffset = readableBytes;
                } else {
                    Component prev = components.get(cIndex - 1);
                    c.offset = prev.endOffset;
                    c.endOffset = c.offset + readableBytes;
                }
            } else {
                components.add(cIndex, c);
                wasAdded = true;
                if (readableBytes != 0) {
                    updateComponentOffsets(cIndex);
                }
            }
            if (increaseWriterIndex) {
                writerIndex(writerIndex() + buffer.readableBytes());
            }
            return cIndex;
        } finally {
            if (!wasAdded) {
                buffer.release();
            }
        }
    }

    /**
     * Add the given {@link ByteBuffer}s on the specific index
     * <p>
     * Be aware that this method does not increase the {@code writerIndex} of the {@link CompositeByteBuffer}.
     * If you need to have it increased you need to handle it by your own.
     * <p>
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects in {@code buffers} is transfered to this
     * {@link CompositeByteBuffer}.
     * @param cIndex the index on which the {@link ByteBuffer} will be added. {@link ByteBuffer#release()} ownership of all
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects is transfered to this
     * {@link CompositeByteBuffer}.
     * @param buffers the {@link ByteBuffer}s to add. {@link ByteBuffer#release()} ownership of all {@link ByteBuffer#release()}
     * ownership of all {@link ByteBuffer} objects is transfered to this {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponents(int cIndex, ByteBuffer... buffers) {
        addComponents0(false, cIndex, buffers, 0, buffers.length);
        consolidateIfNeeded();
        return this;
    }

    private int addComponents0(boolean increaseWriterIndex, int cIndex, ByteBuffer[] buffers, int offset, int len) {
        checkNotNull(buffers, "buffers");
        int i = offset;
        try {
            checkComponentIndex(cIndex);

            // No need for consolidation
            while (i < len) {
                // Increment i now to prepare for the next iteration and prevent a duplicate release (addComponent0
                // will release if an exception occurs, and we also release in the finally block here).
                ByteBuffer b = buffers[i++];
                if (b == null) {
                    break;
                }
                cIndex = addComponent0(increaseWriterIndex, cIndex, b) + 1;
                int size = components.size();
                if (cIndex > size) {
                    cIndex = size;
                }
            }
            return cIndex;
        } finally {
            for (; i < len; ++i) {
                ByteBuffer b = buffers[i];
                if (b != null) {
                    try {
                        b.release();
                    } catch (Throwable ignored) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Add the given {@link ByteBuffer}s on the specific index
     *
     * Be aware that this method does not increase the {@code writerIndex} of the {@link CompositeByteBuffer}.
     * If you need to have it increased you need to handle it by your own.
     * <p>
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects in {@code buffers} is transfered to this
     * {@link CompositeByteBuffer}.
     * @param cIndex the index on which the {@link ByteBuffer} will be added.
     * @param buffers the {@link ByteBuffer}s to add.  {@link ByteBuffer#release()} ownership of all
     * {@link ByteBuffer#release()} ownership of all {@link ByteBuffer} objects is transfered to this
     * {@link CompositeByteBuffer}.
     */
    public CompositeByteBuffer addComponents(int cIndex, Iterable<ByteBuffer> buffers) {
        addComponents0(false, cIndex, buffers);
        consolidateIfNeeded();
        return this;
    }

    private int addComponents0(boolean increaseIndex, int cIndex, Iterable<ByteBuffer> buffers) {
        if (buffers instanceof ByteBuffer) {
            // If buffers also implements ByteBuf (e.g. CompositeByteBuffer), it has to go to addComponent(ByteBuf).
            return addComponent0(increaseIndex, cIndex, (ByteBuffer) buffers);
        }
        checkNotNull(buffers, "buffers");

        if (!(buffers instanceof Collection)) {
            List<ByteBuffer> list = new ArrayList<ByteBuffer>();
            try {
                for (ByteBuffer b: buffers) {
                    list.add(b);
                }
                buffers = list;
            } finally {
                if (buffers != list) {
                    for (ByteBuffer b: buffers) {
                        if (b != null) {
                            try {
                                b.release();
                            } catch (Throwable ignored) {
                                // ignore
                            }
                        }
                    }
                }
            }
        }

        Collection<ByteBuffer> col = (Collection<ByteBuffer>) buffers;
        return addComponents0(increaseIndex, cIndex, col.toArray(new ByteBuffer[col.size()]), 0 , col.size());
    }

    /**
     * This should only be called as last operation from a method as this may adjust the underlying
     * array of components and so affect the index etc.
     */
    private void consolidateIfNeeded() {
        // Consolidate if the number of components will exceed the allowed maximum by the current
        // operation.
        final int numComponents = components.size();
        if (numComponents > maxNumComponents) {
            final int capacity = components.get(numComponents - 1).endOffset;

            ByteBuffer consolidated = allocBuffer(capacity);

            // We're not using foreach to avoid creating an iterator.
            for (int i = 0; i < numComponents; i ++) {
                Component c = components.get(i);
                ByteBuffer b = c.buf;
                consolidated.writeBytes(b);
                c.freeIfNecessary();
            }
            Component c = new Component(consolidated);
            c.endOffset = c.length;
            components.clear();
            components.add(c);
        }
    }

    private void checkComponentIndex(int cIndex) {
        ensureAccessible();
        if (cIndex < 0 || cIndex > components.size()) {
            throw new IndexOutOfBoundsException(String.format(
                    "cIndex: %d (expected: >= 0 && <= numComponents(%d))",
                    cIndex, components.size()));
        }
    }

    private void checkComponentIndex(int cIndex, int numComponents) {
        ensureAccessible();
        if (cIndex < 0 || cIndex + numComponents > components.size()) {
            throw new IndexOutOfBoundsException(String.format(
                    "cIndex: %d, numComponents: %d " +
                    "(expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))",
                    cIndex, numComponents, components.size()));
        }
    }

    private void updateComponentOffsets(int cIndex) {
        int size = components.size();
        if (size <= cIndex) {
            return;
        }

        Component c = components.get(cIndex);
        if (cIndex == 0) {
            c.offset = 0;
            c.endOffset = c.length;
            cIndex ++;
        }

        for (int i = cIndex; i < size; i ++) {
            Component prev = components.get(i - 1);
            Component cur = components.get(i);
            cur.offset = prev.endOffset;
            cur.endOffset = cur.offset + cur.length;
        }
    }

    /**
     * Remove the {@link ByteBuffer} from the given index.
     *
     * @param cIndex the index on from which the {@link ByteBuffer} will be remove
     */
    public CompositeByteBuffer removeComponent(int cIndex) {
        checkComponentIndex(cIndex);
        Component comp = components.remove(cIndex);
        comp.freeIfNecessary();
        if (comp.length > 0) {
            // Only need to call updateComponentOffsets if the length was > 0
            updateComponentOffsets(cIndex);
        }
        return this;
    }

    /**
     * Remove the number of {@link ByteBuffer}s starting from the given index.
     *
     * @param cIndex the index on which the {@link ByteBuffer}s will be started to removed
     * @param numComponents the number of components to remove
     */
    public CompositeByteBuffer removeComponents(int cIndex, int numComponents) {
        checkComponentIndex(cIndex, numComponents);

        if (numComponents == 0) {
            return this;
        }
        int endIndex = cIndex + numComponents;
        boolean needsUpdate = false;
        for (int i = cIndex; i < endIndex; ++i) {
            Component c = components.get(i);
            if (c.length > 0) {
                needsUpdate = true;
            }
            c.freeIfNecessary();
        }
        components.removeRange(cIndex, endIndex);

        if (needsUpdate) {
            // Only need to call updateComponentOffsets if the length was > 0
            updateComponentOffsets(cIndex);
        }
        return this;
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        ensureAccessible();
        if (components.isEmpty()) {
            return EMPTY_ITERATOR;
        }
        return new CompositeByteBufIterator();
    }

    /**
     * Same with {@link #slice(int, int)} except that this method returns a list.
     */
    public List<ByteBuffer> decompose(int offset, int length) {
        checkIndex(offset, length);
        if (length == 0) {
            return Collections.emptyList();
        }

        int componentId = toComponentIndex(offset);
        List<ByteBuffer> slice = new ArrayList<ByteBuffer>(components.size());

        // The first component
        Component firstC = components.get(componentId);
        ByteBuffer first = firstC.buf.duplicate();
        first.readerIndex(offset - firstC.offset);

        ByteBuffer buf = first;
        int bytesToSlice = length;
        do {
            int readableBytes = buf.readableBytes();
            if (bytesToSlice <= readableBytes) {
                // Last component
                buf.writerIndex(buf.readerIndex() + bytesToSlice);
                slice.add(buf);
                break;
            } else {
                // Not the last component
                slice.add(buf);
                bytesToSlice -= readableBytes;
                componentId ++;

                // Fetch the next component.
                buf = components.get(componentId).buf.duplicate();
            }
        } while (bytesToSlice > 0);

        // Slice all components because only readable bytes are interesting.
        for (int i = 0; i < slice.size(); i ++) {
            slice.set(i, slice.get(i).slice());
        }

        return slice;
    }

    @Override
    public boolean isDirect() {
        int size = components.size();
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < size; i++) {
           if (!components.get(i).buf.isDirect()) {
               return false;
           }
        }
        return true;
    }

    @Override
    public boolean hasArray() {
        switch (components.size()) {
        case 0:
            return true;
        case 1:
            return components.get(0).buf.hasArray();
        default:
            return false;
        }
    }

    @Override
    public byte[] array() {
        switch (components.size()) {
        case 0:
            return EmptyArrays.EMPTY_BYTES;
        case 1:
            return components.get(0).buf.array();
        default:
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int arrayOffset() {
        switch (components.size()) {
        case 0:
            return 0;
        case 1:
            return components.get(0).buf.arrayOffset();
        default:
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasMemoryAddress() {
        switch (components.size()) {
        case 0:
            return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
        case 1:
            return components.get(0).buf.hasMemoryAddress();
        default:
            return false;
        }
    }

    @Override
    public long memoryAddress() {
        switch (components.size()) {
        case 0:
            return Unpooled.EMPTY_BUFFER.memoryAddress();
        case 1:
            return components.get(0).buf.memoryAddress();
        default:
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int capacity() {
        final int numComponents = components.size();
        if (numComponents == 0) {
            return 0;
        }
        return components.get(numComponents - 1).endOffset;
    }

    @Override
    public CompositeByteBuffer capacity(int newCapacity) {
        checkNewCapacity(newCapacity);

        int oldCapacity = capacity();
        if (newCapacity > oldCapacity) {
            final int paddingLength = newCapacity - oldCapacity;
            ByteBuffer padding;
            int nComponents = components.size();
            if (nComponents < maxNumComponents) {
                padding = allocBuffer(paddingLength);
                padding.setIndex(0, paddingLength);
                addComponent0(false, components.size(), padding);
            } else {
                padding = allocBuffer(paddingLength);
                padding.setIndex(0, paddingLength);
                // FIXME: No need to create a padding buffer and consolidate.
                // Just create a big single buffer and put the current content there.
                addComponent0(false, components.size(), padding);
                consolidateIfNeeded();
            }
        } else if (newCapacity < oldCapacity) {
            int bytesToTrim = oldCapacity - newCapacity;
            for (ListIterator<Component> i = components.listIterator(components.size()); i.hasPrevious();) {
                Component c = i.previous();
                if (bytesToTrim >= c.length) {
                    bytesToTrim -= c.length;
                    i.remove();
                    continue;
                }

                // Replace the last component with the trimmed slice.
                Component newC = new Component(c.buf.slice(0, c.length - bytesToTrim));
                newC.offset = c.offset;
                newC.endOffset = newC.offset + newC.length;
                i.set(newC);
                break;
            }

            if (readerIndex() > newCapacity) {
                setIndex(newCapacity, newCapacity);
            } else if (writerIndex() > newCapacity) {
                writerIndex(newCapacity);
            }
        }
        return this;
    }

    @Override
    public ByteBufferAllocator alloc() {
        return alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    /**
     * Return the current number of {@link ByteBuffer}'s that are composed in this instance
     */
    public int numComponents() {
        return components.size();
    }

    /**
     * Return the max number of {@link ByteBuffer}'s that are composed in this instance
     */
    public int maxNumComponents() {
        return maxNumComponents;
    }

    /**
     * Return the index for the given offset
     */
    public int toComponentIndex(int offset) {
        checkIndex(offset);

        for (int low = 0, high = components.size(); low <= high;) {
            int mid = low + high >>> 1;
            Component c = components.get(mid);
            if (offset >= c.endOffset) {
                low = mid + 1;
            } else if (offset < c.offset) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        throw new Error("should not reach here");
    }

    public int toByteIndex(int cIndex) {
        checkComponentIndex(cIndex);
        return components.get(cIndex).offset;
    }

    @Override
    public byte getByte(int index) {
        return _getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        Component c = findComponent(index);
        return c.buf.getByte(index - c.offset);
    }

    @Override
    protected short _getShort(int index) {
        Component c = findComponent(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShort(index - c.offset);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (short) ((_getByte(index) & 0xff) << 8 | _getByte(index + 1) & 0xff);
        } else {
            return (short) (_getByte(index) & 0xff | (_getByte(index + 1) & 0xff) << 8);
        }
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        Component c = findComponent(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMedium(index - c.offset);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (_getShort(index) & 0xffff) << 8 | _getByte(index + 2) & 0xff;
        } else {
            return _getShort(index) & 0xFFFF | (_getByte(index + 2) & 0xFF) << 16;
        }
    }

    @Override
    protected int _getInt(int index) {
        Component c = findComponent(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getInt(index - c.offset);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (_getShort(index) & 0xffff) << 16 | _getShort(index + 2) & 0xffff;
        } else {
            return _getShort(index) & 0xFFFF | (_getShort(index + 2) & 0xFFFF) << 16;
        }
    }

    @Override
    protected long _getLong(int index) {
        Component c = findComponent(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLong(index - c.offset);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (_getInt(index) & 0xffffffffL) << 32 | _getInt(index + 4) & 0xffffffffL;
        } else {
            return _getInt(index) & 0xFFFFFFFFL | (_getInt(index + 4) & 0xFFFFFFFFL) << 32;
        }
    }

    @Override
    public CompositeByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.length);
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            i ++;
        }
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, java.nio.ByteBuffer dst) {
        int limit = dst.limit();
        int length = dst.remaining();

        checkIndex(index, length);
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        try {
            while (length > 0) {
                Component c = components.get(i);
                ByteBuffer s = c.buf;
                int adjustment = c.offset;
                int localLength = Math.min(length, s.capacity() - (index - adjustment));
                dst.limit(dst.position() + localLength);
                s.getBytes(index - adjustment, dst);
                index += localLength;
                length -= localLength;
                i ++;
            }
        } finally {
            dst.limit(limit);
        }
        return this;
    }

    @Override
    public CompositeByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            i ++;
        }
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length)
            throws IOException {
        int count = nioBufferCount();
        if (count == 1) {
            return out.write(internalNioBuffer(index, length));
        } else {
            long writtenBytes = out.write(nioBuffers(index, length));
            if (writtenBytes > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return (int) writtenBytes;
            }
        }
    }

    @Override
    public CompositeByteBuffer getBytes(int index, OutputStream out, int length) throws IOException {
        checkIndex(index, length);
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, out, localLength);
            index += localLength;
            length -= localLength;
            i ++;
        }
        return this;
    }

    @Override
    public CompositeByteBuffer setByte(int index, int value) {
        Component c = findComponent(index);
        c.buf.setByte(index - c.offset, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        setByte(index, value);
    }

    @Override
    public CompositeByteBuffer setShort(int index, int value) {
        return (CompositeByteBuffer) super.setShort(index, value);
    }

    @Override
    protected void _setShort(int index, int value) {
        Component c = findComponent(index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShort(index - c.offset, value);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            _setByte(index, (byte) (value >>> 8));
            _setByte(index + 1, (byte) value);
        } else {
            _setByte(index, (byte) value);
            _setByte(index + 1, (byte) (value >>> 8));
        }
    }

    @Override
    public CompositeByteBuffer setMedium(int index, int value) {
        return (CompositeByteBuffer) super.setMedium(index, value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        Component c = findComponent(index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMedium(index - c.offset, value);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            _setShort(index, (short) (value >> 8));
            _setByte(index + 2, (byte) value);
        } else {
            _setShort(index, (short) value);
            _setByte(index + 2, (byte) (value >>> 16));
        }
    }

    @Override
    public CompositeByteBuffer setInt(int index, int value) {
        return (CompositeByteBuffer) super.setInt(index, value);
    }

    @Override
    protected void _setInt(int index, int value) {
        Component c = findComponent(index);
        if (index + 4 <= c.endOffset) {
            c.buf.setInt(index - c.offset, value);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            _setShort(index, (short) (value >>> 16));
            _setShort(index + 2, (short) value);
        } else {
            _setShort(index, (short) value);
            _setShort(index + 2, (short) (value >>> 16));
        }
    }

    @Override
    public CompositeByteBuffer setLong(int index, long value) {
        return (CompositeByteBuffer) super.setLong(index, value);
    }

    @Override
    protected void _setLong(int index, long value) {
        Component c = findComponent(index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLong(index - c.offset, value);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            _setInt(index, (int) (value >>> 32));
            _setInt(index + 4, (int) value);
        } else {
            _setInt(index, (int) value);
            _setInt(index + 4, (int) (value >>> 32));
        }
    }

    @Override
    public CompositeByteBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.length);
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.setBytes(index - adjustment, src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
            i ++;
        }
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, java.nio.ByteBuffer src) {
        int limit = src.limit();
        int length = src.remaining();

        checkIndex(index, length);
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        try {
            while (length > 0) {
                Component c = components.get(i);
                ByteBuffer s = c.buf;
                int adjustment = c.offset;
                int localLength = Math.min(length, s.capacity() - (index - adjustment));
                src.limit(src.position() + localLength);
                s.setBytes(index - adjustment, src);
                index += localLength;
                length -= localLength;
                i ++;
            }
        } finally {
            src.limit(limit);
        }
        return this;
    }

    @Override
    public CompositeByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length) {
        checkSrcIndex(index, length, srcIndex, src.capacity());
        if (length == 0) {
            return this;
        }

        int i = toComponentIndex(index);
        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.setBytes(index - adjustment, src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
            i ++;
        }
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        checkIndex(index, length);
        if (length == 0) {
            return in.read(EmptyArrays.EMPTY_BYTES);
        }

        int i = toComponentIndex(index);
        int readBytes = 0;

        do {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            int localReadBytes = s.setBytes(index - adjustment, in, localLength);
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                } else {
                    break;
                }
            }

            if (localReadBytes == localLength) {
                index += localLength;
                length -= localLength;
                readBytes += localLength;
                i ++;
            } else {
                index += localReadBytes;
                length -= localReadBytes;
                readBytes += localReadBytes;
            }
        } while (length > 0);

        return readBytes;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        checkIndex(index, length);
        if (length == 0) {
            return in.read(EMPTY_NIO_BUFFER);
        }

        int i = toComponentIndex(index);
        int readBytes = 0;
        do {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            int localReadBytes = s.setBytes(index - adjustment, in, localLength);

            if (localReadBytes == 0) {
                break;
            }

            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                } else {
                    break;
                }
            }

            if (localReadBytes == localLength) {
                index += localLength;
                length -= localLength;
                readBytes += localLength;
                i ++;
            } else {
                index += localReadBytes;
                length -= localReadBytes;
                readBytes += localReadBytes;
            }
        } while (length > 0);

        return readBytes;
    }

    @Override
    public ByteBuffer copy(int index, int length) {
        checkIndex(index, length);
        ByteBuffer dst = allocBuffer(length);
        if (length != 0) {
            copyTo(index, length, toComponentIndex(index), dst);
        }
        return dst;
    }

    private void copyTo(int index, int length, int componentId, ByteBuffer dst) {
        int dstIndex = 0;
        int i = componentId;

        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            i ++;
        }

        dst.writerIndex(dst.capacity());
    }

    /**
     * Return the {@link ByteBuffer} on the specified index
     *
     * @param cIndex the index for which the {@link ByteBuffer} should be returned
     * @return buf the {@link ByteBuffer} on the specified index
     */
    public ByteBuffer component(int cIndex) {
        return internalComponent(cIndex).duplicate();
    }

    /**
     * Return the {@link ByteBuffer} on the specified index
     *
     * @param offset the offset for which the {@link ByteBuffer} should be returned
     * @return the {@link ByteBuffer} on the specified index
     */
    public ByteBuffer componentAtOffset(int offset) {
        return internalComponentAtOffset(offset).duplicate();
    }

    /**
     * Return the internal {@link ByteBuffer} on the specified index. Note that updating the indexes of the returned
     * buffer will lead to an undefined behavior of this buffer.
     *
     * @param cIndex the index for which the {@link ByteBuffer} should be returned
     */
    public ByteBuffer internalComponent(int cIndex) {
        checkComponentIndex(cIndex);
        return components.get(cIndex).buf;
    }

    /**
     * Return the internal {@link ByteBuffer} on the specified offset. Note that updating the indexes of the returned
     * buffer will lead to an undefined behavior of this buffer.
     *
     * @param offset the offset for which the {@link ByteBuffer} should be returned
     */
    public ByteBuffer internalComponentAtOffset(int offset) {
        return findComponent(offset).buf;
    }

    private Component findComponent(int offset) {
        checkIndex(offset);

        for (int low = 0, high = components.size(); low <= high;) {
            int mid = low + high >>> 1;
            Component c = components.get(mid);
            if (offset >= c.endOffset) {
                low = mid + 1;
            } else if (offset < c.offset) {
                high = mid - 1;
            } else {
                assert c.length != 0;
                return c;
            }
        }

        throw new Error("should not reach here");
    }

    @Override
    public int nioBufferCount() {
        switch (components.size()) {
        case 0:
            return 1;
        case 1:
            return components.get(0).buf.nioBufferCount();
        default:
            int count = 0;
            int componentsCount = components.size();
            for (int i = 0; i < componentsCount; i++) {
                Component c = components.get(i);
                count += c.buf.nioBufferCount();
            }
            return count;
        }
    }

    @Override
    public java.nio.ByteBuffer internalNioBuffer(int index, int length) {
        switch (components.size()) {
        case 0:
            return EMPTY_NIO_BUFFER;
        case 1:
            return components.get(0).buf.internalNioBuffer(index, length);
        default:
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public java.nio.ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);

        switch (components.size()) {
        case 0:
            return EMPTY_NIO_BUFFER;
        case 1:
            ByteBuffer buf = components.get(0).buf;
            if (buf.nioBufferCount() == 1) {
                return components.get(0).buf.nioBuffer(index, length);
            }
        }

        java.nio.ByteBuffer merged = java.nio.ByteBuffer.allocate(length).order(order());
        java.nio.ByteBuffer[] buffers = nioBuffers(index, length);

        for (java.nio.ByteBuffer buf: buffers) {
            merged.put(buf);
        }

        merged.flip();
        return merged;
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers(int index, int length) {
        checkIndex(index, length);
        if (length == 0) {
            return new java.nio.ByteBuffer[] { EMPTY_NIO_BUFFER };
        }

        List<java.nio.ByteBuffer> buffers = new ArrayList<java.nio.ByteBuffer>(components.size());
        int i = toComponentIndex(index);
        while (length > 0) {
            Component c = components.get(i);
            ByteBuffer s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            switch (s.nioBufferCount()) {
                case 0:
                    throw new UnsupportedOperationException();
                case 1:
                    buffers.add(s.nioBuffer(index - adjustment, localLength));
                    break;
                default:
                    Collections.addAll(buffers, s.nioBuffers(index - adjustment, localLength));
            }

            index += localLength;
            length -= localLength;
            i ++;
        }

        return buffers.toArray(new java.nio.ByteBuffer[buffers.size()]);
    }

    /**
     * Consolidate the composed {@link ByteBuffer}s
     */
    public CompositeByteBuffer consolidate() {
        ensureAccessible();
        final int numComponents = numComponents();
        if (numComponents <= 1) {
            return this;
        }

        final Component last = components.get(numComponents - 1);
        final int capacity = last.endOffset;
        final ByteBuffer consolidated = allocBuffer(capacity);

        for (int i = 0; i < numComponents; i ++) {
            Component c = components.get(i);
            ByteBuffer b = c.buf;
            consolidated.writeBytes(b);
            c.freeIfNecessary();
        }

        components.clear();
        components.add(new Component(consolidated));
        updateComponentOffsets(0);
        return this;
    }

    /**
     * Consolidate the composed {@link ByteBuffer}s
     *
     * @param cIndex the index on which to start to compose
     * @param numComponents the number of components to compose
     */
    public CompositeByteBuffer consolidate(int cIndex, int numComponents) {
        checkComponentIndex(cIndex, numComponents);
        if (numComponents <= 1) {
            return this;
        }

        final int endCIndex = cIndex + numComponents;
        final Component last = components.get(endCIndex - 1);
        final int capacity = last.endOffset - components.get(cIndex).offset;
        final ByteBuffer consolidated = allocBuffer(capacity);

        for (int i = cIndex; i < endCIndex; i ++) {
            Component c = components.get(i);
            ByteBuffer b = c.buf;
            consolidated.writeBytes(b);
            c.freeIfNecessary();
        }

        components.removeRange(cIndex + 1, endCIndex);
        components.set(cIndex, new Component(consolidated));
        updateComponentOffsets(cIndex);
        return this;
    }

    /**
     * Discard all {@link ByteBuffer}s which are read.
     */
    public CompositeByteBuffer discardReadComponents() {
        ensureAccessible();
        final int readerIndex = readerIndex();
        if (readerIndex == 0) {
            return this;
        }

        // Discard everything if (readerIndex = writerIndex = capacity).
        int writerIndex = writerIndex();
        if (readerIndex == writerIndex && writerIndex == capacity()) {
            int size = components.size();
            for (int i = 0; i < size; i++) {
                components.get(i).freeIfNecessary();
            }
            components.clear();
            setIndex(0, 0);
            adjustMarkers(readerIndex);
            return this;
        }

        // Remove read components.
        int firstComponentId = toComponentIndex(readerIndex);
        for (int i = 0; i < firstComponentId; i ++) {
            components.get(i).freeIfNecessary();
        }
        components.removeRange(0, firstComponentId);

        // Update indexes and markers.
        Component first = components.get(0);
        int offset = first.offset;
        updateComponentOffsets(0);
        setIndex(readerIndex - offset, writerIndex - offset);
        adjustMarkers(offset);
        return this;
    }

    @Override
    public CompositeByteBuffer discardReadBytes() {
        ensureAccessible();
        final int readerIndex = readerIndex();
        if (readerIndex == 0) {
            return this;
        }

        // Discard everything if (readerIndex = writerIndex = capacity).
        int writerIndex = writerIndex();
        if (readerIndex == writerIndex && writerIndex == capacity()) {
            int size = components.size();
            for (int i = 0; i < size; i++) {
                components.get(i).freeIfNecessary();
            }
            components.clear();
            setIndex(0, 0);
            adjustMarkers(readerIndex);
            return this;
        }

        // Remove read components.
        int firstComponentId = toComponentIndex(readerIndex);
        for (int i = 0; i < firstComponentId; i ++) {
            components.get(i).freeIfNecessary();
        }

        // Remove or replace the first readable component with a new slice.
        Component c = components.get(firstComponentId);
        int adjustment = readerIndex - c.offset;
        if (adjustment == c.length) {
            // new slice would be empty, so remove instead
            firstComponentId++;
        } else {
            Component newC = new Component(c.buf.slice(adjustment, c.length - adjustment));
            components.set(firstComponentId, newC);
        }

        components.removeRange(0, firstComponentId);

        // Update indexes and markers.
        updateComponentOffsets(0);
        setIndex(0, writerIndex - readerIndex);
        adjustMarkers(readerIndex);
        return this;
    }

    private ByteBuffer allocBuffer(int capacity) {
        return direct ? alloc().directBuffer(capacity) : alloc().heapBuffer(capacity);
    }

    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring(0, result.length() - 1);
        return result + ", components=" + components.size() + ')';
    }

    private static final class Component {
        final ByteBuffer buf;
        final int length;
        int offset;
        int endOffset;

        Component(ByteBuffer buf) {
            this.buf = buf;
            length = buf.readableBytes();
        }

        void freeIfNecessary() {
            buf.release(); // We should not get a NPE here. If so, it must be a bug.
        }
    }

    @Override
    public CompositeByteBuffer readerIndex(int readerIndex) {
        return (CompositeByteBuffer) super.readerIndex(readerIndex);
    }

    @Override
    public CompositeByteBuffer writerIndex(int writerIndex) {
        return (CompositeByteBuffer) super.writerIndex(writerIndex);
    }

    @Override
    public CompositeByteBuffer setIndex(int readerIndex, int writerIndex) {
        return (CompositeByteBuffer) super.setIndex(readerIndex, writerIndex);
    }

    @Override
    public CompositeByteBuffer clear() {
        return (CompositeByteBuffer) super.clear();
    }

    @Override
    public CompositeByteBuffer markReaderIndex() {
        return (CompositeByteBuffer) super.markReaderIndex();
    }

    @Override
    public CompositeByteBuffer resetReaderIndex() {
        return (CompositeByteBuffer) super.resetReaderIndex();
    }

    @Override
    public CompositeByteBuffer markWriterIndex() {
        return (CompositeByteBuffer) super.markWriterIndex();
    }

    @Override
    public CompositeByteBuffer resetWriterIndex() {
        return (CompositeByteBuffer) super.resetWriterIndex();
    }

    @Override
    public CompositeByteBuffer ensureWritable(int minWritableBytes) {
        return (CompositeByteBuffer) super.ensureWritable(minWritableBytes);
    }

    @Override
    public CompositeByteBuffer getBytes(int index, ByteBuffer dst) {
        return (CompositeByteBuffer) super.getBytes(index, dst);
    }

    @Override
    public CompositeByteBuffer getBytes(int index, ByteBuffer dst, int length) {
        return (CompositeByteBuffer) super.getBytes(index, dst, length);
    }

    @Override
    public CompositeByteBuffer getBytes(int index, byte[] dst) {
        return (CompositeByteBuffer) super.getBytes(index, dst);
    }

    @Override
    public CompositeByteBuffer setBoolean(int index, boolean value) {
        return (CompositeByteBuffer) super.setBoolean(index, value);
    }

    @Override
    public CompositeByteBuffer setChar(int index, int value) {
        return (CompositeByteBuffer) super.setChar(index, value);
    }

    @Override
    public CompositeByteBuffer setFloat(int index, float value) {
        return (CompositeByteBuffer) super.setFloat(index, value);
    }

    @Override
    public CompositeByteBuffer setDouble(int index, double value) {
        return (CompositeByteBuffer) super.setDouble(index, value);
    }

    @Override
    public CompositeByteBuffer setBytes(int index, ByteBuffer src) {
        return (CompositeByteBuffer) super.setBytes(index, src);
    }

    @Override
    public CompositeByteBuffer setBytes(int index, ByteBuffer src, int length) {
        return (CompositeByteBuffer) super.setBytes(index, src, length);
    }

    @Override
    public CompositeByteBuffer setBytes(int index, byte[] src) {
        return (CompositeByteBuffer) super.setBytes(index, src);
    }

    @Override
    public CompositeByteBuffer setZero(int index, int length) {
        return (CompositeByteBuffer) super.setZero(index, length);
    }

    @Override
    public CompositeByteBuffer readBytes(ByteBuffer dst) {
        return (CompositeByteBuffer) super.readBytes(dst);
    }

    @Override
    public CompositeByteBuffer readBytes(ByteBuffer dst, int length) {
        return (CompositeByteBuffer) super.readBytes(dst, length);
    }

    @Override
    public CompositeByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        return (CompositeByteBuffer) super.readBytes(dst, dstIndex, length);
    }

    @Override
    public CompositeByteBuffer readBytes(byte[] dst) {
        return (CompositeByteBuffer) super.readBytes(dst);
    }

    @Override
    public CompositeByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        return (CompositeByteBuffer) super.readBytes(dst, dstIndex, length);
    }

    @Override
    public CompositeByteBuffer readBytes(java.nio.ByteBuffer dst) {
        return (CompositeByteBuffer) super.readBytes(dst);
    }

    @Override
    public CompositeByteBuffer readBytes(OutputStream out, int length) throws IOException {
        return (CompositeByteBuffer) super.readBytes(out, length);
    }

    @Override
    public CompositeByteBuffer skipBytes(int length) {
        return (CompositeByteBuffer) super.skipBytes(length);
    }

    @Override
    public CompositeByteBuffer writeBoolean(boolean value) {
        return (CompositeByteBuffer) super.writeBoolean(value);
    }

    @Override
    public CompositeByteBuffer writeByte(int value) {
        return (CompositeByteBuffer) super.writeByte(value);
    }

    @Override
    public CompositeByteBuffer writeShort(int value) {
        return (CompositeByteBuffer) super.writeShort(value);
    }

    @Override
    public CompositeByteBuffer writeMedium(int value) {
        return (CompositeByteBuffer) super.writeMedium(value);
    }

    @Override
    public CompositeByteBuffer writeInt(int value) {
        return (CompositeByteBuffer) super.writeInt(value);
    }

    @Override
    public CompositeByteBuffer writeLong(long value) {
        return (CompositeByteBuffer) super.writeLong(value);
    }

    @Override
    public CompositeByteBuffer writeChar(int value) {
        return (CompositeByteBuffer) super.writeChar(value);
    }

    @Override
    public CompositeByteBuffer writeFloat(float value) {
        return (CompositeByteBuffer) super.writeFloat(value);
    }

    @Override
    public CompositeByteBuffer writeDouble(double value) {
        return (CompositeByteBuffer) super.writeDouble(value);
    }

    @Override
    public CompositeByteBuffer writeBytes(ByteBuffer src) {
        return (CompositeByteBuffer) super.writeBytes(src);
    }

    @Override
    public CompositeByteBuffer writeBytes(ByteBuffer src, int length) {
        return (CompositeByteBuffer) super.writeBytes(src, length);
    }

    @Override
    public CompositeByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length) {
        return (CompositeByteBuffer) super.writeBytes(src, srcIndex, length);
    }

    @Override
    public CompositeByteBuffer writeBytes(byte[] src) {
        return (CompositeByteBuffer) super.writeBytes(src);
    }

    @Override
    public CompositeByteBuffer writeBytes(byte[] src, int srcIndex, int length) {
        return (CompositeByteBuffer) super.writeBytes(src, srcIndex, length);
    }

    @Override
    public CompositeByteBuffer writeBytes(java.nio.ByteBuffer src) {
        return (CompositeByteBuffer) super.writeBytes(src);
    }

    @Override
    public CompositeByteBuffer writeZero(int length) {
        return (CompositeByteBuffer) super.writeZero(length);
    }

    @Override
    public CompositeByteBuffer retain(int increment) {
        return (CompositeByteBuffer) super.retain(increment);
    }

    @Override
    public CompositeByteBuffer retain() {
        return (CompositeByteBuffer) super.retain();
    }

    @Override
    public java.nio.ByteBuffer[] nioBuffers() {
        return nioBuffers(readerIndex(), readableBytes());
    }

    @Override
    public CompositeByteBuffer discardSomeReadBytes() {
        return discardReadComponents();
    }

    @Override
    protected void deallocate() {
        if (freed) {
            return;
        }

        freed = true;
        int size = components.size();
        // We're not using foreach to avoid creating an iterator.
        // see https://github.com/netty/netty/issues/2642
        for (int i = 0; i < size; i++) {
            components.get(i).freeIfNecessary();
        }
    }

    @Override
    public ByteBuffer unwrap() {
        return null;
    }

    private final class CompositeByteBufIterator implements Iterator<ByteBuffer> {
        private final int size = components.size();
        private int index;

        @Override
        public boolean hasNext() {
            return size > index;
        }

        @Override
        public ByteBuffer next() {
            if (size != components.size()) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return components.get(index++).buf;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read-Only");
        }
    }

    private static final class ComponentList extends ArrayList<Component> {

        ComponentList(int initialCapacity) {
            super(initialCapacity);
        }

        // Expose this methods so we not need to create a new subList just to remove a range of elements.
        @Override
        public void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }
}
