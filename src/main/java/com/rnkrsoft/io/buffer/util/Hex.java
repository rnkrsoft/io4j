package com.rnkrsoft.io.buffer.util;


import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by rnkrsoft on 2019/10/30.
 */
public class Hex {
    static final String SEPARATOR = System.getProperty("line.separator");

    /**
     * 向标准输出流输出字节数组
     * @param array 字节数组
     * @throws IOException IO异常
     */
    public static void stdout(byte[] array) throws IOException {
        out(array, ((OutputStream) (System.out)));
    }
    /**
     * 向标准输出流输出字节数组
     * @param title 标题
     * @param array 字节数组
     * @throws IOException IO异常
     */
    public static void stdout(String title, byte[] array) throws IOException {
        out(title, array, ((OutputStream) (System.out)));
    }

    /**
     * 向输出流输出字节数组
     * @param array 字节数组
     * @param out 输出流
     * @throws IOException IO异常
     */
    public static void out(byte[] array, OutputStream out) throws IOException {
        out.write(toDisplayString(array).getBytes());
    }

    /**
     * 向输出流输出字节数组
     * @param title 标题
     * @param array 字节数组
     * @param out 输出流
     * @throws IOException IO异常
     */
    public static void out(String title, byte[] array, OutputStream out) throws IOException {
        out.write(toDisplayString(title, array).getBytes());
    }
    /**
     * 将字节数组输出为16进制字符串,并且格式化
     *
     * @param array 输如字节数组
     * @return 显示字符串
     */
    public static String toDisplayString(byte[] array) {
        return toDisplayString(null, array, 0, array.length);
    }
    /**
     * 将字节数组输出为16进制字符串,并且格式化
     *
     * @param title 标题
     * @param array 输如字节数组
     * @return 显示字符串
     */
    public static String toDisplayString(String title, byte[] array) {
        return toDisplayString(title, array, 0, array.length);
    }

    static String fill(String in, char fill, int len) {
        String str = in;
        while (str.length() < len) {
            str = fill + str + fill;
        }
        return str;
    }
    /**
     * 将字节数组输出为16进制字符串,并且格式化
     *
     * @param array  带输出字节数组
     * @param offset 起始偏移量
     * @param length 输出长度
     * @return 显示字符串
     */
    public static String toDisplayString(String title, byte array[], int offset, int length) {
        StringBuffer buffer = new StringBuffer(length);
        int maxLen = 77;
        array = array == null ? EMPTY : array;
        title = title == null ? "" : title;
        String head = fill(title + " [begin]", '=', maxLen);
        String foot = fill(title + " [end]", '=', maxLen);
        buffer.append(head);
        buffer.append(SEPARATOR);
        buffer.append("-Displace-");
        buffer.append("  ");
        for (int i = 0; i < 16; i++) {
            buffer.append("-").append(Integer.toHexString(i).toUpperCase()).append("-");
        }
        buffer.append(" ");
        buffer.append("---ASCII CODE---");
        buffer.append(SEPARATOR);
        for (int i = offset; i < length; i += 16) {
            int splitLen = length - i < 16 ? length - i : 16;
            String position16 = Integer.toHexString(i - offset).toUpperCase();
            String position = Integer.toString(i - offset).toUpperCase();
            //输出左侧的行首偏移地址
            for (int j = position16.length(); j < 4; j++)
                buffer.append('0');

            buffer.append(position16);
            buffer.append("(");
            for (int j = position.length(); j < 4; j++)
                buffer.append('0');
            buffer.append(position);

            buffer.append(")");
            buffer.append("  ");
            //输出中间的16进制数据
            for (int j = 0; j < 16; j++) {
                if (j < splitLen) {
                    int j1 = array[j + i] & 255;
                    String s1 = Integer.toHexString(j1).toUpperCase();
                    if (s1.length() == 1)
                        buffer.append("0");
                    buffer.append(s1);
                    buffer.append(" ");
                } else {
                    buffer.append("   ");
                }
            }

            buffer.append(" ");
            //输出右边的字符数据
            for (int j = 0; j < 16; j++)
                if (j >= splitLen) {
                    buffer.append(" ");
                } else {
                    char c = (char) (array[j + i] & 255);
                    if (c >= '!' && c <= '~')
                        buffer.append(c);
                    else
                        buffer.append(".");
                }

            buffer.append(SEPARATOR);
        }
        buffer.append(foot);
        buffer.append(SEPARATOR);
        return buffer.toString();
    }

    /**
     * 字节数组显示为16进制字符串
     *
     * @param array 字节数组
     * @return 显示字符串
     */
    public static String toHexString(byte[] array) {
        StringBuffer buff = new StringBuffer(array.length * 2);
        for (int i = 0; i < array.length; i++) {
            buff.append(HEX_STRING.charAt(array[i] >> 4 & 15));
            buff.append(HEX_STRING.charAt(array[i] & 15));
        }
        return buff.toString();
    }

    /**
     * 格式化16进制字符串为字节数组
     *
     * @param hex 16进制字符串
     * @return 字节数组
     */
    public static byte[] fromHexString(String hex) {
        byte ret[] = new byte[hex.length() / 2];
        int i = 0;
        int j = 0;
        while (i < hex.length()) {
            ret[j++] = (byte) (offset(hex.charAt(i++)) << 4 | offset(hex.charAt(i++)));
        }
        return ret;
    }

    static int offset(char c) {
        c = Character.toUpperCase(c);
        int i = c < '0' || c > '9' ? c < 'A' || c > 'F' ? -1 : (c - 65) + 10 : c - 48;
        if (i < 0) {
            throw new IllegalArgumentException("Invalid Hex Char.");
        } else {
            return i;
        }
    }

    public static final byte[] EMPTY = new byte[0];
    private static final String HEX_STRING = "0123456789ABCDEF";
}
