package com.egovalley.domain;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitThreadTest implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(InitThreadTest.class);

    private String testKey1;
    private String testKey2;

    public InitThreadTest(String testKey1, String testKey2) {
        this.testKey1 = testKey1;
        this.testKey2 = testKey2;
    }

    @Override
    public void run() {
        logger.info("初始化测试线程...");
        if (StringUtils.isNotBlank(testKey1) && !"null".equals(testKey1) &&
                StringUtils.isNotBlank(testKey2) && !"null".equals(testKey2)) {
            int endFlag = 1;
            while (true) {
                if (endFlag > 10) {
                    System.out.println("这里有点不一样");
                    System.out.println("再来点");
                    System.out.println("再来点222");
                    System.out.println("さょぅなち");
                    break;
                }
                System.out.println(endFlag + ": testKey1 = " + testKey1 + "; testKey2 = " + testKey2);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                endFlag++;
            }
        } else {
            System.out.println("我莫得感情");
        }
    }
}
