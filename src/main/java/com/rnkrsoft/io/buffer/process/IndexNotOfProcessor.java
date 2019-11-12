package com.rnkrsoft.io.buffer.process;

public class IndexNotOfProcessor implements ByteProcessor {
    private final byte byteToNotFind;

    public IndexNotOfProcessor(byte byteToNotFind) {
        this.byteToNotFind = byteToNotFind;
    }

    public boolean process(byte value) {
        return value != byteToNotFind;
    }
}