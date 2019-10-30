package com.rnkrsoft.io.buffer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wing4j on 2019/10/30.
 */
public class UnpooledHeapByteBufferTest {

    @Test
    public void testCapacity() throws Exception {
        UnpooledHeapByteBuffer buffer = new UnpooledHeapByteBuffer(10, 50);
        buffer.writeStringUTF8("1234");
    }

    @Test
    public void testIsDirect() throws Exception {

    }
}