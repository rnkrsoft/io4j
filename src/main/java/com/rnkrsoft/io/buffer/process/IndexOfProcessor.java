package com.rnkrsoft.io.buffer.process;

public class IndexOfProcessor implements ByteProcessor {
    private final byte byteToFind;

    public IndexOfProcessor(byte byteToFind) {
        this.byteToFind = byteToFind;
    }

    public boolean process(byte value) {
        return value != byteToFind;
    }
}
