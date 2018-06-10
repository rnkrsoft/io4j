package com.rnkrsoft.io.file;

import com.rnkrsoft.io.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by devops4j on 2018/2/15.
 * 文件事务
 */
public interface FileTransaction {
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
