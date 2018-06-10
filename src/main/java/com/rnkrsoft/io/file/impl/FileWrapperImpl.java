package com.rnkrsoft.io.file.impl;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.io.file.FileWrapper;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by woate on 2018/2/16.
 */
class FileWrapperImpl implements FileWrapper {

    @Getter
    File file;

    @Getter
    long version;

    public FileWrapperImpl(File file, long version) {
        this.file = file;
        this.version = version;
    }

    @Override
    public ByteBuf read() throws IOException {
        InputStream is = null;
        try {
            is =new FileInputStream(file);
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
        return read().asInputStream();
    }

    @Override
    public void delete() throws IOException {
        file.delete();
    }
}
