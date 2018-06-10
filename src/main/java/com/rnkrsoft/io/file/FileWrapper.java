package com.rnkrsoft.io.file;

import com.rnkrsoft.io.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by devops4j on 2018/2/16.
 * 文件包装类
 */
public interface FileWrapper {
    /**
     * 获取文件版本号
     *
     * @return
     * @throws IOException
     */
    long getVersion() throws IOException;

    /**
     * 读取指定文件版本号的文件数据为字节缓冲区
     *
     * @return 字节缓冲区
     * @throws IOException IO异常
     */
    ByteBuf read() throws IOException;

    /**
     * 打开当前文件为输入流
     *
     * @return 输入流
     * @throws IOException IO异常
     */
    InputStream stream() throws IOException;

    /**
     * 删除当前文件
     *
     * @throws IOException
     */
    void delete() throws IOException;

    /**
     * 获取文件句柄
     *
     * @return
     */
    File getFile() throws IOException;
}
