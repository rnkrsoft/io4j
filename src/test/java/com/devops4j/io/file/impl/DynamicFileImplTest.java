package com.devops4j.io.file.impl;

import com.devops4j.io.buffer.ByteBuf;
import com.devops4j.io.file.DynamicFile;
import com.devops4j.io.file.FileTransaction;
import com.devops4j.io.file.FileWrapper;
import com.devops4j.time.DateStyle;
import com.devops4j.time.FastDate;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by devops4j on 2018/2/15.
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
        ByteBuf byteBuf1 = fileTransaction.read();
        System.out.println(byteBuf1.asString("UTF-8"));
        txId = fileTransaction.getTransactionId();
        FileTransaction fileTransaction1 = dynamicFile.getTransaction(txId);
        Assert.assertEquals(false, fileTransaction1.isFinished());
        fileTransaction.commit();
        Assert.assertEquals(true, fileTransaction1.isFinished());
        System.out.println("-------------------");
        ByteBuf byteBuf = fileTransaction.read();
        System.out.println(byteBuf.asString("UTF-8"));
        FileWrapper fileWrapper = dynamicFile.getFile();
        System.out.println(fileWrapper.getVersion());
        System.out.println(fileWrapper.read().asString("UTF-8"));
    }
}