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
     * @param create 自动创建目录
     * @return 版本号列表
     * @throws IOException IO异常
     */
    public abstract List<Long> versions(boolean create) throws IOException;

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
