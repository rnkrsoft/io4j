package com.rnkrsoft.io.file;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.io.file.impl.DynamicFileImpl;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rnkrsoft.com on 2018/2/15.
 * 动态文件格式
 */
public abstract class DynamicFile {
    /**
     * 默认提交超时间秒数10秒
     */
    public static final int DEFAULT_COMMIT_TIMEOUT_SEC = 10;
    /**
     * 默认备份数10
     */
    public static final int DEFAULT_BACKUP_SIZE = 10;
    /**
     * 文件所在目录
     */
    @Getter
    protected String directory;
    /**
     * 文件名
     */
    @Getter
    protected String fileName;
    /**
     * 文件后缀名
     */
    @Getter
    protected String fileSuffix;
    /**
     * 存放备份数
     */
    @Getter
    protected int backupSize = DEFAULT_BACKUP_SIZE;

    @Getter
    protected int timeoutSec = DEFAULT_COMMIT_TIMEOUT_SEC;

    /**
     * 保存所有文件事务
     */
    protected Map<String, FileTransaction> transactions = new ConcurrentHashMap();

    /**
     * 根据包含文件名的路径创建动态文件，默认备份数10
     * @param file 文件路径
     * @return 动态文件
     */
    public static DynamicFile file(String file) {
        return new DynamicFileImpl(file, DEFAULT_BACKUP_SIZE);
    }

    /**
     * 根据包含文件名的路径创建动态文件，设置保存的备份数
     * @param file 文件路径
     * @param backupSize 备份数
     * @return 动态文件
     */
    public static DynamicFile file(String file, int backupSize) {
        return new DynamicFileImpl(file, backupSize);
    }

    /**
     * 根据包含文件对象创建动态文件，默认备份数10
     * @param file 文件对象
     * @return 动态文件
     */
    public static DynamicFile file(File file) {
        return new DynamicFileImpl(file, DEFAULT_BACKUP_SIZE);
    }

    /**
     *
     * @param file
     * @param backupSize
     * @return
     */
    public static DynamicFile file(File file, int backupSize) {
        return new DynamicFileImpl(file, backupSize);
    }

    public static DynamicFile file(File directory, String fileName) {
        return new DynamicFileImpl(new File(directory, fileName), DEFAULT_BACKUP_SIZE);
    }

    public static DynamicFile file(String directory, String fileName) {
        return new DynamicFileImpl(directory, fileName, DEFAULT_BACKUP_SIZE);
    }

    public static DynamicFile file(String directory, String fileName, int backupSize) {
        return new DynamicFileImpl(directory, fileName, backupSize);
    }

    /**
     * 检测是否存在文件
     *
     * @return
     * @throws IOException
     */
    public abstract boolean exists() throws IOException;

    /**
     * 删除当前文件
     *
     * @throws IOException
     */
    public abstract void delete() throws IOException;

    /**
     * 读取指定文件版本号的文件数据为字节缓冲区
     *
     * @param version 文件版本号
     * @return 字节缓冲区
     * @throws IOException IO异常
     */
    public abstract ByteBuf read(long version) throws IOException;

    /**
     * 打开当前文件为输入流
     *
     * @param version 文件版本号
     * @return 输入流
     * @throws IOException IO异常
     */
    public abstract InputStream stream(long version) throws IOException;

    /**
     * 发现最大的文件版本号
     *
     * @return 文件版本号，日期格式
     */
    public abstract long lookupMaxVersion() throws IOException;

    /**
     * 读取文件的所有已提交本版号
     *
     * @return 版本号列表
     * @throws IOException IO异常
     */
    public abstract List<Long> versions() throws IOException;

    /**
     * 获取指定版本号的文件，如果指定的版本号不存在，则抛出异常，指定版本号不存在
     *
     * @param version 版本号
     * @return
     * @throws IOException
     */
    public abstract FileWrapper getFile(long version) throws IOException;

    /**
     * 获取当前文件的最新版本文件
     *
     * @return File包装
     * @throws IOException IO异常
     */
    public FileWrapper getFile() throws IOException {
        long maxVersion = lookupMaxVersion();
        return getFile(maxVersion);
    }

    /**
     * 创建当前文件的新版本
     *
     * @return 文件事务对象
     * @throws IOException IO异常
     */
    public abstract FileTransaction begin() throws IOException;

    /**
     * 根据事务号获取事务
     *
     * @param transactionId 事务号
     * @return 事务对象
     * @throws IOException 异常
     */
    public FileTransaction getTransaction(String transactionId) throws IOException {
        //遍历事务，检查是否超时
        Iterator<FileTransaction> iterator = transactions.values().iterator();
        while (iterator.hasNext()) {
            FileTransaction fileTransaction = iterator.next();
            if (System.currentTimeMillis() - fileTransaction.getLastActiveTime() > timeoutSec * 1000) {
                destroy(transactionId);
            }
            if (fileTransaction.isFinished()) {
                destroy(transactionId);
            }
        }
        FileTransaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new TransactionNotFoundException("transaction id '" + transactionId + "' is not found");
        }
        if (transaction.isFinished()) {
            throw new TransactionAlreadyFinishedException("transaction id '" + transactionId + "' has already finished");
        }
        return transaction;
    }

    public DynamicFile destroy(String transactionId) throws IOException {
        transactions.remove(transactionId);
        return this;
    }

}
