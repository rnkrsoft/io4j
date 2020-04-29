package com.rnkrsoft.io._3rd.org.jctools.util;

@InternalAPI
public interface UnsafeJvmInfo {
    int PAGE_SIZE = UnsafeAccess.UNSAFE.pageSize();
}
