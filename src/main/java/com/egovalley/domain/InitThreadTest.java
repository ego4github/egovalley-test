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
        if (StringUtils.isNotBlank(testKey1) && !"null".equals(testKey1) &&
                StringUtils.isNotBlank(testKey2) && !"null".equals(testKey2)) {
            int endFlag = 1;
            while (true) {
                if (endFlag > 3) {
                    logger.info(">>> さょぅなち");
                    break;
                }
                logger.info(">>> " + endFlag + ": key1 = " + testKey1 + "; key2 = " + testKey2);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                endFlag++;
            }
        } else {
            logger.info(">>> 我莫得感情");
        }
    }
}
