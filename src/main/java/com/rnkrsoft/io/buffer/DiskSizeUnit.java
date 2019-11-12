package com.rnkrsoft.io.buffer;

/**
 * Created by rnkrsoft.com on 2019/11/12.
 * 磁盘大小单位常量
 */
public abstract class DiskSizeUnit {
    public final static int KB = 1024;
    public final static int MB = 1024 * KB;
    public final static int GB = 1024 * MB;
    public final static int TB = 1024 * GB;
    public final static int PB = 1024 * TB;

    private DiskSizeUnit() {
    }

    public static int nKB(int size) {
        return size * KB;
    }

    public static int nMB(int size) {
        return size * MB;
    }

    public static int nGB(int size) {
        return size * GB;
    }

    public static int nTB(int size) {
        return size * TB;
    }

    public static int nPB(int size) {
        return size * KB;
    }
}
