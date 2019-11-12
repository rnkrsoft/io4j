package com.rnkrsoft.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public interface ByteBufferWritable {

    ByteBuffer writeBoolean(boolean value);

    ByteBuffer writeByte(byte value);

    ByteBuffer writeShort(short value);

    ByteBuffer writeShortLE(int value);

    ByteBuffer writeMedium(int value);

    ByteBuffer writeMediumLE(int value);

    ByteBuffer writeInt(int value);

    ByteBuffer writeIntLE(int value);

    ByteBuffer writeLong(long value);

    ByteBuffer writeLongLE(long value);

    ByteBuffer writeChar(char value);

    ByteBuffer writeFloat(float value);

    ByteBuffer writeDouble(double value);

    ByteBuffer writeBytes(ByteBuffer src);

    ByteBuffer writeBytes(ByteBuffer src, int length);

    ByteBuffer writeBytes(ByteBuffer src, int srcIndex, int length);

    ByteBuffer writeBytes(byte[] src);

    ByteBuffer writeBytes(byte[] src, int srcIndex, int length);

    ByteBuffer writeBytes(java.nio.ByteBuffer src);

    ByteBuffer writeBytes(InputStream in, int length) throws IOException;

    int writeBytes(ScatteringByteChannel in, int length) throws IOException;

    int writeBytes(FileChannel in, long position, int length) throws IOException;

    ByteBuffer writeZero(int length);

    int writeString(String string, Charset charset);

    int writelnString(String string, Charset charset);

    int writeStringUTF8(String string);
    int writelnStringUTF8(String string);
}
