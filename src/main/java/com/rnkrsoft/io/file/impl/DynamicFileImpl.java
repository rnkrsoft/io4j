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
        versions();
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
        versions();
        return lookupMaxVersion(true);
    }

    @Override
    public List<Long> versions() throws IOException {
        final String filePath = directory + File.separator + fileName;
        final File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
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
