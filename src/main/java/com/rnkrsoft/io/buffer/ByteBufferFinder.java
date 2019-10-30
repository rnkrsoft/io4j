package com.rnkrsoft.io.buffer;


import com.rnkrsoft.io.buffer.process.IndexOfProcessor;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class ByteBufferFinder {
    public static int indexOf(ByteBuffer buffer, int fromIndex, int toIndex, byte value) {
        if (fromIndex <= toIndex) {
            return firstIndexOf(buffer, fromIndex, toIndex, value);
        } else {
            return lastIndexOf(buffer, fromIndex, toIndex, value);
        }
    }

    private static int firstIndexOf(ByteBuffer buffer, int fromIndex, int toIndex, byte value) {
        fromIndex = Math.max(fromIndex, 0);
        if (fromIndex >= toIndex || buffer.capacity() == 0) {
            return -1;
        }

        return buffer.forEachByte(fromIndex, toIndex - fromIndex, new IndexOfProcessor(value));
    }

    private static int lastIndexOf(ByteBuffer buffer, int fromIndex, int toIndex, byte value) {
        fromIndex = Math.min(fromIndex, buffer.capacity());
        if (fromIndex < 0 || buffer.capacity() == 0) {
            return -1;
        }

        return buffer.forEachByteDesc(toIndex, fromIndex - toIndex, new IndexOfProcessor(value));
    }
}
