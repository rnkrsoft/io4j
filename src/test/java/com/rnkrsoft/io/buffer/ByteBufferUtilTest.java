package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.util.DiskSizeUnit;
import com.rnkrsoft.io.buffer.util.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by woate on 2019/11/13.
 */
public class ByteBufferUtilTest {

    @Test
    public void testPrettyHexDump() throws Exception {
        ByteBuffer buffer = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 16, DiskSizeUnit.nMB(16));
        for (int i = 0; i < 1024; i++) {
            buffer.writelnStringUTF8("this is a test");
        }
        System.out.println(ByteBufferUtil.prettyHexDump(buffer));
        System.out.println(Hex.toDisplayString(buffer.array()));
    }

    @Test
    public void testPrettyHexDump1() throws Exception {

    }
}