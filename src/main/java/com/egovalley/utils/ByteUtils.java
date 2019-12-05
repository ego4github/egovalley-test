package com.egovalley.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class ByteUtils {

    private static final Logger logger = LoggerFactory.getLogger(ByteUtils.class);
    public static ExecutorService executorService;

    @PostConstruct
    private void initTest() {
        logger.info(">>> @PostConstruct test, must be @Component.");
    }

    /**
     * 应用场景:
     *    前端按帧采集的音频数据传至后台的数据格式是 List<Integer>
     *    由于一帧的数据量太少, 无法转换出有效的音转字信息, 所以在后台拼接了20帧后调用的数据格式是 List<List<Integer>>
     *    数据格式可根据业务设计灵活变动
     */
    public static List<byte[]> getByteListByListInt8(List<List<Integer>> int8List) {
        List<byte[]> byteList = new ArrayList<>();
        for (List<Integer> thisInt8List : int8List) {
            if (thisInt8List != null && thisInt8List.size() != 0) {
                byte[] byteArray = new byte[thisInt8List.size()];
                for (int j = 0, len = thisInt8List.size(); j < len; j++) {
                    int k = thisInt8List.get(j);
                    byteArray[j] = (byte) k;
                }
                byteList.add(byteArray);
            }
        }
        return byteList;
    }

    /**
     * 应用场景:
     *    调用上一个方法后获得的 List<byte[]> 数据, 可以调用此方法
     *    获得 byte[] 数据后, 就可以扔给模型进行音转字了
     */
    public static byte[] getByteArrayByByteList(List<byte[]> byteList) {
        int byteListLength = 0;
        for (byte[] thisByteList : byteList) {
            byteListLength += thisByteList.length;
        }
        byte[] resultBytes = new byte[byteListLength];
        int countLength = 0;
        for (byte[] thisByteList : byteList) {
            System.arraycopy(thisByteList, 0, resultBytes, countLength, thisByteList.length);
            countLength += thisByteList.length;
        }
        return resultBytes;
    }

    /**
     * 应用场景:
     *      将上两个方法结合, 一次调用获得 byte[] 数据格式
     */
    public static byte[] getByteArrayByListInt8(List<List<Integer>> int8List) {
        // 将 List<List<Integer>> 数据转换成 List<byte[]> 数据
        List<byte[]> byteList = new ArrayList<>();
        for (List<Integer> thisInt8List : int8List) {
            if (thisInt8List != null && thisInt8List.size() != 0) {
                byte[] byteArray = new byte[thisInt8List.size()];
                for (int j = 0, len = thisInt8List.size(); j < len; j++) {
                    int k = thisInt8List.get(j);
                    byteArray[j] = (byte) k;
                }
                byteList.add(byteArray);
            }
        }
        // 将 List<byte[]> 转换成 byte[] 数据
        int byteListLength = 0;
        for (byte[] thisByteList : byteList) {
            byteListLength += thisByteList.length;
        }
        byte[] resultBytes = new byte[byteListLength];
        int countLength = 0;
        for (byte[] thisByteList : byteList) {
            System.arraycopy(thisByteList, 0, resultBytes, countLength, thisByteList.length);
            countLength += thisByteList.length;
        }
        return resultBytes;
    }

    /**
     * 线程写文件
     */
    public static void writeFile(byte[] bytes, String path) {
        executorService.execute(() -> {
            try {
                File file = new File(path);
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(bytes, 0, bytes.length);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                logger.error(">>> 线程写文件异常", e);
            }
        });
    }

}
