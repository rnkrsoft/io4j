package com.rnkrsoft.io.buffer;

import static com.rnkrsoft.io.buffer.ByteBuffers.*;

/**
 * Created by rnkrsoft.com on 2019/11/12.
 */
public class ByteBufferUtilities {
    /**
     * 根据当前容量和写入字节长度计算新的容量大小，
     *
     * @param writerIndex      当前缓冲区已写入大小
     * @param writeBytesLength 要写入长度
     * @param maxCapacity      最大容量
     * @return 计算新的容量大小
     */
    public static int calcNewCapacity(int capacity, int writerIndex, int writeBytesLength, int maxCapacity) {
        int newCapacity = capacity;
        int minNewCapacity = writerIndex + writeBytesLength;
        if (writeBytesLength > 0) {
            if (writerIndex >= DISTRIBUTION_ON_DEMAND_EXPAND_THRESHOLD) {
                newCapacity = minNewCapacity;
            } else if (writerIndex >= DOUBLE_EXPAND_THRESHOLD) {
                newCapacity = minNewCapacity + writeBytesLength;
            } else {
                newCapacity = newCapacity << 1;
            }
        }
        newCapacity = Math.min(maxCapacity, newCapacity);
        return newCapacity;
    }


    /**
     * <a href="http://unicode.org/glossary/#surrogate_code_point">Surrogate Code Point</a>.
     */
    public static boolean isSurrogate(char c) {
        return c >= '\uD800' && c <= '\uDFFF';
    }

    static int writeUtf8(ByteBuffer buffer, int writerIndex, CharSequence seq, int len) {
        return writeUtf8(buffer, writerIndex, seq, 0, len);
    }

    public static int writeUtf8(ByteBuffer buffer, int writerIndex, CharSequence cs, int start, int end) {
        int oldWriterIndex = writerIndex;
        for (int i = start; i < end; i++) {
            char c = cs.charAt(i);
            if (c < 0x80) {
                buffer.setByte(writerIndex++, (byte) c);
            } else if (c < 0x800) {
                buffer.setByte(writerIndex++, (byte) (0xc0 | (c >> 6)));
                buffer.setByte(writerIndex++, (byte) (0x80 | (c & 0x3f)));
            } else if (isSurrogate(c)) {
//                    if (!Character.isHighSurrogate(c)) {
//                        buffer.setByte(writerIndex++, WRITE_UTF_UNKNOWN);
//                        continue;
//                    }
//                    // Surrogate Pair consumes 2 characters.
//                    if (++i == end) {
//                        buffer.setByte(writerIndex++, WRITE_UTF_UNKNOWN);
//                        break;
//                    }
//                    // Extra method to allow inlining the rest of writeUtf8 which is the most likely code path.
//                    writerIndex = writeUtf8Surrogate(buffer, writerIndex, c, seq.charAt(i));
            } else {
                buffer.setByte(writerIndex++, (byte) (0xe0 | (c >> 12)));
                buffer.setByte(writerIndex++, (byte) (0x80 | ((c >> 6) & 0x3f)));
                buffer.setByte(writerIndex++, (byte) (0x80 | (c & 0x3f)));
            }
        }
        return writerIndex - oldWriterIndex;
    }
}
