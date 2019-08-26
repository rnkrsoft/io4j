package com.rnkrsoft.io.buffer;

/**
 * 字符缓存
 */
public class CharacterBuffer {
    char[] data;
    int writeBegin;
    int writeEnd;
    int readBegin;
    int readEnd;
    boolean readonly;
    boolean autoExpand;

    public CharacterBuffer(char[] data) {
        this.data = data;
        this.writeBegin = 0;
        this.writeEnd = data.length;
        this.readBegin = 0;
        this.readEnd = data.length;
    }

    public CharacterBuffer(int size) {
        this.data = new char[size];
        this.writeBegin = 0;
        this.writeEnd = data.length;
        this.readBegin = 0;
        this.readEnd = 0;
    }


    void expendData(int newCapacity) {
        char[] temp = new char[newCapacity];
        if (newCapacity > data.length) {
            System.arraycopy(data, 0, temp, 0, data.length);
        } else {
            System.arraycopy(data, 0, temp, 0, newCapacity);
            this.readEnd = newCapacity - 1;
        }
        this.data = temp;
        this.writeEnd = newCapacity - 1;
    }

    public int capacity() {
        return data.length;
    }

    public CharacterBuffer capacity(int newCapacity) {
        expendData(newCapacity);
        return this;
    }

    public boolean isAutoExpand() {
        return this.autoExpand;
    }

    public CharacterBuffer autoExpand(boolean expand) {
        this.autoExpand = expand;
        return this;
    }

    public boolean isReadOnly() {
        return readonly;
    }

    public CharacterBuffer readOnly(boolean readOnly) {
        this.readonly = readOnly;
        return this;
    }

    public CharacterBuffer clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
        this.writeBegin = 0;
        this.writeEnd = data.length;
        this.readBegin = 0;
        this.readEnd = 0;
        return this;
    }

    public boolean readyRead() {
        return this.readEnd > this.readBegin;
    }

    public CharacterBuffer resetWrite() {
        this.writeBegin = 0;
        return this;
    }

    public CharacterBuffer resetRead() {
        this.readBegin = 0;
        return this;
    }

    public int readableLength() {
        return this.readEnd - this.readBegin;
    }

    public int writableLength() {
        return this.writeEnd - this.writeBegin;
    }

    public CharacterBuffer put(char v) {
        if (readonly) {
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (writableLength() < 1) {
            if (this.autoExpand) {
                double newCap = Math.round((double) this.data.length * 1.75);
                expendData(Math.max((int) newCap, this.data.length + 1));
            } else {
                throw new ArrayIndexOutOfBoundsException("buffer is out of bounds!");
            }
        }
        this.data[writeBegin] = v;
        writeBegin++;
        readEnd++;
        return this;
    }

    public CharacterBuffer put(char[] chars) {
        if (this.readonly) {
            throw new IllegalArgumentException("buffer is readonly!");
        }
        if (writableLength() < 1) {
            if (this.autoExpand) {
                double newCap = Math.round((double) this.data.length * 1.75);
                expendData(Math.max((int) newCap, this.data.length + 1));
            } else {
                throw new ArrayIndexOutOfBoundsException("buffer is out of bounds!");
            }
        }
        System.arraycopy(chars, 0, this.data, this.writeBegin, chars.length);
        this.writeBegin += chars.length;
        this.readEnd += chars.length;
        return this;
    }

    public CharacterBuffer put(String v) {
        char[] chars = v.toCharArray();
        return put(chars);
    }

    public CharacterBuffer get(char[] data) {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        int length = data.length;
        if (readableLength() < length) {
            throw new ArrayIndexOutOfBoundsException("buffer is out of bounds!");
        }
        System.arraycopy(this.data, this.readBegin, data, 0, length);
        this.readBegin += length;
        return this;
    }

    public CharacterBuffer slice(int begin, int length){
        char[] data = new char[length];
        System.arraycopy(this.data, begin, data, 0, length);
        return new CharacterBuffer(data);
    }

    public char getChar() {
        char[] data = new char[1];
        get(data);
        return data[0];
    }

    public char[] getChars(int length) {
        char[] data = new char[length];
        get(data);
        return data;
    }

    public String getString(int length) {
        char[] data = new char[length];
        get(data);
        String string = new String(data);
        return string;
    }
}
