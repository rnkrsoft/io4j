package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.util.Hex;
import junit.framework.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by rnkrsoft.com on 2019/10/31.
 */
public class ByteBuffersTest {

    @Test
    public void testNewUnpooledHeapByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 16, 48);
        buffer.writeStringUTF8("0123456789ABCDEF");
        Assert.assertEquals(16,  buffer.readableBytesLength());
        System.out.println(buffer.readStringUTF8(buffer.readableBytesLength()));
        System.out.println(Hex.toDisplayString(buffer.nioBuffer().array()));
    }

    @Test
    public void testNewUnpooledDirectByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffers.newBuffer(ByteBufferType.DIRECT, false, 16, 48);
        buffer.writeStringUTF8("0123456789ABCDEF");
        Assert.assertEquals(16,  buffer.readableBytesLength());
        InputStream is = buffer.asInputStream();
        int len = 0;
        while ((len = is.available()) > 0){
            byte[] buf = new byte[Math.min(1024, len)];
            is.read(buf);
            System.out.println(new String(buf));
        }
//        System.out.println(buffer.readStringUTF8(buffer.readableBytesLength()));
    }

    @Test
    public void testNewByteBuffer() throws Exception {
        Assert.assertTrue(ByteBuffers.newBuffer(16, 48) instanceof UnpooledHeapByteBuffer);
        ByteBuffers.DEFAULT_BUFFER_TYPE = ByteBufferType.DIRECT;
        Assert.assertTrue(ByteBuffers.newBuffer(16, 48) instanceof UnpooledDirectByteBuffer);
    }
}