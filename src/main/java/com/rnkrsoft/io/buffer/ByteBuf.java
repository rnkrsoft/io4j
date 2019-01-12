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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by rnkrsoft.com on 2017/11/30.
 * 字节缓冲区
 */
public abstract class ByteBuf {
    /**
     * 创建一个指定容量大小的缓冲区
     * @param capacity 容量
     * @return 缓冲区
     */
    public static ByteBuf allocate(int capacity) {
        return new HeapByteBuf(capacity);
    }

    /**
     * 根据字节数组创建缓冲区
     * @param data 字节数组
     * @return 缓冲区
     */
    public static ByteBuf allocate(byte[] data) {
        return new HeapByteBuf(data);
    }

    public abstract boolean bigEndian();

    public abstract ByteBuf bigEndian(boolean bigEndian);

    public abstract int capacity();

    public abstract ByteBuf capacity(int newCapacity);

    public abstract int maxCapacity();

    public abstract boolean isAutoExpand();

    public abstract ByteBuf autoExpand(boolean autoExpand);

    public abstract boolean isReadOnly();

    public abstract ByteBuf readOnly(boolean readOnly);

    public abstract ByteBuf clear();

    public abstract ByteBuf put(byte v);

    public abstract ByteBuf put(short v);

    public abstract ByteBuf put(int v);

    public abstract ByteBuf put(long v);

    public abstract ByteBuf put(float v);

    public abstract ByteBuf put(double v);

    public abstract ByteBuf put(byte[] v);

    public abstract ByteBuf put(String charset, String... strings);
    public abstract ByteBuf putUTF_8(String... strings);
    public abstract ByteBuf putGBK(String... strings);

    public abstract ByteBuf put(ByteBuffer buffer);

    public abstract ByteBuf append(String charset, String line);
    public abstract ByteBuf get(byte[] data);

    /**
     * 检测缓存是否还有可读的
     * @return 是否可读
     */
    public abstract boolean readyRead();

    /**
     * 重置写指针
     * @return 缓冲区对象
     */
    public abstract ByteBuf resetWrite();
    /**
     * 重置读指针
     * @return 缓冲区对象
     */
    public abstract ByteBuf resetRead();
    /**
     * 将缓冲区中的内容作为ByteBuffer缓冲区
     * @return ByteBuffer缓冲区
     */
    public abstract ByteBuffer asByteBuffer();

    /**
     * 将缓冲区中的内容作为输入流
     * @return 字节数组输入流
     */
    public abstract ByteArrayInputStream asInputStream();

    /**
     * 从输入流读取
     * @param is 输入流
     * @return 读取字节数
     * @throws IOException 异常
     */
    public abstract int read(InputStream is)throws IOException;
    /**
     * 向输出流写入
     * @param os 输入流
     * @return 写入字节数
     * @throws IOException 异常
     */
    public abstract int write(OutputStream os) throws IOException;

    /**
     * 使用ByteBuffer获取ByteBuf的内容
     * @param buffer java缓冲区对象
     * @return 缓冲区对象
     */
    public abstract ByteBuf get(ByteBuffer buffer);

    /**
     * 根据默认字符集将所有内容输出未字符串
     * @return 字符串
     */
    public String asString(){
        return asString(System.getProperty("file.encoding"));
    }
    /**
     * 将所有内容作为字符串输出
     * @param charset 字符集
     * @return 字符串
     */
    public abstract String asString(String charset);

    /**
     * 根据默认字符集从当前读指针开始读取指定长度的字符串
     * @param length 长度
     * @return 字符串
     */
    public String getString(int length){
        return getString(System.getProperty("file.encoding"), length);
    }
    /**
     * 根据默认字符集从当前读指针开始读取指定长度的字符串
     * @param charset 字符集
     * @param length 长度
     * @return 字符串
     */
    public abstract String getString(String charset, int length);

    public abstract byte getByte();

    public abstract short getShort();

    public abstract int getInt();

    public abstract long getLong();

    public abstract float getFloat();

    public abstract double getDouble();

    public abstract byte[] getBytes();

    public abstract byte[] getBytes(int length);

    public abstract int readableLength();

    public abstract int writableLength();
}
