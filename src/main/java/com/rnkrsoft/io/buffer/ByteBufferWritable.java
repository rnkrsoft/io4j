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
    ByteBuffer setBoolean(int index, boolean value);

    ByteBuffer setByte(int index, int value);

    ByteBuffer setShort(int index, int value);

    ByteBuffer setShortLE(int index, int value);

    ByteBuffer setMedium(int index, int value);

    ByteBuffer setMediumLE(int index, int value);

    ByteBuffer setInt(int index, int value);

    ByteBuffer setIntLE(int index, int value);

    ByteBuffer setLong(int index, long value);

    ByteBuffer setLongLE(int index, long value);

    ByteBuffer setChar(int index, int value);

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


    ByteBuffer writeBoolean(boolean value);

    ByteBuffer writeByte(int value);

    ByteBuffer writeShort(int value);

    ByteBuffer writeShortLE(int value);

    ByteBuffer writeMedium(int value);

    ByteBuffer writeMediumLE(int value);

    ByteBuffer writeInt(int value);

    ByteBuffer writeIntLE(int value);

    ByteBuffer writeLong(long value);

    ByteBuffer writeLongLE(long value);

    ByteBuffer writeChar(int value);

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
