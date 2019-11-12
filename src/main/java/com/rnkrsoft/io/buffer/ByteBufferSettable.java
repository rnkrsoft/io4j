package com.rnkrsoft.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft.com on 2019/11/4.
 */
public interface ByteBufferSettable {
    ByteBuffer setBoolean(int index, boolean value);

    ByteBuffer setByte(int index, byte value);

    ByteBuffer setShort(int index, short value);

    ByteBuffer setShortLE(int index, int value);

    ByteBuffer setMedium(int index, int value);

    ByteBuffer setMediumLE(int index, int value);

    ByteBuffer setInt(int index, int value);

    ByteBuffer setIntLE(int index, int value);

    ByteBuffer setChar(int index, char value);

    ByteBuffer setLong(int index, long value);

    ByteBuffer setLongLE(int index, long value);

    ByteBuffer setFloat(int index, float value);

    ByteBuffer setDouble(int index, double value);

    ByteBuffer setBytes(int index, ByteBuffer src);

    ByteBuffer setBytes(int index, ByteBuffer src, int length);

    ByteBuffer setBytes(int index, ByteBuffer src, int srcIndex, int length);

    ByteBuffer setBytes(int index, byte[] src);

    ByteBuffer setBytes(int index, byte[] src, int srcIndex, int length);

    ByteBuffer setBytes(int index, java.nio.ByteBuffer src);

    int setBytes(int index, InputStream in, int length) throws IOException;

    int setBytes(int index, ScatteringByteChannel in, int length) throws IOException;

    int setBytes(int index, FileChannel in, long position, int length) throws IOException;

    ByteBuffer setZero(int index, int length);

    int setCharSequence(int index, CharSequence sequence, Charset charset);
}
