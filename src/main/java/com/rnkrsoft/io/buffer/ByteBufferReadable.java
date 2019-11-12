package com.rnkrsoft.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public interface ByteBufferReadable {

    boolean readBoolean();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    short readShortLE();

    int readUnsignedShort();

    int readUnsignedShortLE();

    int readMedium();

    int readMediumLE();

    int readUnsignedMedium();

    int readUnsignedMediumLE();

    int readInt();

    int readIntLE();

    long readUnsignedInt();

    long readUnsignedIntLE();

    long readLong();

    long readLongLE();

    char readChar();

    float readFloat();

    double readDouble();

    ByteBuffer readBytes(int length);

    ByteBuffer readSlice(int length);

    ByteBuffer readRetainedSlice(int length);

    ByteBuffer readBytes(ByteBuffer dst);

    ByteBuffer readBytes(ByteBuffer dst, int length);

    ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length);

    ByteBuffer readBytes(byte[] dst);

    ByteBuffer readBytes(byte[] dst, int dstIndex, int length);

    ByteBuffer readBytes(java.nio.ByteBuffer dst);

    ByteBuffer readBytes(OutputStream out, int length) throws IOException;

    int readBytes(GatheringByteChannel out, int length) throws IOException;

    String  readString(int length, Charset charset);

    String readStringUTF8(int length);

    int readBytes(FileChannel out, long position, int length) throws IOException;

}
