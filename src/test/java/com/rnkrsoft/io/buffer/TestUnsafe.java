package com.rnkrsoft.io.buffer;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by rnkrsoft.com on 2019/11/1.
 */
public class TestUnsafe {

    static class Demo{
        String name;
        int age;
    }
    @Test
    public void testGetLong() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        Demo demo1 = new Demo();
        Demo demo2 = new Demo();
        Field nameField = Demo.class.getDeclaredField("name");
        Field ageField = Demo.class.getDeclaredField("age");
        long offset1 = unsafe.objectFieldOffset(nameField);
        long offset2 = unsafe.objectFieldOffset(ageField);
        System.out.println(unsafe.getLong(demo1, offset1));
        System.out.println(unsafe.getLong(demo1, offset2));
        System.out.println(unsafe.getLong(demo2, offset1));
        System.out.println(unsafe.getLong(demo2, offset2));
        System.out.println(unsafe.getLong(demo1, offset2));
        System.out.println(unsafe.getLong(demo2, offset2));
    }
}
