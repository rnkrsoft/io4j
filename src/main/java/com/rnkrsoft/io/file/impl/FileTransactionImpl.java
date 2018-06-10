package com.rnkrsoft.io.file.impl;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.io.file.DynamicFile;
import com.rnkrsoft.io.file.FileTransaction;
import com.devops4j.message.MessageFormatter;
import com.devops4j.time.DateStyle;
import com.devops4j.time.FastDateFormat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.UUID;

/**
 * Created by woate on 2018/2/15.
 */
@Slf4j
class FileTransactionImpl implements FileTransaction {
    final File tempFile;
    File realFile;
    @Getter
    final DynamicFile dynamicFile;
    @Getter
    final String transactionId;
    @Getter
    long lastActiveTime;
    boolean commit = false;
    boolean rollback = false;

    public FileTransactionImpl(String transactionId, DynamicFile dynamicFile) throws IOException {
        this.transactionId = transactionId;
        this.dynamicFile = dynamicFile;
        File dir = new File(dynamicFile.getDirectory() + File.separator + dynamicFile.getFileName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.tempFile = new File(dir, MessageFormatter.format("{}.temp.{}", dynamicFile.getFileName(), UUID.randomUUID().toString()));
        this.tempFile.createNewFile();
        this.lastActiveTime = System.currentTimeMillis();
    }

    @Override
    public void write(byte[] data) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
            fos.write(data);
            this.lastActiveTime = System.currentTimeMillis();
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
                fos = null;
            }
        }
    }

    @Override
    public void write(CharSequence charSequence) throws IOException {
        write(charSequence != null ? charSequence.toString().getBytes("UTF-8") : new byte[0]);
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
            byteBuf.write(fos);
            this.lastActiveTime = System.currentTimeMillis();
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
                fos = null;
            }
        }
    }

    @Override
    public ByteBuf read() throws IOException {
        InputStream is = null;
        try {
            is =new FileInputStream(getFile());
            ByteBuf byteBuf = ByteBuf.allocate(1024).autoExpand(true);
            byteBuf.read(is);
            return byteBuf;
        } finally {
            if (is != null) {
                is.close();
                is = null;
            }
        }
    }

    @Override
    public InputStream stream() throws IOException {
        File file = getFile();
        FileInputStream fis = new FileInputStream(file);
        return fis;
    }

    @Override
    public boolean isFinished() throws IOException {
        return commit || rollback;
    }

    @Override
    public boolean commit() throws IOException {
        File dir = new File(dynamicFile.getDirectory() + File.separator + dynamicFile.getFileName());
        try {
            if (!tempFile.exists()) {
                log.error("temp file '{}' not exist", tempFile);
                return false;
            }
            String file0 = dynamicFile.getFileName() + "." + new FastDateFormat().format(System.currentTimeMillis(), DateStyle.FILE_FORMAT3);
            realFile = new File(dir, file0);
            log.debug("commit {} file to {}", tempFile, realFile);
            FileUtils.copyFile(tempFile, realFile);
            FileUtils.deleteQuietly(tempFile);
            if (tempFile.exists()) {
                throw new IOException(MessageFormatter.format("delete temp file {} fail!", tempFile));
            }
            log.debug("commit {} file to {} ok", tempFile, realFile);
            this.lastActiveTime = System.currentTimeMillis();
            this.commit = true;
            this.dynamicFile.destroy(transactionId);
            return true;
        } catch (FileNotFoundException e) {
            log.error("save file happens error!", e);
            throw e;
        } catch (Exception e) {
            log.error("save file happens error!", e);
            return false;
        }
    }

    @Override
    public boolean rollback() throws IOException {
        try {
            if (!tempFile.exists()) {
                log.error("temp file '{}' not exist", tempFile);
                return false;
            }
            log.debug("rollback {} file", tempFile);
            FileUtils.forceDelete(tempFile);
            if (tempFile.exists()) {
                throw new IOException(MessageFormatter.format("delete temp file {} fail!", tempFile));
            }
            log.debug("rollback {} file ok ", tempFile);
            this.lastActiveTime = System.currentTimeMillis();
            this.rollback = true;
            this.dynamicFile.destroy(transactionId);
            return true;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("save file happens error!", e);
            return false;
        }
    }


    @Override
    public File getFile() throws IOException {
        if (isFinished()) {
            //访问文件的真实文件
            if(rollback){
                throw new FileNotFoundException(MessageFormatter.format("file {} has rollback!", tempFile));
            }
            return realFile;
        } else {
            return tempFile;
        }
    }
}
