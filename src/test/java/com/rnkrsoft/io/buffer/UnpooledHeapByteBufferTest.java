package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.util.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft.com on 2019/10/31.
 */
public class UnpooledHeapByteBufferTest {

    @Test
    public void testWriteStringUTF8() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 5, 40);
        buffer.writeStringUTF8("1234567890");
        Assert.assertEquals("31323334353637383930000000000000000000000000000000000000000000000000000000000000", Hex.toHexString(buffer.array()));
        buffer.writeStringUTF8("123456789");
        Assert.assertEquals("31323334353637383930313233343536373839000000000000000000000000000000000000000000", Hex.toHexString(buffer.array()));
    }

    @Test
    public void testReadStringUTF8() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeStringUTF8("1234567890");
        String str = buffer.readStringUTF8(10);
        Assert.assertEquals("1234567890", str);
    }

    @Test
    public void testInternalNioBuffer() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        buffer.writeStringUTF8("1234567890");
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteBoolean() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeBoolean(true);
        buffer.writeBoolean(true);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        buffer.writeBoolean(false);
        buffer.writeBoolean(true);
        System.out.println(buffer.getBoolean(0));
        System.out.println(buffer.getBoolean(1));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteByte() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeByte((byte) 0x1);
        buffer.writeByte((byte) 0x2);
        buffer.writeByte((byte) 0x1F);
        buffer.writeByte((byte) 0x7F);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }


    @Test
    public void testWriteShort() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeShort((short) 0x1);
        buffer.writeShort((short) 0x2);
        buffer.writeShort((short) 0x1F);
        buffer.writeShort(Short.MIN_VALUE);
        buffer.writeShort(Short.MAX_VALUE);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteMedium() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeMedium(0x1);
        buffer.writeMedium(0x2);
        buffer.writeMedium(0x1F);
        buffer.writeMedium(0xFFFFFF);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteInt() throws Exception {
        final UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 16, 40);
        buffer.writeInt(0x1);
        buffer.writeInt(0x2);
        buffer.writeInt(Integer.MIN_VALUE);
        buffer.writeInt(Integer.MAX_VALUE);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }
    @Test
    public void testWriteLong() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeLong(0x1);
        buffer.writeLong(0x2);
        buffer.writeLong(Long.MIN_VALUE);
        buffer.writeLong(Long.MAX_VALUE);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteChar() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeChar('0');
        buffer.writeChar('A');
        buffer.writeChar('a');
        buffer.writeChar('Z');
        buffer.writeChar('z');
        buffer.writeChar('我');
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        System.out.println(Hex.toDisplayString("0AaZz".getBytes()));
    }

    @Test
    public void testWriteFloat() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeFloat(123.234F);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        System.out.println(buffer.getFloat(0));
    }

    @Test
    public void testWriteDouble() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 10, 40);
        buffer.writeDouble(123.234D);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        System.out.println(buffer.getDouble(0));
    }

    @Test
    public void testGetByte0() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, Byte.MAX_VALUE, Byte.MAX_VALUE);
        for (byte i = 0; i < Byte.MAX_VALUE; i++) {
            buffer.setByte(i, i);
            Assert.assertEquals(i, buffer.getByte(i));
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testGetShort0() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, Short.MAX_VALUE, Short.MAX_VALUE);
        for (short i = 0; i < Short.MAX_VALUE - 1; i++) {
            buffer.setShort(i, i);
            Assert.assertEquals(i, buffer.getShort(i));
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testGetInt0() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 1024, 1024);
        for (short i = 0; i < 256; i++) {
            buffer.setInt(i * 4, i);
            Assert.assertEquals(i, buffer.getInt(i * 4));
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testGetLong0() throws Exception {

    }

    @Test
    public void testWriteBoolean1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 2, 2);
        buffer.writeBoolean(false);
        Assert.assertEquals(false, buffer.readBoolean());
        buffer.writeBoolean(true);
        Assert.assertEquals(true, buffer.readBoolean());
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteByte1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, Byte.MAX_VALUE, Byte.MAX_VALUE);
        for (byte i = 0; i < Byte.MAX_VALUE; i++) {
            buffer.writeByte(i);
            Assert.assertEquals(i, buffer.readByte());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        Assert.assertEquals(Byte.MAX_VALUE, buffer.readerIndex());
        Assert.assertEquals(Byte.MAX_VALUE, buffer.writerIndex());
    }

    @Test
    public void testWriteShortLE1() throws Exception {

    }

    @Test
    public void testWriteShort1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 2 * Short.MAX_VALUE, 2 * Short.MAX_VALUE);
        for (short i = 0; i < Short.MAX_VALUE; i++) {
            buffer.writeShort(i);
            Assert.assertEquals(i, buffer.readShort());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        Assert.assertEquals(2 * Short.MAX_VALUE, buffer.readerIndex());
        Assert.assertEquals(2 * Short.MAX_VALUE, buffer.writerIndex());
    }


    @Test
    public void testWriteInt1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 1024, 1024);
        for (int i = 0; i < 256; i++) {
            buffer.writeInt(i);
            Assert.assertEquals(i, buffer.readInt());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        Assert.assertEquals(1024, buffer.readerIndex());
        Assert.assertEquals(1024, buffer.writerIndex());
    }


    @Test
    public void testWriteLong1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 2048, 2048);
        for (long i = 0; i < 256; i++) {
            buffer.writeLong(i);
            Assert.assertEquals(i, buffer.readLong());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        Assert.assertEquals(2048, buffer.readerIndex());
        Assert.assertEquals(2048, buffer.writerIndex());
    }

    @Test
    public void testWriteChar1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 2048, 2048);
        for (char i = 'a'; i < 'z'; i++) {
            buffer.writeChar(i);
            Assert.assertEquals(i, buffer.readChar());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteStringUTF81() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 48, 48);
        buffer.writeStringUTF8("0123456789ABCDEF");
        Assert.assertEquals("0123456789ABCDEF", buffer.readStringUTF8(16));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testReadString() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT,0, 48);
        buffer.writeBytes(Hex.fromHexString("30313233343536373839414243444546"));
        Assert.assertEquals("0123456789ABCDEF", buffer.readString(16, Charset.forName("UTF-8")));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testReadStringUTF81() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 0, 48);
        buffer.writeBytes(Hex.fromHexString("30313233343536373839414243444546"));
        Assert.assertEquals("0123456789ABCDEF", buffer.readStringUTF8(16));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testForEachByte() throws Exception {
//        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
//        Assert.assertEquals(-1, buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x35}));
//        Assert.assertEquals(-1, buffer.indexOf(0, 5, new byte[]{(byte) 0x35}));
//        Assert.assertEquals(4, buffer.indexOf(0, 5, new byte[]{(byte) 0x34}));
//        Assert.assertEquals(3, buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x34}));
//        buffer.readerIndex(buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x34}));
//        System.out.println(buffer.readStringUTF8(2));
//        System.out.println(buffer.getString(buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x34}), 2, Charset.forName("UTF-8")));
//        buffer.readerIndex(buffer.indexOf(0, 5, new byte[]{(byte) 0x34}));
//        System.out.println(buffer.getString(buffer.indexOf(0, 5, new byte[]{(byte) 0x34}), 1, Charset.forName("UTF-8")));
//        System.out.println(buffer.readStringUTF8(1));
//        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testGetBytes() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT,0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        byte[] bytes = new byte[2];
        buffer.getBytes(1, bytes);
        System.out.println(Hex.toDisplayString(bytes));

    }

    @Test
    public void testGetBytes1() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT,0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        ByteBuffer buffer1 = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 3, 3);
        buffer.getBytes(1, buffer1);
        System.out.println(Hex.toDisplayString(buffer1.array()));
    }

    @Test
    public void testGetBytes2() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT,0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        ByteBuffer buffer1 = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 3, 3);
        buffer.getBytes(1, buffer1, 2);
        System.out.println(Hex.toDisplayString(buffer1.array()));
    }

    @Test
    public void testGetBytes3() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        java.nio.ByteBuffer buffer1 = java.nio.ByteBuffer.allocate(2);
        buffer.getBytes(1, buffer1);
        System.out.println(Hex.toDisplayString(buffer1.array()));
    }

    @Test
    public void testGetBytes4() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        FileOutputStream fos = new FileOutputStream("./target/test.txt");
        buffer.getBytes(0, fos, 3);
        fos.flush();
        fos.close();
    }

    @Test
    public void testGetBytes5() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT,0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        FileOutputStream fos = new FileOutputStream("./target/test.txt");
        buffer.getBytes(0, (GatheringByteChannel) fos.getChannel(), 4);
        fos.flush();
        fos.close();
    }

    @Test
    public void testGetBytes6() throws Exception {

    }

    @Test
    public void testGetBytes7() throws Exception {

    }

    @Test
    public void testSetZero() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(ByteBufferAllocator.DEFAULT, 0, 5);
        buffer.writeBytes(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34});
        System.out.println(Hex.toDisplayString(buffer.array()));
        buffer.setZero(0, 4);
        System.out.println(Hex.toDisplayString(buffer.array()));
        buffer.clear();
        System.out.println(Hex.toDisplayString(buffer.array()));
        buffer.writeByte((byte) 0x30);
        buffer.writeByte((byte) 0x30);
        buffer.writeByte((byte) 0x30);
        buffer.writeByte((byte) 0x30);
        buffer.writeByte((byte) 0x30);
        System.out.println(Hex.toDisplayString(buffer.array()));
    }
}