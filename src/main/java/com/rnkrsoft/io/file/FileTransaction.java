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
package com.rnkrsoft.io.file;

import com.rnkrsoft.io.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rnkrsoft.com on 2018/2/15.
 * 文件事务
 */
public interface FileTransaction {
    /**
     * 获取动态文件对象
     * @return
     */
    DynamicFile getDynamicFile();
    /**
     * 获取事务编号
     * @return
     */
    String getTransactionId();
    /**
     * 获取上次活动时间
     *
     * @return 返回毫秒数
     */
    long getLastActiveTime();

    /**
     * 写入字节数组，对事务的活动时间进行更新
     *
     * @param data 字节数组
     * @throws IOException IO异常
     */
    void write(byte[] data) throws IOException;

    /**
     * 写入字符序列数组，对事务的活动时间进行更新
     *
     * @param charSequence 字符序列
     * @throws IOException IO异常
     */
    void write(CharSequence charSequence) throws IOException;

    /**
     * 写入缓冲区，对事务的活动时间进行更新
     *
     * @param byteBuf 字节缓冲区
     * @throws IOException IO异常
     */
    void write(ByteBuf byteBuf) throws IOException;

    /**
     * 读取指定文件版本号的文件数据为字节缓冲区，对事务的活动时间进行更新
     *
     * @return 字节缓冲区
     * @throws IOException IO异常
     */
    ByteBuf read() throws IOException;

    /**
     * 打开当前文件为输入流，对事务的活动时间进行更新
     *
     * @return 输入流
     * @throws IOException IO异常
     */
    InputStream stream() throws IOException;

    /**
     * 是否已经已经提交
     *
     * @return
     * @throws IOException
     */
    boolean isFinished() throws IOException;

    /**
     * 提交当前文件的新版本，销毁事务
     *
     * @return 是否提交成功
     * @throws IOException IO异常
     */
    boolean commit() throws IOException;

    /**
     * 回滚当前文件的未提交的新版本，销毁事务
     *
     * @return 是否能够提交成功
     * @throws IOException IO异常
     */
    boolean rollback() throws IOException;


    /**
     * 获取当前事务对应的文件对象句柄
     *
     * @return 文件对象
     * @throws IOException IO异常
     */
    File getFile() throws IOException;
}
