//package com.rnkrsoft.io.buffer;
//
//import com.rnkrsoft.io.ByteBuffer;
//import com.rnkrsoft.io.Hex;
//import org.junit.Test;
//
///**
// * Created by woate on 2019/11/12.
// */
//public class ByteBufferTest {
//
//    @Test
//    public void testCopy() throws Exception {
//        ByteBuffer buffer = ByteBuffers.newBuffer(ByteBufferType.HEAP, false, 16, 48);
//        ByteBuffer copy = buffer.copy();
//        copy.writelnStringUTF8("Hello world!");
//        System.out.println(copy);
//        System.out.println(buffer);
//        System.out.println(Hex.toDisplayString(copy.array()));
//        System.out.println(Hex.toDisplayString(buffer.array()));
//    }
//
//    @Test
//    public void testCopy1() throws Exception {
//
//    }
//
//    @Test
//    public void testSlice() throws Exception {
//
//    }
//
//    @Test
//    public void testSlice1() throws Exception {
//
//    }
//}