package com.egovalley.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.SpeechReqProtocol;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import com.egovalley.common.WebSocketComponent;
import com.egovalley.utils.ByteUtils;
import com.egovalley.utils.CacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 此示例演示了从麦克风采集语音并实时识别的过程
 * (仅作演示，需用户根据实际情况实现)
 */
public class SpeechTranscriberWithMicrophone {

    private static final Logger logger = LoggerFactory.getLogger(SpeechTranscriberWithMicrophone.class);

    private String appKey;
    private int count;
    private String fileSavePath;
    private WebSocketComponent webSocket;
    private String sessionId;

    public SpeechTranscriberWithMicrophone(
            String appKey,
            String accessKeyId,
            String accessKeySecret,
            String nlsUrl,
            String sessionId,
            int count,
            String fileSavePath,
            WebSocketComponent webSocket
    ) {
        this.appKey = appKey;
        this.count = count;
        this.fileSavePath = fileSavePath;
        this.webSocket = webSocket;
        this.sessionId = sessionId;
        // 新版SDK在这里每次都重新获取token, 所以不会过期
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            logger.info(">>> 初始化ASR客户端, token过期时间: " + accessToken.getExpireTime());
            //创建NlsClient实例,应用全局创建一个即可,默认服务地址为阿里云线上服务地址
            NlsClient client;
            if (nlsUrl.isEmpty()) {
                client = new NlsClient(accessToken.getToken());
            } else {
                client = new NlsClient(nlsUrl, accessToken.getToken());
            }
            CacheUtils.put(sessionId + "@client", client, 7200000);
        } catch (Exception e) {
            logger.error(">>> 初始化ASR客户端异常", e);
        }
    }

    public SpeechTranscriberListener getTranscriberListener() {
        SpeechTranscriberListener listener = new SpeechTranscriberListener() {
            //识别出中间结果.服务端识别出一个字或词时会返回此消息.仅当setEnableIntermediateResult(true)时,才会有此类消息返回
            @Override
            public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                // 重要提示： task_id很重要，是调用方和服务端通信的唯一ID标识，当遇到问题时，需要提供此task_id以便排查
                System.out.println("name: " + response.getName() +
                    //状态码 20000000 表示正常识别
                    ", status: " + response.getStatus() +
                    //句子编号，从1开始递增
                    ", index: " + response.getTransSentenceIndex() +
                    //当前的识别结果
                    ", result: " + response.getTransSentenceText() +
                    //当前已处理的音频时长，单位是毫秒
                    ", time: " + response.getTransSentenceTime());
            }

            @Override
            public void onTranscriberStart(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() +
                    "name: " + response.getName() +
                    ", status: " + response.getStatus());
            }

            @Override
            public void onSentenceBegin(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() +
                    "name: " + response.getName() +
                    ", status: " + response.getStatus());
            }

            //识别出一句话.服务端会智能断句,当识别到一句话结束时会返回此消息
            @Override
            public void onSentenceEnd(SpeechTranscriberResponse response) {
                System.out.println("name: " + response.getName() +
                    //状态码 20000000 表示正常识别
                    ", status: " + response.getStatus() +
                    //句子编号，从1开始递增
                    ", index: " + response.getTransSentenceIndex() +
                    //当前的识别结果
                    ", result: " + response.getTransSentenceText() +
                    //置信度
                    ", confidence: " + response.getConfidence() +
                    //开始时间
                    ", begin_time: " + response.getSentenceBeginTime() +
                    //当前已处理的音频时长，单位是毫秒
                    ", time: " + response.getTransSentenceTime());

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("result", response.getTransSentenceText());
                webSocket.assignMessageToUser(JSON.toJSONString(resultMap), sessionId);
            }

            //识别完毕
            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() +
                    ", name: " + response.getName() +
                    ", status: " + response.getStatus());
            }

            @Override
            public void onFail(SpeechTranscriberResponse response) {
                // 重要提示： task_id很重要，是调用方和服务端通信的唯一ID标识，当遇到问题时，需要提供此task_id以便排查
                System.out.println(
                    "task_id: " + response.getTaskId() +
                        //状态码 20000000 表示识别成功
                        ", status: " + response.getStatus() +
                        //错误信息
                        ", status_text: " + response.getStatusText());
            }
        };

        return listener;
    }

    public void process(String sessionId) {
        SpeechTranscriber transcriber = null;
        try {
            NlsClient client = (NlsClient) CacheUtils.get(sessionId + "@client");
            if (client == null) {
                logger.info(">>> ASR客户端为空");
                return;
            }
            // 创建实例,建立连接
            transcriber = new SpeechTranscriber(client, getTranscriberListener());
            transcriber.setAppKey(appKey);
            // 输入音频编码方式
            transcriber.setFormat(InputFormatEnum.PCM);
            // 输入音频采样率
            transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            // 是否返回中间识别结果
            transcriber.setEnableIntermediateResult(true);
            // 是否生成并返回标点符号
            transcriber.setEnablePunctuation(true);
            // 是否将返回结果规整化,比如将一百返回为100
            transcriber.setEnableITN(false);

            //此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
            transcriber.start();
            CacheUtils.put(sessionId + "@transcriber", transcriber, 7200000);

            AudioFormat audioFormat = new AudioFormat(16000.0F, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            logger.info(">>> You can speak now!");
            int nByte = 0;
            final int bufSize = 4096;// 默认3200
            byte[] buffer = new byte[bufSize];
            while ((nByte = targetDataLine.read(buffer, 0, bufSize)) > 0) {
                String endStatus = "" + CacheUtils.get(sessionId + "@endStatus");
                if (StringUtils.isNotBlank(endStatus) && !"null".equals(endStatus) && "-1".equals(endStatus)) {
                    saveAndSendAudioData(sessionId, buffer, transcriber, endStatus);
                    logger.info(">>> 再见");
                    break;
                } else if (StringUtils.isNotBlank(endStatus) && !"null".equals(endStatus) && "0".equals(endStatus)) {
                    /**
                     * 第一次开流, 就直接开始监控麦克风
                     * 第一次来关流, 不断开, 而是stop, 并把标识置为2
                     * 第二次来开流, 调用start开流并监控麦克风, 并把标识重置为1
                     */
                } else {
                    saveAndSendAudioData(sessionId, buffer, transcriber, endStatus);
                }
            }

            transcriber.stop();
        } catch (Exception e) {
            logger.error(">>> ASR处理异常", e);
        } finally {
            if (null != transcriber) {
                transcriber.close();
            }
        }
    }

    public void shutdownTranscriber(String sessionId) {
        NlsClient client = (NlsClient) CacheUtils.get(sessionId + "@client");
        if (client != null) {
            client.shutdown();
            CacheUtils.remove(sessionId + "@client");
            logger.info(">>> ASR客户端关闭成功");
        } else {
            logger.info(">>> ASR客户端关闭失败");
        }
    }

    @SuppressWarnings("unchecked")
    private void saveAndSendAudioData(String sessionId, byte[] buffer, SpeechTranscriber transcriber, String endStatus) throws IOException {
        // 记录录音数据
        File file = new File(fileSavePath);
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(buffer, 0, buffer.length);
        fos.flush();
        fos.close();

//        transcriber.send(buffer);
        // 存包到一定量级再发往ASR
        if (CacheUtils.get(sessionId + "@audioData") == null) {
            List<byte[]> list = new ArrayList<>();
            list.add(buffer);
            CacheUtils.put(sessionId + "@audioData", list, 300000);
        } else {
            List<byte[]> list = (List<byte[]>) CacheUtils.get(sessionId + "@audioData");
            list.add(buffer);
            CacheUtils.put(sessionId + "@audioData", list, 300000);
        }
        // 检查量级并发包
        List<byte[]> audioDataList = (List<byte[]>) CacheUtils.get(sessionId + "@audioData");
        logger.info(">>> 音频数组长度: " + audioDataList.size());
        if (StringUtils.isNotBlank(endStatus) && !"null".equals(endStatus) && "-1".equals(endStatus) && audioDataList.size() <= count) {
            SpeechTranscriberWithMicrophone stwm = (SpeechTranscriberWithMicrophone) CacheUtils.get(sessionId + "@stwm");
            if (stwm != null) {
                List<byte[]> endList = (List<byte[]>) CacheUtils.get(sessionId + "@audioData");
                if (endList != null && !endList.isEmpty()) {
                    byte[] endBytes = ByteUtils.getByteArrayByByteList(endList);
                    InputStream bis = new BufferedInputStream(new ByteArrayInputStream(endBytes));
                    SpeechReqProtocol.State state = transcriber.getState();
                    logger.info(">>> transcriber状态: " + state);
                    if (!"STATE_CLOSED".equals("" + state)) {
                        logger.info(">>> 发送结束帧");
                        transcriber.send(bis);
                    }
                    try {
                        transcriber.stop();
                    } catch (Exception e) {
                        logger.error(">>> transcriber结束异常", e);
                    }
                    stwm.shutdownTranscriber(sessionId);
                    CacheUtils.remove(sessionId + "@audioData");
                    CacheUtils.remove(sessionId + "@endStatus");
                    CacheUtils.remove(sessionId + "@stwm");
                    logger.info(">>> 结束录音, 关闭ASR");
                }
            } else {
                logger.info(">>> 关闭ASR失败");
            }
        } else if (audioDataList.size() == count) {
            byte[] audioBytes = ByteUtils.getByteArrayByByteList(audioDataList);
            InputStream bis = new BufferedInputStream(new ByteArrayInputStream(audioBytes));
            SpeechReqProtocol.State state = transcriber.getState();
            logger.info(">>> transcriber状态: " + state);
            if (!"STATE_CLOSED".equals("" + state)) {
                logger.info(">>> 发送中间帧");
                transcriber.send(bis);
                CacheUtils.remove(sessionId + "@audioData");
            }
        }
    }

}
