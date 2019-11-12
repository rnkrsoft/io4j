package com.rnkrsoft.io.buffer;


import com.rnkrsoft.io.buffer.process.ByteProcessor;
import com.rnkrsoft.io.buffer.process.IndexOfProcessor;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class ByteBufferFinder {
    public static int indexOf(ByteBuffer buffer, int fromIndex, int toIndex, byte value) {
        if (fromIndex <= toIndex) {
            return firstIndexOf(buffer, fromIndex, toIndex, new IndexOfProcessor(value));
        } else {
            return lastIndexOf(buffer, fromIndex, toIndex, new IndexOfProcessor(value));
        }
    }

    public static int indexOf(ByteBuffer buffer, int fromIndex, int toIndex, byte[] value) {
        final int len = value.length;
        ByteProcessor[] processors = new ByteProcessor[len];
        for (int i = 0; i < len; i++) {
            processors[i] = new IndexOfProcessor(value[i]);
        }
        return buffer.forEachByte(fromIndex, toIndex, processors);
    }

     static int firstIndexOf(ByteBuffer buffer, int fromIndex, int toIndex, ByteProcessor processor) {
        fromIndex = Math.max(fromIndex, 0);
        if (fromIndex >= toIndex || buffer.capacity() == 0) {
            return -1;
        }

        return buffer.forEachByte(fromIndex, toIndex - fromIndex, processor);
    }

     static int lastIndexOf(ByteBuffer buffer, int fromIndex, int toIndex, ByteProcessor processor) {
        fromIndex = Math.min(fromIndex, buffer.capacity());
        if (fromIndex < 0 || buffer.capacity() == 0) {
            return -1;
        }

        return buffer.forEachByteDesc(toIndex, fromIndex - toIndex, processor);
    }
}
