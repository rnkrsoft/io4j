package com.rnkrsoft.io.buffer;

import com.rnkrsoft.io.buffer.util.Hex;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/10/31.
 */
public class HexTest {

    @Test
    public void testStdout() throws Exception {
        Hex.stdout("test", "0123456789012345".getBytes());
        Hex.stdout("test","01234567890123456".getBytes());
    }

    @Test
    public void testWrite1() throws Exception {

    }

    @Test
    public void testToDisplayString() throws Exception {

    }

    @Test
    public void testToDisplayString1() throws Exception {

    }

    @Test
    public void testToHexString() throws Exception {

    }

    @Test
    public void testFromHexString() throws Exception {

    }
}