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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key1", "value11");
        paramMap.put("key2", "value22");
        new Thread(new InitThreadTest(initThreadKey1, initThreadKey2)).start();
//        new Thread(new InitThreadTest("" + paramMap.get("key1"), "" + paramMap.get("key3"))).start();
        logger.info(">>> 初始化线程任务完成...");

        logger.info(">>> 来一个写文件的线程");
        ByteUtils.executorService = Executors.newCachedThreadPool();
        logger.info(">>> 来好了");
    }

}
