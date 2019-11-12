package com.rnkrsoft.io.buffer;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/10/31.
 */
public class ByteBuffersTest {

    @Test
    public void testNewUnpooledHeapByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 16, 48);
        buffer.writeStringUTF8("0123456789ABCDEF");
        Assert.assertEquals(48,  buffer.readableBytesLength());
        System.out.println(buffer.readStringUTF8(buffer.readableBytesLength()));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testNewUnpooledDirectByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffers.newBuffer(ByteBufferType.DIRECT, false, 16, 48);
        buffer.writeStringUTF8("0123456789ABCDEF");
        Assert.assertEquals(48,  buffer.readableBytesLength());
        System.out.println(buffer.readStringUTF8(buffer.readableBytesLength()));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testNewByteBuffer() throws Exception {
        Assert.assertTrue(ByteBuffers.newBuffer(16, 48) instanceof UnpooledHeapByteBuffer);
        ByteBuffers.DEFAULT_BUFFER_TYPE = ByteBufferType.DIRECT;
        Assert.assertTrue(ByteBuffers.newBuffer(16, 48) instanceof UnpooledDirectByteBuffer);
    }
}