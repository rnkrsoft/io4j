package com.rnkrsoft.io.buffer;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by renkrsoft.com on 2017/11/30.
 */
class HeapByteBuf extends ByteBuf {
    byte[] data = null;
    int writeBegin = 0;
    int writeEnd = 0;
    int readBegin = 0;
    int readEnd = 0;
    boolean readonly = false;
    boolean autoExpand = false;
    boolean bigEndian = false;

    public HeapByteBuf(byte[] data) {
        this.data = data;
        this.writeBegin = 0;
        this.writeEnd = data.length;
        this.readBegin = 0;
        this.readEnd = data.length;
    }

    public HeapByteBuf(int size) {
        this.data = new byte[size];
        this.writeBegin = 0;
        this.writeEnd = data.length;
        this.readBegin = 0;
        this.readEnd = 0;
    }


    @Override
    public boolean bigEndian() {
        return bigEndian;
    }

    @Override
    public ByteBuf bigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
        return this;
    }

    @Override
    public int capacity() {
        return data.length;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        expendData(newCapacity);
        return this;
    }

    void expendData(int newCapacity) {
        byte[] temp = new byte[newCapacity];
        if (newCapacity > data.length) {
            System.arraycopy(data, 0, temp, 0, data.length);
        } else {
            System.arraycopy(data, 0, temp, 0, newCapacity);
            this.readEnd = newCapacity - 1;
        }
        this.data = temp;
        this.writeEnd = newCapacity - 1;
    }

    @Override
    public int maxCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isAutoExpand() {
        return this.autoExpand;
    }

    @Override
    public ByteBuf autoExpand(boolean expand) {
        this.autoExpand = expand;
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return readonly;
    }

    @Override
    public ByteBuf readOnly(boolean readOnly) {
        this.readonly = readOnly;
        return this;
    }

    @Override
    public ByteBuf clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
        this.writeBegin = 0;
        this.writeEnd = data.length;
        this.readBegin = 0;
        this.readEnd = 0;
        return this;
    }


    @Override
    public ByteBuf put(byte v) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (writableLength() < 1) {
            if (this.autoExpand) {
                double newCap = Math.round((double) this.data.length * 1.75);
                expendData(Math.max((int) newCap, this.data.length + 1));
            } else {
                throw new ArrayIndexOutOfBoundsException("buffer is out of bounds!");
            }
        }
        this.data[writeBegin] = v;
        writeBegin++;
        readEnd++;
        return this;
    }

    @Override
    public ByteBuf put(short v) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (bigEndian) {
            put(com.rnkrsoft.io.buffer.Bits.short1(v));
            put(com.rnkrsoft.io.buffer.Bits.short0(v));
        } else {
            put(com.rnkrsoft.io.buffer.Bits.short0(v));
            put(com.rnkrsoft.io.buffer.Bits.short1(v));
        }
        return this;
    }

    @Override
    public ByteBuf put(int v) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (bigEndian) {
            put(com.rnkrsoft.io.buffer.Bits.int3(v));
            put(com.rnkrsoft.io.buffer.Bits.int2(v));
            put(com.rnkrsoft.io.buffer.Bits.int1(v));
            put(com.rnkrsoft.io.buffer.Bits.int0(v));
        } else {
            put(com.rnkrsoft.io.buffer.Bits.int0(v));
            put(com.rnkrsoft.io.buffer.Bits.int1(v));
            put(com.rnkrsoft.io.buffer.Bits.int2(v));
            put(com.rnkrsoft.io.buffer.Bits.int3(v));
        }
        return this;
    }

    @Override
    public ByteBuf put(long v) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (bigEndian) {
            put(com.rnkrsoft.io.buffer.Bits.long7(v));
            put(com.rnkrsoft.io.buffer.Bits.long6(v));
            put(com.rnkrsoft.io.buffer.Bits.long5(v));
            put(com.rnkrsoft.io.buffer.Bits.long4(v));
            put(com.rnkrsoft.io.buffer.Bits.long3(v));
            put(com.rnkrsoft.io.buffer.Bits.long2(v));
            put(com.rnkrsoft.io.buffer.Bits.long1(v));
            put(com.rnkrsoft.io.buffer.Bits.long0(v));
        } else {
            put(com.rnkrsoft.io.buffer.Bits.long0(v));
            put(com.rnkrsoft.io.buffer.Bits.long1(v));
            put(com.rnkrsoft.io.buffer.Bits.long2(v));
            put(com.rnkrsoft.io.buffer.Bits.long3(v));
            put(com.rnkrsoft.io.buffer.Bits.long4(v));
            put(com.rnkrsoft.io.buffer.Bits.long5(v));
            put(com.rnkrsoft.io.buffer.Bits.long6(v));
            put(com.rnkrsoft.io.buffer.Bits.long7(v));
        }
        return this;
    }

    @Override
    public ByteBuf put(float v) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        put(Float.floatToRawIntBits(v));
        return this;
    }

    @Override
    public ByteBuf put(double v) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        put(Double.doubleToRawLongBits(v));
        return this;
    }

    @Override
    public ByteBuf put(byte[] bytes) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (bytes == null) {
            return this;
        }
        int length = bytes.length;
        //检测是否越界
        if (writableLength() < length) {
            if (this.autoExpand) {
                int newCap = (int) ((double) this.data.length * 1.75);
                expendData(Math.max(newCap, this.data.length + length));
            } else {
                throw new ArrayIndexOutOfBoundsException("buffer is out of bounds!");
            }
        }
        System.arraycopy(bytes, 0, this.data, this.writeBegin, length);
        //重新计算指针
        this.writeBegin += length;
        this.readEnd += length;
        return this;
    }

    @Override
    public ByteBuf put(String charset, String... strings) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        for (String line : lines) {
            try {
                if (line == null) {
                    break;
                }
                byte[] temp = line.getBytes(charset);
                put(temp);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public ByteBuf putUTF_8(String... strings) {
        return put("UTF-8", strings);
    }

    @Override
    public ByteBuf putGBK(String... strings) {
        return put("GBK", strings);
    }

    @Override
    public ByteBuf put(ByteBuffer buffer) {
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        put(buffer.array());
        return this;
    }
	
	    @Override
    public ByteBuf append(String charset, String line) {
        try {
            byte[] temp = line.getBytes(charset);
            put(temp);
            put("\n");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public byte[] getBytes() {
        return this.data;
    }

    @Override
    public byte[] getBytes(int length) {
        byte[] temp = new byte[length];
        get(temp);
        return temp;
    }

    @Override
    public ByteBuf get(byte[] data) {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        int length = data.length;
        if (readableLength() < length) {
            throw new ArrayIndexOutOfBoundsException("buffer is out of bounds!");
        }
        System.arraycopy(this.data, this.readBegin, data, 0, length);
        this.readBegin += length;
        return this;
    }

    @Override
    public boolean readyRead() {
        return this.readEnd > this.readBegin;
    }

    @Override
    public ByteBuf resetWrite() {
        this.writeBegin = 0;
        return this;
    }

    @Override
    public ByteBuf resetRead() {
        this.readBegin = 0;
        return this;
    }

    @Override
    public ByteBuffer asByteBuffer() {
        byte[] temp = new byte[readableLength()];
        get(temp);
        return ByteBuffer.wrap(temp);
    }

    @Override
    public ByteArrayInputStream asInputStream() {
        return new ByteArrayInputStream(getBytes(readableLength()));
    }

    @Override
    public int read(InputStream is) throws IOException {
        if (is == null) {
            throw new NullPointerException("InputStream is null!");
        }
        if(readonly){
            throw new IllegalArgumentException("buffer is readonly!");
        }
        int byteLen = 0;
        int byteReadLen = 0;
        byte[] data = new byte[1024];
        while ((byteReadLen = is.read(data)) != -1) {
            if(byteReadLen == 1024){
                put(data);
            }else{
                byte[] data0 = new byte[byteReadLen];
                System.arraycopy(data, 0, data0, 0, byteReadLen);
                put(data0);
            }
            byteLen += byteReadLen;
        }
        return byteLen;
    }

    @Override
    public int write(OutputStream os) throws IOException {
        if (os == null) {
            throw new NullPointerException("OutputStream is null!");
        }
        int byteLen = 0;
        int byteWriteLen = 0;
        while ((byteWriteLen = readableLength()) > 0) {
            byte[] data = new byte[Math.min(1024, byteWriteLen)];
            byteLen += data.length;
            get(data);
            os.write(data);
        }
        return byteLen;
    }


    public int readableLength() {
        return this.readEnd - this.readBegin;
    }

    @Override
    public int writableLength() {
        return this.writeEnd - this.writeBegin;
    }

    @Override
    public ByteBuf get(ByteBuffer buffer) {
        int length = readableLength();
        if (buffer.remaining() < length) {
            throw new ArrayIndexOutOfBoundsException("spaces is not enough!");
        }
        byte[] temp = new byte[length];
        get(temp);
        buffer.put(temp);
        return this;
    }

    @Override
    public String asString(String charset) {
        return getString(charset, readableLength());
    }


    @Override
    public String getString(String charset, int length) {
        byte[] temp = new byte[length];
        get(temp);
        String str = null;
        try {
            str = new String(temp, charset);
            return str;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public byte getByte() {
        if (readableLength() < 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        byte b = this.data[readBegin++];
        return b;
    }

    @Override
    public short getShort() {
        byte[] temp = new byte[2];
        get(temp);
        return bigEndian
                ? com.rnkrsoft.io.buffer.Bits.makeShort(temp[0], temp[1])
                : com.rnkrsoft.io.buffer.Bits.makeShort(temp[1], temp[0]);
    }

    @Override
    public int getInt() {
        byte[] temp = new byte[4];
        get(temp);
        return bigEndian
                ? com.rnkrsoft.io.buffer.Bits.makeInt(temp[0], temp[1], temp[2], temp[3])
                : com.rnkrsoft.io.buffer.Bits.makeInt(temp[3], temp[2], temp[1], temp[0]);
    }

    @Override
    public long getLong() {
        byte[] temp = new byte[8];
        get(temp);
        return bigEndian
                ? com.rnkrsoft.io.buffer.Bits.makeLong(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7])
                : com.rnkrsoft.io.buffer.Bits.makeLong(temp[7], temp[6], temp[5], temp[4], temp[3], temp[2], temp[1], temp[0]);
    }


    @Override
    public float getFloat() {
        int v = getInt();
        return Float.intBitsToFloat(v);
    }

    @Override
    public double getDouble() {
        long v = getLong();
        return Double.longBitsToDouble(v);
    }

}
