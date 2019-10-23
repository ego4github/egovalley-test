package com.egovalley.web.job;

import com.egovalley.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class TaskJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskJob.class);

    @Value("${count.daily.path}")
    private String dailyPath;
    @Value("${count.total.path}")
    private String totalPath;

    /**
     * 每日0点更新总访问量, 并将今日访问量清零
     */
    @Scheduled(cron = "${total.count.time}")
    private void addTotalCount() {
        IOUtils.addCount(totalPath, "" + (Integer.parseInt(IOUtils.readCount(dailyPath)) + Integer.parseInt(IOUtils.readCount(totalPath))));
        IOUtils.addCount(dailyPath, "0");
    }

    /**
     * 配置文件定时任务测试
     * 冒号后面是给cron表达式的默认值, 当配置文件无值的时候, 以默认值执行, 配置文件有值则以配置文件为准
     * 不给默认值, 也可以
     * 如果配置文件中没有这个key, 这里又没有给cron默认值, 则会读取不到配置文件值的报错
     */
    @Scheduled(cron = "${prop.test.time:0 0/10 * * * ?}")
    public void propertiesTest() {
        logger.info(">>> keep alive...");
    }

}
