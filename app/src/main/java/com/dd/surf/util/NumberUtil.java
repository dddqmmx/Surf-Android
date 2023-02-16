package com.dd.surf.util;

public class NumberUtil {
    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= bytes[i] & 0xFF;
        }
        return result;
    }
}
