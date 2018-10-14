package com.rnkrsoft.io.file;

import java.io.IOException;

/**
 * Created by devops4j on 2018/2/16.
 */
public class TransactionNotFoundException extends IOException{
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
