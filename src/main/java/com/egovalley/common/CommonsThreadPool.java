package com.egovalley.common;

import com.egovalley.service.PoolTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class CommonsThreadPool {

    private static final Logger logger = LoggerFactory.getLogger(CommonsThreadPool.class);
    private static CommonsThreadPool commonsThreadPool;
    private LinkedBlockingDeque<Object> linkedBlockingDeque;
    private ExecutorService threadInPool;
    private ExecutorService threadOutPool;
    private ThreadPoolExecutor threadDataPool;
    private Integer threadOutPoolCount = 0;

    @Value("${commonsThreadPool.dequePoolSize}")
    private String dequePoolSize;
    @Value("${commonsThreadPool.threadInSize}")
    private String threadInSize;
    @Value("${commonsThreadPool.threadOutSize}")
    private String threadOutSize;
    @Value("${commonsThreadPool.threadDataSize}")
    private String threadDataSize;
    @Value("${commonsThreadPool.inPoolLimitMillisecond}")
    private String inPoolLimitMillisecond;

    @Autowired
    private PoolTestService poolTestService;

    @PostConstruct
    public void createLinkedBlockingDeque() {
        commonsThreadPool = this;
        commonsThreadPool.setLinkedBlockingDeque(new LinkedBlockingDeque<>(Integer.valueOf(dequePoolSize)));
        commonsThreadPool.setThreadInPool(Executors.newFixedThreadPool(Integer.valueOf(threadInSize)));
        commonsThreadPool.setThreadOutPool(Executors.newFixedThreadPool(Integer.valueOf(threadOutSize)));
        commonsThreadPool.setThreadDataPool((ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.valueOf(threadDataSize)));
//        commonsThreadPool.outPoolRemoveLast();
    }

    // 获取池对象
    public static CommonsThreadPool getCommonsThreadPool() {
        return commonsThreadPool;
    }

    private void addFirst(Object object, LinkedBlockingDeque<Object> deque) {
        try {
            deque.putFirst(object);
        } catch (InterruptedException e) {
            logger.error(">>> addFirst 链表已满", e);
            outPoolRemoveLast();
        }
    }

    private void addLast(Object object) {
        try {
            linkedBlockingDeque.putLast(object);
        } catch (InterruptedException e) {
            logger.error(">>> addLast 链表已满", e);
            outPoolRemoveFirst();
        }
    }

    private Object removeFirst() {
        Object object = null;
        try {
            object = linkedBlockingDeque.takeFirst();
        } catch (InterruptedException e) {
            logger.error(">>> removeFirst 异常", e);
        }
        return object;
    }

    private Object removeLast() {
        Object object = null;
        try {
            object = linkedBlockingDeque.takeLast();
        } catch (InterruptedException e) {
            logger.error(">>> removeLast 异常", e);
        }
        return object;
    }

    public void inPoolAddFirst(Object object, LinkedBlockingDeque<Object> deque) {
        if (object == null) {
            logger.info(">>> inPoolAddFirst object is null");
            return;
        }
        if (deque == null) {
            logger.info(">>> inPoolAddFirst deque is null");
            return;
        }
//        try {
//            Thread.sleep(Integer.valueOf(inPoolLimitMillisecond));
//        } catch (InterruptedException e) {
//            logger.error(">>> inPoolAddFirst sleep 异常", e);
//        }
//        threadInPool.execute(() -> commonsThreadPool.addFirst(object, deque));
        commonsThreadPool.addFirst(object, deque);
    }

    public void inPoolAddLast(Object object) {
        if (object == null) {
            logger.info(">>> inPoolAddLast object is null");
            return;
        }
        try {
            Thread.sleep(Integer.valueOf(inPoolLimitMillisecond));
        } catch (InterruptedException e) {
            logger.error(">>> inPoolAddLast sleep 异常", e);
        }
        threadInPool.execute(() -> commonsThreadPool.addLast(object));
//        commonsThreadPool.addLast(object);
    }

    public void outPoolRemoveFirst() {
        Boolean countFlag = commonsThreadPool.addOutPoolCount();
        if (!countFlag) {
            logger.info(">>> outPoolRemoveFirst 获取队列数据的线程池已满");
            return;
        }
        threadOutPool.execute(() -> {
            while (true) {
                try {
                    Object removeFirstObj = commonsThreadPool.removeFirst();// 阻塞方法, 如果为空就不走下面的方法, 停在这里
                    if (removeFirstObj != null) {// 调用异步线程处理数据
                        System.out.println(">>> threadDataPool.getPoolSize = " + threadDataPool.getPoolSize() + "; getActiveCount = " + threadDataPool.getActiveCount());
                        while (threadDataPool.getActiveCount() == 6) {// 达到最大线程数就等待
                            logger.info(">>> outPoolRemoveFirst 线程池满, 排队等待...");
                            Thread.sleep(10);
                        }
                        threadDataPool.execute(() -> {
                            try {
                                commonsThreadPool.doSomething(removeFirstObj);
                            } catch (Exception e) {
                                logger.error(">>> outPoolRemoveFirst 异步处理异常", e);
                            }
                        });
                        Thread.sleep(1);// 线程执行完睡眠1毫秒, 保证数据统计正确
                    }
                } catch (Exception e) {
                    logger.error(">>> outPoolRemoveFirst 获取队列数据异常", e);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e1) {
                        logger.error(">>> outPoolRemoveFirst sleep 异常", e1);
                    }
                }
            }
        });
    }

    public void outPoolRemoveLast() {
        Boolean countFlag = commonsThreadPool.addOutPoolCount();
        if (!countFlag) {
            logger.info(">>> outPoolRemoveLast 获取队列数据的线程池已满 ");
            return;
        }
        threadOutPool.execute(() -> {
            while (true) {
                try {
                    Object removeLastObj = commonsThreadPool.removeLast();// 阻塞方法, 如果为空就不走下面的方法, 停在这里
                    if (removeLastObj != null) {// 调用异步线程处理数据
                        System.out.println(">>> threadDataPool.getPoolSize = " + threadDataPool.getPoolSize() + "; getActiveCount = " + threadDataPool.getActiveCount());
                        while (threadDataPool.getActiveCount() == 6) {// 达到最大线程数就等待
                            logger.info(">>> outPoolRemoveLast 线程池满, 排队等待...");
                            Thread.sleep(10);
                        }
                        threadOutPool.execute(() -> {
                            try {
                                commonsThreadPool.doSomething(removeLastObj);
                            } catch (Exception e) {
                                logger.error(">>> outPoolRemoveLast 异步处理异常", e);
                            }
                        });
                        Thread.sleep(1);// 线程执行完睡眠1毫秒, 保证数据统计正确
                    }
                } catch (Exception e) {
                    logger.error(">>> outPoolRemoveLast 获取队列数据异常", e);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e1) {
                        logger.error(">>> outPoolRemoveLast sleep 异常", e1);
                    }
                }
            }
        });
    }

    private void doSomething(Object object) {
        poolTestService.doSomething(object);
    }

    private Boolean addOutPoolCount() {
        if (threadOutPoolCount >= Integer.valueOf(threadOutSize)) {
            return false;
        }
        threadOutPoolCount++;
        return true;
    }

    public void setLinkedBlockingDeque(LinkedBlockingDeque<Object> linkedBlockingDeque) {
        this.linkedBlockingDeque = linkedBlockingDeque;
    }

    public LinkedBlockingDeque<Object> getLinkedBlockingDeque() {
        return linkedBlockingDeque;
    }

    private void setThreadInPool(ExecutorService threadInPool) {
        this.threadInPool = threadInPool;
    }

    private void setThreadOutPool(ExecutorService threadOutPool) {
        this.threadOutPool = threadOutPool;
    }

    private void setThreadDataPool(ThreadPoolExecutor threadDataPool) {
        this.threadDataPool = threadDataPool;
    }

}
