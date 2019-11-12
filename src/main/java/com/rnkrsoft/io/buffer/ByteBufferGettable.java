package com.rnkrsoft.io.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft.com on 2019/11/4.
 */
public interface ByteBufferGettable {
    boolean getBoolean(int index);

    byte getByte(int index);

    short getUnsignedByte(int index);

    short getShort(int index);

    short getShortLE(int index);

    int getUnsignedShort(int index);

    int getUnsignedShortLE(int index);

    int getMedium(int index);

    int getMediumLE(int index);

    int getUnsignedMedium(int index);

    int getUnsignedMediumLE(int index);

    int getInt(int index);

    int getIntLE(int index);

    long getUnsignedInt(int index);

    long getUnsignedIntLE(int index);

    long getLong(int index);

    long getLongLE(int index);

    char getChar(int index);

    float getFloat(int index);

    double getDouble(int index);

    ByteBuffer getBytes(int index, ByteBuffer dst);

    ByteBuffer getBytes(int index, ByteBuffer dst, int length);

    ByteBuffer getBytes(int index, ByteBuffer dst, int dstIndex, int length);

    ByteBuffer getBytes(int index, byte[] dst);

    ByteBuffer getBytes(int index, byte[] dst, int dstIndex, int length);

    ByteBuffer getBytes(int index, java.nio.ByteBuffer dst);

    int getBytes(int index, OutputStream out, int length) throws IOException;

    int getBytes(int index, GatheringByteChannel out, int length) throws IOException;

    int getBytes(int index, FileChannel out, long position, int length) throws IOException;

    String getString(int index, int length, Charset charset);
}
