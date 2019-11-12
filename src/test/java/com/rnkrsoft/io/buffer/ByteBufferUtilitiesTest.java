//package com.rnkrsoft.io.buffer;
//
//import junit.framework.Assert;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
///**
// * Created by rnkrsoft.com on 2019/11/12.
// */
//public class ByteBufferUtilitiesTest {
//
//    @Test
//    public void testCalcNewCapacity() throws Exception {
//        Assert.assertEquals(8, ByteBufferUtilities.calcNewCapacity(4, 4, 2, 16));
//        Assert.assertEquals(4 * 1024 * 1024, ByteBufferUtilities.calcNewCapacity(4 * 1024 * 1023, 4 * 1024 * 1024 - 1, 2, 4 * 1024 * 1024));
//        Assert.assertEquals(4 * 1024 * 1024 + 8, ByteBufferUtilities.calcNewCapacity(4 * 1024 * 1024, 4 * 1024 * 1024, 4, 8 * 1024 * 1024));
//        Assert.assertEquals(8 * 1024 * 1024 + 4, ByteBufferUtilities.calcNewCapacity(4 * 1024 * 1024, 8 * 1024 * 1024, 4, 16 * 1024 * 1024));
//    }
//}