package com.egovalley.common;

import com.egovalley.domain.InitThreadTest;
import com.egovalley.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Lazy(value = false)
@Service
public class InitThreadManager {

    private static final Logger logger = LoggerFactory.getLogger(InitThreadManager.class);

    @Value("${init.thread.key1}")
    private String initThreadKey1;
    @Value("${init.thread.key2}")
    private String initThreadKey2;

    @PostConstruct
    private void initThread() {
        logger.info(">>> 开始初始化线程任务...");
        logger.info(">>> 综合测试线程...");
        new Thread(new InitThreadTest(initThreadKey1, initThreadKey2)).start();
        logger.info(">>> 异步写文件线程...");
        ByteUtils.executorService = Executors.newCachedThreadPool();
        logger.info(">>> 初始化线程任务完成...");
    }

}
