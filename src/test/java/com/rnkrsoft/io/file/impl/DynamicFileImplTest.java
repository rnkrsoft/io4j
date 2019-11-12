package com.rnkrsoft.io.file.impl;

import com.rnkrsoft.io.buffer.ByteBuffer;
import com.rnkrsoft.io.file.DynamicFile;
import com.rnkrsoft.io.file.FileTransaction;
import com.rnkrsoft.io.file.FileWrapper;
import com.rnkrsoft.time.DateStyle;
import com.rnkrsoft.time.FastDate;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by rnkrsoft.com on 2018/2/15.
 */
public class DynamicFileImplTest {

    @Test
    public void testExists() throws Exception {
        DynamicFile dynamicFile = DynamicFile.file("./target/demo/test.txt", 2);
        String txId = null;
        if (dynamicFile.exists()) {
            dynamicFile.delete();
        }
        Assert.assertEquals(false, dynamicFile.exists());
        FileTransaction fileTransaction = dynamicFile.begin();
        File file = fileTransaction.getFile();
        System.out.println(file);
        fileTransaction.write("this is a test" + new FastDate().toString(DateStyle.CHINESE_FORMAT1));
        System.out.println("-------------------");
        ByteBuffer byteBuf1 = fileTransaction.read();
        System.out.println(byteBuf1.getString(0, byteBuf1.readableBytesLength(), Charset.forName("UTF-8")));
        txId = fileTransaction.getTransactionId();
        FileTransaction fileTransaction1 = dynamicFile.getTransaction(txId);
        Assert.assertEquals(false, fileTransaction1.isFinished());
        fileTransaction.commit();
        Assert.assertEquals(true, fileTransaction1.isFinished());
        System.out.println("-------------------");
        ByteBuffer byteBuf = fileTransaction.read();
        System.out.println(byteBuf.getString(0, byteBuf1.readableBytesLength(), Charset.forName("UTF-8")));
        FileWrapper fileWrapper = dynamicFile.getFile();
        System.out.println(fileWrapper.getVersion());
        System.out.println(fileWrapper.read().getString(0, byteBuf1.readableBytesLength(), Charset.forName("UTF-8")));
    }
}