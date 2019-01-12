/**
 * RNKRSOFT OPEN SOURCE SOFTWARE LICENSE TERMS ver.1
 * - 氡氪网络科技(重庆)有限公司 开源软件许可条款(版本1)
 * 氡氪网络科技(重庆)有限公司 以下简称Rnkrsoft。
 * 这些许可条款是 Rnkrsoft Corporation（或您所在地的其中一个关联公司）与您之间达成的协议。
 * 请阅读本条款。本条款适用于所有Rnkrsoft的开源软件项目，任何个人或企业禁止以下行为：
 * .禁止基于删除开源代码所附带的本协议内容、
 * .以非Rnkrsoft的名义发布Rnkrsoft开源代码或者基于Rnkrsoft开源源代码的二次开发代码到任何公共仓库,
 * 除非上述条款附带有其他条款。如果确实附带其他条款，则附加条款应适用。
 * <p/>
 * 使用该软件，即表示您接受这些条款。如果您不接受这些条款，请不要使用该软件。
 * 如下所述，安装或使用该软件也表示您同意在验证、自动下载和安装某些更新期间传输某些标准计算机信息以便获取基于 Internet 的服务。
 * <p/>
 * 如果您遵守这些许可条款，将拥有以下权利。
 * 1.阅读源代码和文档
 * 如果您是个人用户，则可以在任何个人设备上阅读、分析、研究Rnkrsoft开源源代码。
 * 如果您经营一家企业，则禁止在任何设备上阅读Rnkrsoft开源源代码,禁止分析、禁止研究Rnkrsoft开源源代码。
 * 2.编译源代码
 * 如果您是个人用户，可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作，编译产生的文件依然受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作。
 * 3.二次开发拓展功能
 * 如果您是个人用户，可以基于Rnkrsoft开源源代码进行二次开发，修改产生的元代码同样受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码进行任何二次开发，但是可以通过联系Rnkrsoft进行商业授予权进行修改源代码。
 * 完整协议。本协议以及开源源代码附加协议，共同构成了Rnkrsoft开源软件的完整协议。
 * <p/>
 * 4.免责声明
 * 该软件按“原样”授予许可。 使用本文档的风险由您自己承担。Rnkrsoft 不提供任何明示的担保、保证或条件。
 * 5.版权声明
 * 本协议所对应的软件为 Rnkrsoft 所拥有的自主知识产权，如果基于本软件进行二次开发，在不改变本软件的任何组成部分的情况下的而二次开发源代码所属版权为贵公司所有。
 */
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
    public ByteBuf put(String charset, String... lines) {
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
