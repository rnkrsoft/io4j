package com.rnkrsoft.io.file;

import java.io.IOException;

/**
 * Created by rnkrsoft.com on 2018/2/16.
 */
public class TransactionAlreadyFinishedException extends IOException{
    public TransactionAlreadyFinishedException(String message) {
        super(message);
    }
}
