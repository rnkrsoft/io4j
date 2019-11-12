package com.rnkrsoft.io.buffer;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;


/**
 * Created by rnkrsoft.com on 2019/11/1.
 */
public class UnpooledDirectByteBufferTest {

    @Test
    public void testWriteStringUTF8() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(10, 40);
        buffer.writeStringUTF8("123456789");
        Assert.assertEquals("313233343536373839300000000000000000000000000000000000000000", Hex.toHexString(buffer.array()));
    }

    @Test
    public void testReadStringUTF8() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(10, 40);
        buffer.writeStringUTF8("1234567890");
        String str = buffer.readStringUTF8(10);
        Assert.assertEquals("1234567890", str);
    }

    @Test
    public void testInternalNioBuffer() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(10, 40);
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        buffer.writeStringUTF8("1234567890");
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteBoolean() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(10, 40);
        buffer.writeBoolean(true);
        buffer.writeBoolean(true);
        buffer.writeBoolean(false);
        buffer.writeBoolean(true);
        Assert.assertEquals(true, buffer.readBoolean());
        Assert.assertEquals(true, buffer.readBoolean());
        Assert.assertEquals(false, buffer.readBoolean());
        Assert.assertEquals(true, buffer.readBoolean());
    }

    @Test
    public void testWriteByte() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(10, 40);
        buffer.writeByte((byte) 0x1);
        buffer.writeByte((byte) 0x2);
        buffer.writeByte((byte) 0x1F);
        buffer.writeByte((byte) 0x7F);
        Assert.assertEquals(0x1, buffer.readByte());
        Assert.assertEquals(0x2, buffer.readByte());
        Assert.assertEquals(0x1F, buffer.readByte());
        Assert.assertEquals(0x7F, buffer.readByte());
    }

    @Test
    public void testWriteShortLE() throws Exception {

    }

    @Test
    public void testWriteShort() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(10, 40);
        buffer.writeShort((short) 0x1);
        buffer.writeShort((short) 0x2);
        buffer.writeShort((short) 0x1F);
        buffer.writeShort(Short.MIN_VALUE);
        buffer.writeShort(Short.MAX_VALUE);
        Assert.assertEquals((short) 0x1, buffer.readShort());
        Assert.assertEquals((short) 0x2, buffer.readShort());
        Assert.assertEquals((short) 0x1F, buffer.readShort());
        Assert.assertEquals(Short.MIN_VALUE, buffer.readShort());
        Assert.assertEquals(Short.MAX_VALUE, buffer.readShort());
    }

    @Test
    public void testWriteMedium() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(12, 40);
        buffer.writeMedium(0x1);
        buffer.writeMedium(0x2);
        buffer.writeMedium(0x1F);
        buffer.writeMedium(0xFFFFFF);
        Assert.assertEquals(0x1, buffer.readMedium());
        Assert.assertEquals(0x2, buffer.readMedium());
        Assert.assertEquals(0x1F, buffer.readMedium());
        Assert.assertEquals(0xFFFFFF, buffer.readMedium());
    }

    @Test
    public void testWriteMediumLE() throws Exception {

    }

    @Test
    public void testWriteInt() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(16, 32);
        buffer.writeInt(0x1);
        buffer.writeInt(0x2);
        buffer.writeInt(Integer.MIN_VALUE);
        buffer.writeInt(Integer.MAX_VALUE);
        Assert.assertEquals(0x1, buffer.readInt());
        Assert.assertEquals(0x2, buffer.readInt());
        Assert.assertEquals(Integer.MIN_VALUE, buffer.readInt());
        Assert.assertEquals(Integer.MAX_VALUE, buffer.readInt());
    }

    @Test
    public void testWriteIntLE() throws Exception {

    }

    @Test
    public void testWriteLongLE() throws Exception {

    }

    @Test
    public void testWriteLong() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(32, 40);
        buffer.writeLong(0x1);
        buffer.writeLong(0x2);
        buffer.writeLong(Long.MIN_VALUE);
        buffer.writeLong(Long.MAX_VALUE);
        Assert.assertEquals(0x1, buffer.readLong());
        Assert.assertEquals(0x2, buffer.readLong());
        Assert.assertEquals(Long.MIN_VALUE, buffer.readLong());
        Assert.assertEquals(Long.MAX_VALUE, buffer.readLong());
    }

    @Test
    public void testWriteChar() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(12, 40);
        buffer.writeChar('0');
        buffer.writeChar('A');
        buffer.writeChar('a');
        buffer.writeChar('Z');
        buffer.writeChar('z');
        buffer.writeChar('我');
        Assert.assertEquals('0', buffer.readChar());
        Assert.assertEquals('A', buffer.readChar());
        Assert.assertEquals('a', buffer.readChar());
        Assert.assertEquals('Z', buffer.readChar());
        Assert.assertEquals('z', buffer.readChar());
        Assert.assertEquals('我', buffer.readChar());
    }

    @Test
    public void testWriteFloat() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(8, 40);
        buffer.writeFloat(123.456F);
        buffer.writeFloat(0.123456F);
        Assert.assertEquals("123.456", Float.toString(buffer.readFloat()));
        Assert.assertEquals("0.123456", Float.toString(buffer.readFloat()));
    }

    @Test
    public void testWriteDouble() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(16, 40);
        buffer.writeDouble(123.456D);
        buffer.writeDouble(0.123456D);
        Assert.assertEquals("123.456", Double.toString(buffer.readDouble()));
        Assert.assertEquals("0.123456", Double.toString(buffer.readDouble()));
    }

    @Test
    public void testGetByte0() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(Byte.MAX_VALUE, Byte.MAX_VALUE);
        for (byte i = 0; i < Byte.MAX_VALUE; i++) {
            buffer.writeByte(i);
            Assert.assertEquals(i, buffer.getByte(i));
        }
        while (buffer.isReadable()){
            System.out.println(buffer.readByte());
        }
    }

    @Test
    public void testGetShort0() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(Short.MAX_VALUE * 2, Short.MAX_VALUE * 2);
        for (short i = 0; i < Short.MAX_VALUE - 1; i++) {
            buffer.writeShort(i);
            Assert.assertEquals(i, buffer.getShort(2 * i));
        }
        while (buffer.isReadable()){
            System.out.println(buffer.readShort());
        }
    }

    @Test
    public void testGetInt0() throws Exception {

    }

    @Test
    public void testGetLong0() throws Exception {

    }

    @Test
    public void testWriteBoolean1() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(2, 2);
        buffer.writeBoolean(false);
        Assert.assertEquals(false, buffer.readBoolean());
        buffer.writeBoolean(true);
        Assert.assertEquals(true, buffer.readBoolean());
    }

    @Test
    public void testWriteByte1() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(Byte.MAX_VALUE, Byte.MAX_VALUE);
        for (byte i = 0; i < Byte.MAX_VALUE; i++) {
            buffer.writeByte(i);
            Assert.assertEquals(i, buffer.readByte());
        }
        Assert.assertEquals(Byte.MAX_VALUE, buffer.readerIndex());
        Assert.assertEquals(Byte.MAX_VALUE, buffer.writerIndex());
    }

    @Test
    public void testWriteShortLE1() throws Exception {

    }

    @Test
    public void testWriteShort1() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(2 * Short.MAX_VALUE, 2 * Short.MAX_VALUE);
        for (short i = 0; i < Short.MAX_VALUE; i++) {
            buffer.writeShort(i);
            Assert.assertEquals(i, buffer.readShort());
        }
        Assert.assertEquals(2 * Short.MAX_VALUE, buffer.readerIndex());
        Assert.assertEquals(2 * Short.MAX_VALUE, buffer.writerIndex());
    }

    @Test
    public void testWriteMedium1() throws Exception {

    }

    @Test
    public void testWriteMediumLE1() throws Exception {

    }

    @Test
    public void testWriteInt1() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(1024, 1024);
        for (int i = 0; i < 256; i++) {
            buffer.writeInt(i);
            Assert.assertEquals(i, buffer.readInt());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
        Assert.assertEquals(1024, buffer.readerIndex());
        Assert.assertEquals(1024, buffer.writerIndex());
    }

    @Test
    public void testWriteIntLE1() throws Exception {

    }

    @Test
    public void testWriteLongLE1() throws Exception {

    }

    @Test
    public void testWriteLong1() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(2048, 2048);
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
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(2048, 2048);
        for (char i = 'a'; i < 'z'; i++) {
            buffer.writeChar(i);
            Assert.assertEquals(i, buffer.readChar());
        }
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testWriteFloat1() throws Exception {

    }

    @Test
    public void testWriteDouble1() throws Exception {

    }

    @Test
    public void testWriteString() throws Exception {
        final UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(48, 48);
        buffer.writeString("0123456789ABCDEF", Charset.forName("UTF-8"));
        Assert.assertEquals("01234", buffer.readString(5, Charset.forName("UTF-8")));
        Assert.assertEquals("56789ABCDEF", buffer.readString(11, Charset.forName("UTF-8")));
        new Thread(){
            @Override
            public void run() {
                buffer.clear();
                buffer.writeString("0123456789ABCDEF", Charset.forName("UTF-8"));
                Assert.assertEquals("01234", buffer.readString(5, Charset.forName("UTF-8")));
                Assert.assertEquals("56789ABCDEF", buffer.readString(11, Charset.forName("UTF-8")));
            }
        }.start();
        Thread.sleep(1000);
    }

    @Test
    public void testWriteStringUTF81() throws Exception {
        final UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(48, 48);
        buffer.writeStringUTF8("0123456789ABCDEF");
        Assert.assertEquals("0123456789ABCDEF", buffer.readStringUTF8(16));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));

    }

    @Test
    public void testReadString() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(Hex.fromHexString("30313233343536373839414243444546"), 0, 48);
        Assert.assertEquals("0123456789ABCDEF", buffer.readString(16, Charset.forName("UTF-8")));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testReadStringUTF81() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(Hex.fromHexString("30313233343536373839414243444546"), 0, 48);
        Assert.assertEquals("0123456789ABCDEF", buffer.readStringUTF8(16));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testForEachByte() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        Assert.assertEquals(-1, buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x35}));
        Assert.assertEquals(-1, buffer.indexOf(0, 5, new byte[]{(byte) 0x35}));
        Assert.assertEquals(4, buffer.indexOf(0, 5, new byte[]{(byte) 0x34}));
        Assert.assertEquals(3, buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x34}));
        buffer.readerIndex(buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x34}));
        System.out.println(buffer.readStringUTF8(2));
        System.out.println(buffer.getString(buffer.indexOf(0, 5, new byte[]{(byte) 0x33, (byte) 0x34}), 2, Charset.forName("UTF-8")));
        buffer.readerIndex(buffer.indexOf(0, 5, new byte[]{(byte) 0x34}));
        System.out.println(buffer.getString(buffer.indexOf(0, 5, new byte[]{(byte) 0x34}), 1, Charset.forName("UTF-8")));
        System.out.println(buffer.readStringUTF8(1));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testGetBytes() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        byte[] bytes = new byte[2];
        buffer.getBytes(1, bytes);
        System.out.println(Hex.toDisplayString(bytes));

    }

    @Test
    public void testGetBytes1() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        ByteBuffer buffer1 = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 3, 3);
        buffer.getBytes(1, buffer1);
        System.out.println(Hex.toDisplayString(buffer1.array()));
    }

    @Test
    public void testGetBytes2() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        ByteBuffer buffer1 = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 3, 3);
        buffer.getBytes(1, buffer1, 2);
        System.out.println(Hex.toDisplayString(buffer1.array()));
    }

    @Test
    public void testGetBytes3() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        java.nio.ByteBuffer buffer1 = java.nio.ByteBuffer.allocate(2);
        buffer.getBytes(1, buffer1);
        System.out.println(Hex.toDisplayString(buffer1.array()));
    }

    @Test
    public void testGetBytes4() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        FileOutputStream fos = new FileOutputStream("./target/test.txt");
        buffer.getBytes(0, fos.getChannel(), 0, 3);
        fos.flush();
        fos.close();
    }

    @Test
    public void testGetBytes5() throws Exception {
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
        FileOutputStream fos = new FileOutputStream("./target/test.txt");
        buffer.getBytes(0, (GatheringByteChannel)fos.getChannel(), 4);
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
        UnpooledDirectByteBuffer buffer = new UnpooledDirectByteBuffer(new byte[]{(byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34}, 0, 5);
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