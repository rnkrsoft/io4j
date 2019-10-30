package com.rnkrsoft.io.buffer;

import java.nio.charset.Charset;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
class UnpooledHeapByteBuffer extends UnpooledByteBuffer {
    boolean autoExpand;
    byte[] array;
    java.nio.ByteBuffer tmpNioBuf;

    UnpooledHeapByteBuffer(int initialCapacity, int maxCapacity) {
       this(new byte[initialCapacity],  0, 0, maxCapacity);
    }

    UnpooledHeapByteBuffer(byte[] initialArray, int readerIndex, int writerIndex, int maxCapacity){
        super(maxCapacity);
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity(%d) > maxCapacity(%d)", initialArray.length, maxCapacity));
        }

        setArray(initialArray);
        setIndex(readerIndex, writerIndex);
    }


    @Override
    public boolean isAutoExpand() {
        return autoExpand;
    }

    @Override
    public ByteBuffer autoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
        return this;
    }

    public int capacity() {
        return array.length;
    }

    public ByteBuffer capacity(int newCapacity) {
        if (newCapacity < 0 || newCapacity > maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity);
        }

        int oldCapacity = array.length;
        if (newCapacity > oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            System.arraycopy(array, 0, newArray, 0, array.length);
            setArray(newArray);
        } else if (newCapacity < oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            int readerIndex = readerIndex();
            if (readerIndex < newCapacity) {
                int writerIndex = writerIndex();
                if (writerIndex > newCapacity) {
                    writerIndex(writerIndex = newCapacity);
                }
                System.arraycopy(array, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
            } else {
                setIndex(newCapacity, newCapacity);
            }
            setArray(newArray);
        }
        return this;
    }

    public boolean isDirect() {
        return false;
    }


    @Override
    protected void deallocate() {

    }

    public boolean hasArray() {
        return true;
    }

    private void setArray(byte[] initialArray) {
        array = initialArray;
        tmpNioBuf = null;
    }

    public ReferenceCounted touch(Object hint) {
        return null;
    }

    @Override
    public String readString(int length, Charset charset) {
        return null;
    }
}
