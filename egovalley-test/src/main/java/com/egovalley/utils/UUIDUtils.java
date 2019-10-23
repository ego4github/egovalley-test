package com.egovalley.utils;

import java.util.UUID;

public class UUIDUtils {

    /**
     * 生成随机id
     *
     * @return
     */
    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public static String getSixNum() {
        return ((int) ((Math.random() * 9 + 1) * 100000)) + "";
    }

    public static void main(String[] args) {
        System.out.println(getId());
        System.out.println(getSixNum());
    }
}
