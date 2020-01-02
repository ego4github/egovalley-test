package com.egovalley.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VerifyTokenUtils {

    /**
     * 创建SHA加密token
     *
     * @param secretKey   约定密钥
     * @param randomCode  每次交互随机生成
     * @param currentTime 密钥生成时间
     * @return token
     */
    public static String createTokenBySHA(String secretKey, String randomCode, long currentTime) {
        String strVal = secretKey + randomCode + currentTime;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(strVal.getBytes("GBK"));// 此处编码sourceId如果没有中文, 则生成token一致, 如果有中文, 则需要商定编码方式
            byte[] byteVal = md.digest();
            StringBuilder sb = new StringBuilder();// StringBuffer线程安全(多线程用), StringBuilder效率更高(单线程用)
            for (byte aByteVal : byteVal) {
                String hex = Integer.toHexString(aByteVal & 0xFF);
                if (hex.length() == 1) {
                    hex += "0";
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void getToken(String sourceId) {
        long currentTime = System.currentTimeMillis() + 1000 * 60 * 10;
//        long currentTime = Long.parseLong("1576723725519");
        String token = createTokenBySHA("secretKey", sourceId, currentTime);
        System.out.println("token = " + token);
    }

    public static void main(String[] args) {
//        String sourceId = "9998877中文abcdefg";
//        getToken(sourceId);

        String randomCode = RandomStringUtils.randomAlphanumeric(16);
        System.out.println("randomCode = " + randomCode);
        long startTime = System.currentTimeMillis() + 998 * 66 * 3;
        String token1 = createTokenBySHA("secretKey", randomCode, startTime);
        System.out.println("token1 = " + token1);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis() + 998 * 66 * 3;
        String token2 = createTokenBySHA("secretKey", randomCode, startTime);
        System.out.println("token2 = " + token2);
        long costTime = (endTime - startTime) / 1000;
        System.out.println("costTime = " + costTime);
        if (costTime > 3) {
            System.out.println("请求超时");
        } else if (!token2.equals(token1)) {
            System.out.println("签名不一致");
        } else {
            System.out.println("请求通过");
        }
    }

}
