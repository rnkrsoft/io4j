package com.rnkrsoft.io.buffer;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterBufferTest {

    @Test
    public void expendData() {
        CharacterBuffer buffer = new CharacterBuffer(1);
        Assert.assertEquals(1, buffer.capacity());
        buffer.expendData(2);
        Assert.assertEquals(2, buffer.capacity());
    }

    @Test
    public void isAutoExpand() {
        CharacterBuffer buffer = new CharacterBuffer(1);
        Assert.assertEquals(false, buffer.isAutoExpand());
        buffer.autoExpand(true);
        Assert.assertEquals(true, buffer.isAutoExpand());
    }

    @Test
    public void isReadOnly() {
        CharacterBuffer buffer = new CharacterBuffer(1);
        Assert.assertEquals(false, buffer.isReadOnly());
        buffer.readOnly(true);
        Assert.assertEquals(true, buffer.isReadOnly());
    }

    @Test
    public void clear() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        buffer.put('1');
        buffer.put('2');
        Assert.assertEquals(2, buffer.readableLength());
        Assert.assertEquals(0, buffer.writableLength());
        buffer.clear();
        Assert.assertEquals(0, buffer.readableLength());
        Assert.assertEquals(2, buffer.writableLength());
    }

    @Test
    public void readyRead() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(false, buffer.readyRead());
        buffer.put('1');
        Assert.assertEquals(true, buffer.readyRead());
    }

    @Test
    public void resetWrite() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(2, buffer.writableLength());
        Assert.assertEquals(0, buffer.readableLength());
        buffer.put("12");
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
        buffer.resetWrite();
        Assert.assertEquals(2, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
    }

    @Test
    public void resetRead() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(2, buffer.writableLength());
        Assert.assertEquals(0, buffer.readableLength());
        buffer.put("12");
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
        buffer.getChars(1);
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(1, buffer.readableLength());
        buffer.resetRead();
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
    }

    @Test
    public void readableLength() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(0, buffer.readableLength());
        buffer.put('1');
        Assert.assertEquals(1, buffer.readableLength());
        buffer.put('2');
        Assert.assertEquals(2, buffer.readableLength());
    }

    @Test
    public void writableLength() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(2, buffer.writableLength());
        buffer.put('1');
        Assert.assertEquals(1, buffer.writableLength());
        buffer.put('2');
        Assert.assertEquals(0, buffer.writableLength());
    }


    @Test
    public void put() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(2, buffer.writableLength());
        Assert.assertEquals(0, buffer.readableLength());
        buffer.put("12".toCharArray());
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
    }

    @Test
    public void put1() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(2, buffer.writableLength());
        Assert.assertEquals(0, buffer.readableLength());
        buffer.put("12");
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
    }

    @Test
    public void put2() {
        CharacterBuffer buffer = new CharacterBuffer(2);
        Assert.assertEquals(2, buffer.writableLength());
        Assert.assertEquals(0, buffer.readableLength());
        buffer.put('1');
        buffer.put('2');
        Assert.assertEquals(0, buffer.writableLength());
        Assert.assertEquals(2, buffer.readableLength());
    }

    @Test
    public void get() {
        char[] chars = "ABC".toCharArray();
        CharacterBuffer buffer = new CharacterBuffer(10);
        buffer.put(chars);

        char[] data = new char[1];
        buffer.get(data);
        System.out.println(data[0]);
        buffer.get(data);
        System.out.println(data[0]);
    }

    @Test
    public void getChar() {
        char[] chars = "ABC".toCharArray();
        CharacterBuffer buffer = new CharacterBuffer(10);
        buffer.put(chars);
        System.out.println(buffer.getChar());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getChar());
    }

    @Test
    public void getChars() {
        char[] chars = "ABC".toCharArray();
        CharacterBuffer buffer = new CharacterBuffer(10);
        buffer.put(chars);
        System.out.println(new String(buffer.getChars(3)));
    }

    @Test
    public void getString() {
        char[] chars = "ABC".toCharArray();
        CharacterBuffer buffer = new CharacterBuffer(10);
        buffer.put(chars);
        System.out.println(buffer.getString(1));
        System.out.println(buffer.getString(1));
        System.out.println(buffer.getString(1));
    }

    @Test
    public void slice() {
        char[] chars = "ABC".toCharArray();
        CharacterBuffer buffer = new CharacterBuffer(chars);
        {
            CharacterBuffer buffer1 = buffer.slice(0, 1);
            Assert.assertEquals("A", buffer1.getString(buffer1.readableLength()));
            CharacterBuffer buffer2 = buffer.slice(1, 1);
            Assert.assertEquals("B", buffer2.getString(buffer2.readableLength()));
            CharacterBuffer buffer3 = buffer.slice(2, 1);
            Assert.assertEquals("C", buffer3.getString(buffer3.readableLength()));
        }

        {
            CharacterBuffer buffer1 = buffer.slice(0, 2);
            Assert.assertEquals("AB", buffer1.getString(buffer1.readableLength()));
            CharacterBuffer buffer2 = buffer.slice(1, 2);
            Assert.assertEquals("BC", buffer2.getString(buffer2.readableLength()));
        }

        {
            CharacterBuffer buffer1 = buffer.slice(0, 3);
            Assert.assertEquals("ABC", buffer1.getString(buffer1.readableLength()));
        }
    }
}