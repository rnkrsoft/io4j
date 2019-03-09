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
package com.rnkrsoft.io.file.impl;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.io.file.DynamicFile;
import com.rnkrsoft.io.file.FileTransaction;
import com.rnkrsoft.io.file.FileWrapper;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2018/2/15.
 * 动态文件实现类
 */
@Slf4j
@ToString
public final class DynamicFileImpl extends DynamicFile {
    long maxVersion;

    public DynamicFileImpl(String directory, String fileName, int backupSize) {
        this.directory = directory;
        this.fileName = fileName;
        int idx = fileName.lastIndexOf(".");
        this.fileSuffix = fileName.substring(idx + 1);
        this.backupSize = backupSize;
    }

    public DynamicFileImpl(String file, int backupSize) {
        this(new File(file), backupSize);
    }

    public DynamicFileImpl(File file, int backupSize) {
        this(file.getParent(), file.getName(), backupSize);
    }


    /**
     * 检查文件是否存在
     *
     * @return 返回真，则存在
     * @throws IOException 异常
     */
    public boolean exists() throws IOException {
        versions(false);
        return lookupMaxVersion(false) > 0;
    }

    /**
     * 删除文件
     *
     * @throws IOException
     */
    public void delete() throws IOException {
        File dir = new File(directory + File.separator + fileName);
        FileUtils.deleteDirectory(dir);
        if (dir.exists()) {
            File deleteDir = new File(directory + File.separator + "~" + fileName + System.currentTimeMillis());
            dir.renameTo(deleteDir);
            FileUtils.deleteDirectory(deleteDir);
        }
        maxVersion = -1;
    }

    /**
     * 读取文件内容
     *
     * @return 字节数组
     * @throws IOException 异常
     */
    public ByteBuf read(long version) throws IOException {
        FileWrapper fileWrapper = getFile(version);
        return fileWrapper.read();
    }

    /**
     * 打开文件
     *
     * @return 输入流
     * @throws IOException 异常
     */
    public InputStream stream(long version) throws IOException {
        FileWrapper fileWrapper = getFile(version);
        return fileWrapper.stream();
    }

    @Override
    public long lookupMaxVersion() throws IOException {
        versions(false);
        return lookupMaxVersion(true);
    }

    public List<Long> versions(boolean create) throws IOException {
        final String filePath = directory + File.separator + fileName;
        final File dir = new File(filePath);
        if (!dir.exists()) {
            if (create) {
                dir.mkdirs();
            }
            return Collections.emptyList();
        }
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir0, String name) {
                try {
                    return dir.getCanonicalPath().equals(dir0.getCanonicalPath()) && (name.startsWith(fileName + ".") && !name.startsWith(fileName + ".temp."));
                } catch (IOException e) {
                    return false;
                }
            }
        });
        File[] tempFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir0, String name) {
                try {
                    return dir.getCanonicalPath().equals(dir0.getCanonicalPath()) && (name.startsWith(fileName + ".") && name.startsWith(fileName + ".temp."));
                } catch (IOException e) {
                    return false;
                }
            }
        });
        for (File tempFile : tempFiles) {
            long time = System.currentTimeMillis() - tempFile.lastModified();
            if (time > timeoutSec * 1000) {
                log.debug("temp file '{}' is timeout '{}' ms", tempFile.getCanonicalFile(), time);
                tempFile.delete();
            }
        }
        Long[] fileTsArray = new Long[files.length];
        int i = 0;
        for (File file0 : files) {
            if (log.isDebugEnabled()) {
                log.debug("search file : {}", file0);
            }
            String fileName0 = file0.getName();
            int idx = fileName0.lastIndexOf(".");
            if (idx < 0) {
                continue;
            }
            String ts = fileName0.substring(idx + 1);
            long timeStamp = 0L;
            try {
                timeStamp = Long.valueOf(ts);
                fileTsArray[i] = timeStamp;
                i++;
            } catch (Exception e) {
                log.error("getRealFile happens error!", e);
                continue;
            }
            if (timeStamp > maxVersion) {
                maxVersion = timeStamp;
            }
        }
        fileTsArray = Arrays.copyOf(fileTsArray, i);
        //升序排列
        Arrays.sort(fileTsArray);
        for (int j = 0; j < fileTsArray.length; j++) {
            if (j < fileTsArray.length - backupSize) {
                File deleteFile = new File(dir, fileName + "." + fileTsArray[j]);
                if (log.isDebugEnabled()) {
                    log.debug("auto delete backup file : {}", deleteFile);
                }
                deleteFile.delete();
            }
        }
        return Arrays.asList(fileTsArray);
    }

    @Override
    public List<Long> versions() throws IOException {
        return versions(false);
    }

    @Override
    public FileWrapper getFile(long version) throws IOException {
        File file = openVersion(version);
        return new FileWrapperImpl(file, version);
    }

    @Override
    public FileTransaction begin() throws IOException {
        FileTransaction transaction = new FileTransactionImpl(UUID.randomUUID().toString(), this);
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    File openVersion(long version) {
        File dir = new File(directory + File.separator + fileName);
        File file = new File(dir, fileName + "." + version);
        return file;
    }

    /**
     * 打扫动态文件
     *
     * @param notFoundThrowEx 未发现是否抛异常
     * @return 最新的文文件名
     * @throws IOException 异常
     */
    long lookupMaxVersion(boolean notFoundThrowEx) throws IOException {
        if (maxVersion <= 0) {
            if (notFoundThrowEx) {
                throw new FileNotFoundException("open dynamicFile '" + directory + "\\" + fileName + "' not found!");
            } else {
                return -1;
            }
        }
        return maxVersion;
    }
}
