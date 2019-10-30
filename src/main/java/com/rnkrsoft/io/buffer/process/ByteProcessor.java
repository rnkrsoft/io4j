package com.rnkrsoft.io.buffer.process;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public interface ByteProcessor {
    /**
     * Aborts on a {@code NUL (0x00)}.
     */
    ByteProcessor FIND_NUL = new IndexOfProcessor((byte) 0);

    /**
     * Aborts on a non-{@code NUL (0x00)}.
     */
    ByteProcessor FIND_NON_NUL = new IndexNotOfProcessor((byte) 0);

    /**
     * Aborts on a {@code CR ('\r')}.
     */
    ByteProcessor FIND_CR = new IndexOfProcessor((byte) '\r');

    /**
     * Aborts on a non-{@code CR ('\r')}.
     */
    ByteProcessor FIND_NON_CR = new IndexNotOfProcessor((byte) '\r');

    /**
     * Aborts on a {@code LF ('\n')}.
     */
    ByteProcessor FIND_LF = new IndexOfProcessor((byte) '\n');

    /**
     * Aborts on a non-{@code LF ('\n')}.
     */
    ByteProcessor FIND_NON_LF = new IndexNotOfProcessor((byte) '\n');

    /**
     * Aborts on a {@code CR (';')}.
     */
    ByteProcessor FIND_SEMI_COLON = new IndexOfProcessor((byte) ';');

    /**
     * Aborts on a {@code CR ('\r')} or a {@code LF ('\n')}.
     */
    ByteProcessor FIND_CRLF = new ByteProcessor() {
        public boolean process(byte value) {
            return value != '\r' && value != '\n';
        }
    };

    /**
     * Aborts on a byte which is neither a {@code CR ('\r')} nor a {@code LF ('\n')}.
     */
    ByteProcessor FIND_NON_CRLF = new ByteProcessor() {
        public boolean process(byte value) {
            return value == '\r' || value == '\n';
        }
    };

    /**
     * Aborts on a linear whitespace (a ({@code ' '} or a {@code '\t'}).
     */
    ByteProcessor FIND_LINEAR_WHITESPACE = new ByteProcessor() {
        public boolean process(byte value) {
            return value != ' ' && value != '\t';
        }
    };

    /**
     * Aborts on a byte which is not a linear whitespace (neither {@code ' '} nor {@code '\t'}).
     */
    ByteProcessor FIND_NON_LINEAR_WHITESPACE = new ByteProcessor() {
        public boolean process(byte value) {
            return value == ' ' || value == '\t';
        }
    };

    boolean process(byte value) throws Exception;
}
