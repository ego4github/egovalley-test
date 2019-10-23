package com.egovalley.utils;

import java.io.*;

/**
 * 不能用匿名对象 new BufferedReader(new FileReader())
 * 因为如果没有关闭流的时候, 另一个流再去读同一个文件, 可能会出错或数据不是最新的
 * 虽然方法调用结束后虚拟机会将对象销毁
 * 但是有可能在两次几乎同时的访问里, 第一次的对象还未被虚拟机销毁之前第二次访问了还未改变的数据
 *
 *  ☆☆☆☆☆
 *  同一个文件路径不能new两个IO流, 否则第二个打开是null
 *  是因为↓
 *  文件写入对象被创建时会执行一个native的open操作, 这个操作会清空文件. 你也可以选择创建追加模式的文件写入对象.
 *  FileWriter fw = new FileWriter(f, true);
 */
@SuppressWarnings("all")
public class IOUtils {

    /**
     * 读取访问量
     * @param path
     * @return
     */
    public static String readCount(String source) {
        File file = new File(source);
        String count = "";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null) {
                count = line;
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 增加访问量
     * @param target
     * @param count
     */
    public static void addCount(String target, String count) {
        File file = new File(target);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(count);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
