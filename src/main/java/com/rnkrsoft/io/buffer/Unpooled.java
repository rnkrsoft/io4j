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

import com.rnkrsoft.io.buffer.util.internal.PlatformDependent;

import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Creates a new {@link ByteBuffer} by allocating new space or by wrapping
 * or copying existing byte arrays, byte buffers and a string.
 *
 * <h3>Use static import</h3>
 * This classes is intended to be used with Java 5 static import statement:
 *
 * <pre>
 * import static com.rnkrsoft.io.buffer.{@link Unpooled}.*;
 *
 * {@link ByteBuffer} heapBuffer    = buffer(128);
 * {@link ByteBuffer} directBuffer  = directBuffer(256);
 * {@link ByteBuffer} wrappedBuffer = wrappedBuffer(new byte[128], new byte[256]);
 * {@link ByteBuffer} copiedBuffer  = copiedBuffer({@link java.nio.ByteBuffer}.allocate(128));
 * </pre>
 *
 * <h3>Allocating a new buffer</h3>
 *
 * Three buffer types are provided out of the box.
 *
 * <ul>
 * <li>{@link #buffer(int)} allocates a new fixed-capacity heap buffer.</li>
 * <li>{@link #directBuffer(int)} allocates a new fixed-capacity direct buffer.</li>
 * </ul>
 *
 * <h3>Creating a wrapped buffer</h3>
 *
 * Wrapped buffer is a buffer which is a view of one or more existing
 * byte arrays and byte buffers.  Any changes in the content of the original
 * array or buffer will be visible in the wrapped buffer.  Various wrapper
 * methods are provided and their name is all {@code wrappedBuffer()}.
 * You might want to take a look at the methods that accept varargs closely if
 * you want to create a buffer which is composed of more than one array to
 * reduce the number of memory copy.
 *
 * <h3>Creating a copied buffer</h3>
 *
 * Copied buffer is a deep copy of one or more existing byte arrays, byte
 * buffers or a string.  Unlike a wrapped buffer, there's no shared data
 * between the original data and the copied buffer.  Various copy methods are
 * provided and their name is all {@code copiedBuffer()}.  It is also convenient
 * to use this operation to merge multiple buffers into one buffer.
 */
public final class Unpooled {

    private static final ByteBufferAllocator ALLOC = UnpooledByteBufferAllocator.DEFAULT;

    /**
     * Big endian byte order.
     */
    public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;

    /**
     * Little endian byte order.
     */
    public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;

    /**
     * A buffer whose capacity is {@code 0}.
     */
    public static final ByteBuffer EMPTY_BUFFER = ALLOC.buffer(0, 0);

    static {
        assert EMPTY_BUFFER instanceof EmptyByteBuffer : "EMPTY_BUFFER must be an EmptyByteBuffer.";
    }

    /**
     * Creates a new big-endian Java heap buffer with reasonably small initial capacity, which
     * expands its capacity boundlessly on demand.
     */
    public static ByteBuffer buffer() {
        return ALLOC.heapBuffer();
    }

    /**
     * Creates a new big-endian direct buffer with reasonably small initial capacity, which
     * expands its capacity boundlessly on demand.
     */
    public static ByteBuffer directBuffer() {
        return ALLOC.directBuffer();
    }

    /**
     * Creates a new big-endian Java heap buffer with the specified {@code capacity}, which
     * expands its capacity boundlessly on demand.  The new buffer's {@code readerIndex} and
     * {@code writerIndex} are {@code 0}.
     */
    public static ByteBuffer buffer(int initialCapacity) {
        return ALLOC.heapBuffer(initialCapacity);
    }

    /**
     * Creates a new big-endian direct buffer with the specified {@code capacity}, which
     * expands its capacity boundlessly on demand.  The new buffer's {@code readerIndex} and
     * {@code writerIndex} are {@code 0}.
     */
    public static ByteBuffer directBuffer(int initialCapacity) {
        return ALLOC.directBuffer(initialCapacity);
    }

    /**
     * Creates a new big-endian Java heap buffer with the specified
     * {@code initialCapacity}, that may grow up to {@code maxCapacity}
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0}.
     */
    public static ByteBuffer buffer(int initialCapacity, int maxCapacity) {
        return ALLOC.heapBuffer(initialCapacity, maxCapacity);
    }

    /**
     * Creates a new big-endian direct buffer with the specified
     * {@code initialCapacity}, that may grow up to {@code maxCapacity}.
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0}.
     */
    public static ByteBuffer directBuffer(int initialCapacity, int maxCapacity) {
        return ALLOC.directBuffer(initialCapacity, maxCapacity);
    }

    /**
     * Creates a new big-endian buffer which wraps the specified {@code array}.
     * A modification on the specified array's content will be visible to the
     * returned buffer.
     */
    public static ByteBuffer wrappedBuffer(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BUFFER;
        }
        return new UnpooledHeapByteBuffer(ALLOC, array, array.length);
    }

    /**
     * Creates a new big-endian buffer which wraps the sub-region of the
     * specified {@code array}.  A modification on the specified array's
     * content will be visible to the returned buffer.
     */
    public static ByteBuffer wrappedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }

        if (offset == 0 && length == array.length) {
            return wrappedBuffer(array);
        }

        return wrappedBuffer(array).slice(offset, length);
    }

    /**
     * Creates a new buffer which wraps the specified NIO buffer's current
     * slice.  A modification on the specified buffer's content will be
     * visible to the returned buffer.
     */
    public static ByteBuffer wrappedBuffer(java.nio.ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return EMPTY_BUFFER;
        }
        if (!buffer.isDirect() && buffer.hasArray()) {
            return wrappedBuffer(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining()).order(buffer.order());
        } else if (PlatformDependent.hasUnsafe()) {
            if (buffer.isReadOnly()) {
                if (buffer.isDirect()) {
                    return new ReadOnlyUnsafeDirectByteBuffer(ALLOC, buffer);
                } else {
                    return new ReadOnlyByteBufferBuffer(ALLOC, buffer);
                }
            } else {
                return new UnpooledUnsafeDirectByteBuffer(ALLOC, buffer, buffer.remaining());
            }
        } else {
            if (buffer.isReadOnly()) {
                return new ReadOnlyByteBufferBuffer(ALLOC, buffer);
            }  else {
                return new UnpooledDirectByteBuffer(ALLOC, buffer, buffer.remaining());
            }
        }
    }

    /**
     * Creates a new buffer which wraps the specified memory address. If {@code doFree} is true the
     * memoryAddress will automatically be freed once the reference count of the {@link ByteBuffer} reaches {@code 0}.
     */
    public static ByteBuffer wrappedBuffer(long memoryAddress, int size, boolean doFree) {
        return new WrappedUnpooledUnsafeDirectByteBuffer(ALLOC, memoryAddress, size, doFree);
    }

    /**
     * Creates a new buffer which wraps the specified buffer's readable bytes.
     * A modification on the specified buffer's content will be visible to the
     * returned buffer.
     * @param buffer The buffer to wrap. Reference count ownership of this variable is transfered to this method.
     * @return The readable portion of the {@code buffer}, or an empty buffer if there is no readable portion.
     * The caller is responsible for releasing this buffer.
     */
    public static ByteBuffer wrappedBuffer(ByteBuffer buffer) {
        if (buffer.isReadable()) {
            return buffer.slice();
        } else {
            buffer.release();
            return EMPTY_BUFFER;
        }
    }

    /**
     * Creates a new big-endian composite buffer which wraps the specified
     * arrays without copying them.  A modification on the specified arrays'
     * content will be visible to the returned buffer.
     */
    public static ByteBuffer wrappedBuffer(byte[]... arrays) {
        return wrappedBuffer(AbstractByteBufferAllocator.DEFAULT_MAX_COMPONENTS, arrays);
    }

    /**
     * Creates a new big-endian composite buffer which wraps the readable bytes of the
     * specified buffers without copying them.  A modification on the content
     * of the specified buffers will be visible to the returned buffer.
     * @param buffers The buffers to wrap. Reference count ownership of all variables is transfered to this method.
     * @return The readable portion of the {@code buffers}. The caller is responsible for releasing this buffer.
     */
    public static ByteBuffer wrappedBuffer(ByteBuffer... buffers) {
        return wrappedBuffer(AbstractByteBufferAllocator.DEFAULT_MAX_COMPONENTS, buffers);
    }

    /**
     * Creates a new big-endian composite buffer which wraps the slices of the specified
     * NIO buffers without copying them.  A modification on the content of the
     * specified buffers will be visible to the returned buffer.
     */
    public static ByteBuffer wrappedBuffer(java.nio.ByteBuffer... buffers) {
        return wrappedBuffer(AbstractByteBufferAllocator.DEFAULT_MAX_COMPONENTS, buffers);
    }

    /**
     * Creates a new big-endian composite buffer which wraps the specified
     * arrays without copying them.  A modification on the specified arrays'
     * content will be visible to the returned buffer.
     */
    public static ByteBuffer wrappedBuffer(int maxNumComponents, byte[]... arrays) {
        switch (arrays.length) {
        case 0:
            break;
        case 1:
            if (arrays[0].length != 0) {
                return wrappedBuffer(arrays[0]);
            }
            break;
        default:
            // Get the list of the component, while guessing the byte order.
            final List<ByteBuffer> components = new ArrayList<ByteBuffer>(arrays.length);
            for (byte[] a: arrays) {
                if (a == null) {
                    break;
                }
                if (a.length > 0) {
                    components.add(wrappedBuffer(a));
                }
            }

            if (!components.isEmpty()) {
                return new CompositeByteBuffer(ALLOC, false, maxNumComponents, components);
            }
        }

        return EMPTY_BUFFER;
    }

    /**
     * Creates a new big-endian composite buffer which wraps the readable bytes of the
     * specified buffers without copying them.  A modification on the content
     * of the specified buffers will be visible to the returned buffer.
     * @param maxNumComponents Advisement as to how many independent buffers are allowed to exist before
     * consolidation occurs.
     * @param buffers The buffers to wrap. Reference count ownership of all variables is transfered to this method.
     * @return The readable portion of the {@code buffers}. The caller is responsible for releasing this buffer.
     */
    public static ByteBuffer wrappedBuffer(int maxNumComponents, ByteBuffer... buffers) {
        switch (buffers.length) {
        case 0:
            break;
        case 1:
            ByteBuffer buffer = buffers[0];
            if (buffer.isReadable()) {
                return wrappedBuffer(buffer.order(BIG_ENDIAN));
            } else {
                buffer.release();
            }
            break;
        default:
            for (int i = 0; i < buffers.length; i++) {
                ByteBuffer buf = buffers[i];
                if (buf.isReadable()) {
                    return new CompositeByteBuffer(ALLOC, false, maxNumComponents, buffers, i, buffers.length);
                }
                buf.release();
            }
            break;
        }
        return EMPTY_BUFFER;
    }

    /**
     * Creates a new big-endian composite buffer which wraps the slices of the specified
     * NIO buffers without copying them.  A modification on the content of the
     * specified buffers will be visible to the returned buffer.
     */
    public static ByteBuffer wrappedBuffer(int maxNumComponents, java.nio.ByteBuffer... buffers) {
        switch (buffers.length) {
        case 0:
            break;
        case 1:
            if (buffers[0].hasRemaining()) {
                return wrappedBuffer(buffers[0].order(BIG_ENDIAN));
            }
            break;
        default:
            // Get the list of the component, while guessing the byte order.
            final List<ByteBuffer> components = new ArrayList<ByteBuffer>(buffers.length);
            for (java.nio.ByteBuffer b: buffers) {
                if (b == null) {
                    break;
                }
                if (b.remaining() > 0) {
                    components.add(wrappedBuffer(b.order(BIG_ENDIAN)));
                }
            }

            if (!components.isEmpty()) {
                return new CompositeByteBuffer(ALLOC, false, maxNumComponents, components);
            }
        }

        return EMPTY_BUFFER;
    }

    /**
     * Returns a new big-endian composite buffer with no components.
     */
    public static CompositeByteBuffer compositeBuffer() {
        return compositeBuffer(AbstractByteBufferAllocator.DEFAULT_MAX_COMPONENTS);
    }

    /**
     * Returns a new big-endian composite buffer with no components.
     */
    public static CompositeByteBuffer compositeBuffer(int maxNumComponents) {
        return new CompositeByteBuffer(ALLOC, false, maxNumComponents);
    }

    /**
     * Creates a new big-endian buffer whose content is a copy of the
     * specified {@code array}.  The new buffer's {@code readerIndex} and
     * {@code writerIndex} are {@code 0} and {@code array.length} respectively.
     */
    public static ByteBuffer copiedBuffer(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BUFFER;
        }
        return wrappedBuffer(array.clone());
    }

    /**
     * Creates a new big-endian buffer whose content is a copy of the
     * specified {@code array}'s sub-region.  The new buffer's
     * {@code readerIndex} and {@code writerIndex} are {@code 0} and
     * the specified {@code length} respectively.
     */
    public static ByteBuffer copiedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] copy = new byte[length];
        System.arraycopy(array, offset, copy, 0, length);
        return wrappedBuffer(copy);
    }

    /**
     * Creates a new buffer whose content is a copy of the specified
     * {@code buffer}'s current slice.  The new buffer's {@code readerIndex}
     * and {@code writerIndex} are {@code 0} and {@code buffer.remaining}
     * respectively.
     */
    public static ByteBuffer copiedBuffer(java.nio.ByteBuffer buffer) {
        int length = buffer.remaining();
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        byte[] copy = new byte[length];
        // Duplicate the buffer so we not adjust the position during our get operation.
        // See https://github.com/netty/netty/issues/3896
        java.nio.ByteBuffer duplicate = buffer.duplicate();
        duplicate.get(copy);
        return wrappedBuffer(copy).order(duplicate.order());
    }

    /**
     * Creates a new buffer whose content is a copy of the specified
     * {@code buffer}'s readable bytes.  The new buffer's {@code readerIndex}
     * and {@code writerIndex} are {@code 0} and {@code buffer.readableBytes}
     * respectively.
     */
    public static ByteBuffer copiedBuffer(ByteBuffer buffer) {
        int readable = buffer.readableBytesLength();
        if (readable > 0) {
            ByteBuffer copy = buffer(readable);
            copy.writeBytes(buffer, buffer.readerIndex(), readable);
            return copy;
        } else {
            return EMPTY_BUFFER;
        }
    }

    /**
     * Creates a new big-endian buffer whose content is a merged copy of
     * the specified {@code arrays}.  The new buffer's {@code readerIndex}
     * and {@code writerIndex} are {@code 0} and the sum of all arrays'
     * {@code length} respectively.
     */
    public static ByteBuffer copiedBuffer(byte[]... arrays) {
        switch (arrays.length) {
        case 0:
            return EMPTY_BUFFER;
        case 1:
            if (arrays[0].length == 0) {
                return EMPTY_BUFFER;
            } else {
                return copiedBuffer(arrays[0]);
            }
        }

        // Merge the specified arrays into one array.
        int length = 0;
        for (byte[] a: arrays) {
            if (Integer.MAX_VALUE - length < a.length) {
                throw new IllegalArgumentException(
                        "The total length of the specified arrays is too big.");
            }
            length += a.length;
        }

        if (length == 0) {
            return EMPTY_BUFFER;
        }

        byte[] mergedArray = new byte[length];
        for (int i = 0, j = 0; i < arrays.length; i ++) {
            byte[] a = arrays[i];
            System.arraycopy(a, 0, mergedArray, j, a.length);
            j += a.length;
        }

        return wrappedBuffer(mergedArray);
    }

    /**
     * Creates a new buffer whose content is a merged copy of the specified
     * {@code buffers}' readable bytes.  The new buffer's {@code readerIndex}
     * and {@code writerIndex} are {@code 0} and the sum of all buffers'
     * {@code readableBytes} respectively.
     *
     * @throws IllegalArgumentException
     *         if the specified buffers' endianness are different from each
     *         other
     */
    public static ByteBuffer copiedBuffer(ByteBuffer... buffers) {
        switch (buffers.length) {
        case 0:
            return EMPTY_BUFFER;
        case 1:
            return copiedBuffer(buffers[0]);
        }

        // Merge the specified buffers into one buffer.
        ByteOrder order = null;
        int length = 0;
        for (ByteBuffer b: buffers) {
            int bLen = b.readableBytesLength();
            if (bLen <= 0) {
                continue;
            }
            if (Integer.MAX_VALUE - length < bLen) {
                throw new IllegalArgumentException(
                        "The total length of the specified buffers is too big.");
            }
            length += bLen;
            if (order != null) {
                if (!order.equals(b.order())) {
                    throw new IllegalArgumentException("inconsistent byte order");
                }
            } else {
                order = b.order();
            }
        }

        if (length == 0) {
            return EMPTY_BUFFER;
        }

        byte[] mergedArray = new byte[length];
        for (int i = 0, j = 0; i < buffers.length; i ++) {
            ByteBuffer b = buffers[i];
            int bLen = b.readableBytesLength();
            b.getBytes(b.readerIndex(), mergedArray, j, bLen);
            j += bLen;
        }

        return wrappedBuffer(mergedArray).order(order);
    }

    /**
     * Creates a new buffer whose content is a merged copy of the specified
     * {@code buffers}' slices.  The new buffer's {@code readerIndex} and
     * {@code writerIndex} are {@code 0} and the sum of all buffers'
     * {@code remaining} respectively.
     *
     * @throws IllegalArgumentException
     *         if the specified buffers' endianness are different from each
     *         other
     */
    public static ByteBuffer copiedBuffer(java.nio.ByteBuffer... buffers) {
        switch (buffers.length) {
        case 0:
            return EMPTY_BUFFER;
        case 1:
            return copiedBuffer(buffers[0]);
        }

        // Merge the specified buffers into one buffer.
        ByteOrder order = null;
        int length = 0;
        for (java.nio.ByteBuffer b: buffers) {
            int bLen = b.remaining();
            if (bLen <= 0) {
                continue;
            }
            if (Integer.MAX_VALUE - length < bLen) {
                throw new IllegalArgumentException(
                        "The total length of the specified buffers is too big.");
            }
            length += bLen;
            if (order != null) {
                if (!order.equals(b.order())) {
                    throw new IllegalArgumentException("inconsistent byte order");
                }
            } else {
                order = b.order();
            }
        }

        if (length == 0) {
            return EMPTY_BUFFER;
        }

        byte[] mergedArray = new byte[length];
        for (int i = 0, j = 0; i < buffers.length; i ++) {
            // Duplicate the buffer so we not adjust the position during our get operation.
            // See https://github.com/netty/netty/issues/3896
            java.nio.ByteBuffer b = buffers[i].duplicate();
            int bLen = b.remaining();
            b.get(mergedArray, j, bLen);
            j += bLen;
        }

        return wrappedBuffer(mergedArray).order(order);
    }

    /**
     * Creates a new big-endian buffer whose content is the specified
     * {@code string} encoded in the specified {@code charset}.
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0} and the length of the encoded string respectively.
     */
    public static ByteBuffer copiedBuffer(CharSequence string, Charset charset) {
        if (string == null) {
            throw new NullPointerException("string");
        }

        if (string instanceof CharBuffer) {
            return copiedBuffer((CharBuffer) string, charset);
        }

        return copiedBuffer(CharBuffer.wrap(string), charset);
    }

    /**
     * Creates a new big-endian buffer whose content is a subregion of
     * the specified {@code string} encoded in the specified {@code charset}.
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0} and the length of the encoded string respectively.
     */
    public static ByteBuffer copiedBuffer(
            CharSequence string, int offset, int length, Charset charset) {
        if (string == null) {
            throw new NullPointerException("string");
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }

        if (string instanceof CharBuffer) {
            CharBuffer buf = (CharBuffer) string;
            if (buf.hasArray()) {
                return copiedBuffer(
                        buf.array(),
                        buf.arrayOffset() + buf.position() + offset,
                        length, charset);
            }

            buf = buf.slice();
            buf.limit(length);
            buf.position(offset);
            return copiedBuffer(buf, charset);
        }

        return copiedBuffer(CharBuffer.wrap(string, offset, offset + length), charset);
    }

    /**
     * Creates a new big-endian buffer whose content is the specified
     * {@code array} encoded in the specified {@code charset}.
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0} and the length of the encoded string respectively.
     */
    public static ByteBuffer copiedBuffer(char[] array, Charset charset) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        return copiedBuffer(array, 0, array.length, charset);
    }

    /**
     * Creates a new big-endian buffer whose content is a subregion of
     * the specified {@code array} encoded in the specified {@code charset}.
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0} and the length of the encoded string respectively.
     */
    public static ByteBuffer copiedBuffer(char[] array, int offset, int length, Charset charset) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        if (length == 0) {
            return EMPTY_BUFFER;
        }
        return copiedBuffer(CharBuffer.wrap(array, offset, length), charset);
    }

    private static ByteBuffer copiedBuffer(CharBuffer buffer, Charset charset) {
        return ByteBufferUtil.encodeString0(ALLOC, true, buffer, charset);
    }

    /**
     * Creates a read-only buffer which disallows any modification operations
     * on the specified {@code buffer}.  The new buffer has the same
     * {@code readerIndex} and {@code writerIndex} with the specified
     * {@code buffer}.
     */
    public static ByteBuffer unmodifiableBuffer(ByteBuffer buffer) {
        ByteOrder endianness = buffer.order();
        if (endianness == BIG_ENDIAN) {
            return new ReadOnlyByteBuffer(buffer);
        }

        return new ReadOnlyByteBuffer(buffer.order(BIG_ENDIAN)).order(LITTLE_ENDIAN);
    }

    /**
     * Creates a new 4-byte big-endian buffer that holds the specified 32-bit integer.
     */
    public static ByteBuffer copyInt(int value) {
        ByteBuffer buf = buffer(4);
        buf.writeInt(value);
        return buf;
    }

    /**
     * Create a big-endian buffer that holds a sequence of the specified 32-bit integers.
     */
    public static ByteBuffer copyInt(int... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 4);
        for (int v: values) {
            buffer.writeInt(v);
        }
        return buffer;
    }

    /**
     * Creates a new 2-byte big-endian buffer that holds the specified 16-bit integer.
     */
    public static ByteBuffer copyShort(int value) {
        ByteBuffer buf = buffer(2);
        buf.writeShort(value);
        return buf;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified 16-bit integers.
     */
    public static ByteBuffer copyShort(short... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 2);
        for (int v: values) {
            buffer.writeShort(v);
        }
        return buffer;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified 16-bit integers.
     */
    public static ByteBuffer copyShort(int... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 2);
        for (int v: values) {
            buffer.writeShort(v);
        }
        return buffer;
    }

    /**
     * Creates a new 3-byte big-endian buffer that holds the specified 24-bit integer.
     */
    public static ByteBuffer copyMedium(int value) {
        ByteBuffer buf = buffer(3);
        buf.writeMedium(value);
        return buf;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified 24-bit integers.
     */
    public static ByteBuffer copyMedium(int... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 3);
        for (int v: values) {
            buffer.writeMedium(v);
        }
        return buffer;
    }

    /**
     * Creates a new 8-byte big-endian buffer that holds the specified 64-bit integer.
     */
    public static ByteBuffer copyLong(long value) {
        ByteBuffer buf = buffer(8);
        buf.writeLong(value);
        return buf;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified 64-bit integers.
     */
    public static ByteBuffer copyLong(long... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 8);
        for (long v: values) {
            buffer.writeLong(v);
        }
        return buffer;
    }

    /**
     * Creates a new single-byte big-endian buffer that holds the specified boolean value.
     */
    public static ByteBuffer copyBoolean(boolean value) {
        ByteBuffer buf = buffer(1);
        buf.writeBoolean(value);
        return buf;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified boolean values.
     */
    public static ByteBuffer copyBoolean(boolean... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length);
        for (boolean v: values) {
            buffer.writeBoolean(v);
        }
        return buffer;
    }

    /**
     * Creates a new 4-byte big-endian buffer that holds the specified 32-bit floating point number.
     */
    public static ByteBuffer copyFloat(float value) {
        ByteBuffer buf = buffer(4);
        buf.writeFloat(value);
        return buf;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified 32-bit floating point numbers.
     */
    public static ByteBuffer copyFloat(float... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 4);
        for (float v: values) {
            buffer.writeFloat(v);
        }
        return buffer;
    }

    /**
     * Creates a new 8-byte big-endian buffer that holds the specified 64-bit floating point number.
     */
    public static ByteBuffer copyDouble(double value) {
        ByteBuffer buf = buffer(8);
        buf.writeDouble(value);
        return buf;
    }

    /**
     * Create a new big-endian buffer that holds a sequence of the specified 64-bit floating point numbers.
     */
    public static ByteBuffer copyDouble(double... values) {
        if (values == null || values.length == 0) {
            return EMPTY_BUFFER;
        }
        ByteBuffer buffer = buffer(values.length * 8);
        for (double v: values) {
            buffer.writeDouble(v);
        }
        return buffer;
    }

    /**
     * Return a unreleasable view on the given {@link ByteBuffer} which will just ignore release and retain calls.
     */
    public static ByteBuffer unreleasableBuffer(ByteBuffer buf) {
        return new UnreleasableByteBuffer(buf);
    }

    /**
     * Wrap the given {@link ByteBuffer}s in an unmodifiable {@link ByteBuffer}. Be aware the returned {@link ByteBuffer} will
     * not try to slice the given {@link ByteBuffer}s to reduce GC-Pressure.
     */
    public static ByteBuffer unmodifiableBuffer(ByteBuffer... buffers) {
        return new FixedCompositeByteBuffer(ALLOC, buffers);
    }

    private Unpooled() {
        // Unused
    }
}
