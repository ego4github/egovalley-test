package com.egovalley.common;

import com.egovalley.utils.CacheUtils;

public class MainTest {

    public static void main(String[] args) {
        /*CommonsDataPool pool = CommonsDataPool.getCommonsDataPool();
        System.out.println("pool = " + pool);
//        pool.outPoolRemoveFirst();
        for (int i = 0; i < 10; i++) {
            pool.inPoolAddFirst(i);
        }
        pool.outPoolRemoveFirst();*/

        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("添加缓存...");
        CacheUtils.put("key1", "value1");
        CacheUtils.put("key2", "value2");
        CacheUtils.put("key3", "value3");
        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("key1 = " + CacheUtils.get("key1"));
        CacheUtils.put("key1", "value1_changed");
        System.out.println("重新对key1赋值...");
        System.out.println("key1 = " + CacheUtils.get("key1"));
        CacheUtils.remove("key1");
        System.out.println("删除key1...");
        System.out.println("key1 = " + CacheUtils.get("key1"));
        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("添加一个3秒过期的缓存...");
        CacheUtils.put("expireKey", "expire3000", 3000);
        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("expireKey = " + CacheUtils.get("expireKey"));
        try {
            System.out.println("等待5秒...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("expireKey = " + CacheUtils.get("expireKey"));
        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("添加一个5秒过期的缓存...");
        CacheUtils.put("expireKey2", "expire5000", 5000);
        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("expireKey2 = " + CacheUtils.get("expireKey2"));
        try {
            System.out.println("等待2秒...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("删除expireKey2...");
        CacheUtils.remove("expireKey2");
        System.out.println("expireKey2 = " + CacheUtils.get("expireKey2"));
        System.out.println("当前缓存量: " + CacheUtils.size());
        System.out.println("清除所有缓存...");
        CacheUtils.removeAll();
        System.out.println("当前缓存量: " + CacheUtils.size());
    }

}
