package com.egovalley.service.impl;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import com.egovalley.common.WebSocketComponent;
import com.egovalley.service.AliASRSpeechTranscriberService;
import com.egovalley.utils.CacheUtils;
import com.egovalley.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 20191203
 * 这个是正常调用的Service
 */
@Service("aliASRSpeechTranscriberService")
public class AliASRSpeechTranscriberServiceImpl implements AliASRSpeechTranscriberService {

    private static final Logger logger = LoggerFactory.getLogger(AliASRSpeechTranscriberServiceImpl.class);

    @Value("${ali.asr.AppKey}")
    private String appKey;
    @Value("${ali.asr.AccessKeyId}")
    private String accessKeyId;
    @Value("${ali.asr.AccessKeySecret}")
    private String accessKeySecret;
    @Value("${ali.asr.NlsUrl}")
    private String nlsUrl;
    @Value("${ali.asr.preFileSavePath}")
    private String preFileSavePath;
    @Value("${ali.asr.sufFileSavePath}")
    private String sufFileSavePath;

    private static WebSocketComponent webSocketComponent;
    @Autowired
    private void setWebSocketComponent(WebSocketComponent webSocketComponent) {
        this.webSocketComponent = webSocketComponent;
    }

    @Override
    public Map<String, Object> initTranscriber(String sessionId) {
        Map<String, Object> returnMap = new HashMap<>();
        // 新版SDK在这里每次都重新获取token, 所以不会过期
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            logger.info(">>> 初始化ASR客户端, token过期时间: " + accessToken.getExpireTime());
            NlsClient client;
            if (nlsUrl.isEmpty()) {
                client = new NlsClient(accessToken.getToken());
            } else {
                client = new NlsClient(nlsUrl, accessToken.getToken());
            }
            CacheUtils.put(sessionId + "@client", client, 7200000);
            returnMap.put("resCode", "200");
            returnMap.put("resMsg", "init succeed");
        } catch (Exception e) {
            logger.error(">>> 初始化ASR客户端异常", e);
            returnMap.put("resCode", "500");
            returnMap.put("resMsg", "init error");
        }
        return returnMap;
    }

    private static SpeechTranscriberListener getTranscriberListener(String sessionId) {
        System.out.println("Listener的sessionId = " + sessionId);
        SpeechTranscriberListener listener = new SpeechTranscriberListener() {
            //TODO 识别出中间结果.服务端识别出一个字或词时会返回此消息.仅当setEnableIntermediateResult(true)时,才会有此类消息返回
            @Override
            public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() +
                        ", name: " + response.getName() +
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
                // TODO 重要提示： task_id很重要，是调用方和服务端通信的唯一ID标识，当遇到问题时，需要提供此task_id以便排查
                System.out.println("task_id: " + response.getTaskId() + ", name: " + response.getName() + ", status: " + response.getStatus());
            }

            @Override
            public void onSentenceBegin(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() + ", name: " + response.getName() + ", status: " + response.getStatus());

            }

            //识别出一句话.服务端会智能断句,当识别到一句话结束时会返回此消息
            @Override
            public void onSentenceEnd(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() +
                        ", name: " + response.getName() +
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
                // TODO 在这里返回前台
                Map<String, Object> resMap = new HashMap<>();
                resMap.put("result", response.getTransSentenceText());
                try {
                    String resJson = JsonUtils.objectToJson(resMap);
                    webSocketComponent.assignMessageToUser(resJson, sessionId);
                } catch (Exception e) {
                    logger.error("哦豁", e);
                }
            }

            //识别完毕
            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                System.out.println("task_id: " + response.getTaskId() + ", name: " + response.getName() + ", status: " + response.getStatus());
            }

            @Override
            public void onFail(SpeechTranscriberResponse response) {
                // TODO 重要提示： task_id很重要，是调用方和服务端通信的唯一ID标识，当遇到问题时，需要提供此task_id以便排查
                System.out.println("task_id: " + response.getTaskId() + ", status: " + response.getStatus() + ", status_text: " + response.getStatusText());
            }
        };

        return listener;
    }

    /// 根据二进制数据大小计算对应的同等语音长度
    /// sampleRate 仅支持8000或16000
    public static int getSleepDelta(int dataSize, int sampleRate) {
        // 仅支持16位采样
        int sampleBytes = 16;
        // 仅支持单通道
        int soundChannel = 1;
        return (dataSize * 10 * 8000) / (160 * sampleRate);
    }

    @Override
    public void transcriberProcessing(String sessionId, byte[] audioData) {
        SpeechTranscriber transcriber = null;
        NlsClient client = (NlsClient) CacheUtils.get(sessionId + "@client");
        if (client == null) {
            logger.info(">>> ASR客户端为空");
            return;
        }
        try {
            //创建实例,建立连接
            transcriber = new SpeechTranscriber(client, getTranscriberListener(sessionId));
            transcriber.setAppKey(appKey);
            //输入音频编码方式
            transcriber.setFormat(InputFormatEnum.PCM);
            //输入音频采样率
            transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            //是否返回中间识别结果
            transcriber.setEnableIntermediateResult(false);
            //是否生成并返回标点符号
            transcriber.setEnablePunctuation(true);
            //是否将返回结果规整化,比如将一百返回为100
            transcriber.setEnableITN(false);

            //此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
            transcriber.start();

//            File file = new File("E:\\lky\\阿里ASRdemo\\nls-sdk-java-demo\\nls-example-transcriber\\src\\main\\resources\\nls-sample-16k.wav");
            File file = new File(preFileSavePath + sessionId + sufFileSavePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[4096];
            int len;
            while ((len = fis.read(b)) > 0) {
                logger.info("send data pack length: " + len);
                transcriber.send(b);
                // TODO  重要提示：这里是用读取本地文件的形式模拟实时获取语音流并发送的，因为read很快，所以这里需要sleep
                // TODO  如果是真正的实时获取语音，则无需sleep, 如果是8k采样率语音，第二个参数改为8000
                // 8000采样率情况下，3200byte字节建议 sleep 200ms，16000采样率情况下，3200byte字节建议 sleep 100ms
//                int deltaSleep = getSleepDelta(len, 16000);
//                Thread.sleep(deltaSleep);
            }
//            InputStream bis = new BufferedInputStream(new ByteArrayInputStream(audioData));
//            transcriber.send(bis);
//            bis.close();
            //通知服务端语音数据发送完毕,等待服务端处理完成
            long now = System.currentTimeMillis();
            logger.info(">>> ASR wait for complete");
            transcriber.stop();
            fis.close();// TODO 这里不关流, 后面不删除
            logger.info(">>> ASR latency: " + (System.currentTimeMillis() - now) + " ms");
        } catch (Exception e) {
            logger.error(">>> ASR处理异常", e);
        } finally {
            if (null != transcriber) {
                transcriber.close();
            }
        }
    }

    @Override
    public Map<String, Object> shutdownTranscriber(String sessionId) {
        File file = new File(preFileSavePath + sessionId + sufFileSavePath);
        if (file.exists() && file.isFile()) {
            logger.info(">>> 有, 准备删除");
            boolean delete = file.delete();
            logger.info(">>> delete = " + delete);
        }

        Map<String, Object> returnMap = new HashMap<>();
        NlsClient client = (NlsClient) CacheUtils.get(sessionId + "@client");
        if (client != null) {
            client.shutdown();
            returnMap.put("resCode", "200");
            returnMap.put("resMsg", "shutdown succeed");
        } else {
            returnMap.put("resCode", "400");
            returnMap.put("resMsg", "shutdown failed");
        }
        return returnMap;
    }


}
