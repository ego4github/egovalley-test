package com.egovalley.service.impl;

import com.alibaba.nls.client.protocol.SpeechReqProtocol;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.egovalley.common.WebSocketComponent;
import com.egovalley.domain.SpeechTranscriberWithMicrophone;
import com.egovalley.service.ASRAudioDataService;
import com.egovalley.service.AliASRSpeechTranscriberService;
import com.egovalley.utils.ByteUtils;
import com.egovalley.utils.CacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 20191203
 * 目前是对接Microphone的Service
 * 后续备案了域名, 拿到https的SSL安全证书后, 应该就有硬件调用权限
 * 然后再由Microphone转为Transcriber
 */
@Service("aSRAudioDataService")
public class ASRAudioDataServiceImpl implements ASRAudioDataService {

    private static final Logger logger = LoggerFactory.getLogger(ASRAudioDataServiceImpl.class);

    @Value("${ali.asr.AppKey}")
    private String appKey;
    @Value("${ali.asr.AccessKeyId}")
    private String accessKeyId;
    @Value("${ali.asr.AccessKeySecret}")
    private String accessKeySecret;
    @Value("${ali.asr.NlsUrl}")
    private String nlsUrl;
    @Value("${ali.asr.count}")
    private int count;
//    @Value("${ali.asr.fileSavePath}")
    private String fileSavePath;

    @Autowired
    private AliASRSpeechTranscriberService aliASRSpeechTranscriberService;

    @Autowired
    private WebSocketComponent webSocketComponent;

    @Override
    public void transcriberService(Map<String, Object> paramMap) {
        logger.info(">>> ASR请求入参: " + paramMap);
        String sessionId = "" + paramMap.get("sessionId");
        String status = "" + paramMap.get("status");
        if (StringUtils.isBlank(sessionId) || "null".equals(sessionId)
                || StringUtils.isBlank(status) && !"null".equals(status)) {
            logger.info(">>> 当前ASR请求的sessionId或status缺失");
            return;
        }
        if ("1".equals(status)) {
            logger.info(">>> 开始监控麦克风, 并处理ASR");
            SpeechTranscriberWithMicrophone stwm = new SpeechTranscriberWithMicrophone(appKey, accessKeyId, accessKeySecret, nlsUrl, sessionId, count, fileSavePath, webSocketComponent);
            CacheUtils.put(sessionId + "@stwm", stwm, 7200000);
            stwm.process(sessionId);
        } else if ("0".equals(status)) {
            SpeechTranscriberWithMicrophone stwm = (SpeechTranscriberWithMicrophone) CacheUtils.get(sessionId + "@stwm");
            if (stwm != null) {
                CacheUtils.put(sessionId + "@endStatus", status, 300000);
            } else {
                logger.info(">>> 没有就别乱点了");
            }
        } else if ("-1".equals(status)) {
            SpeechTranscriberWithMicrophone stwm = (SpeechTranscriberWithMicrophone) CacheUtils.get(sessionId + "@stwm");
            if (stwm != null) {
                CacheUtils.put(sessionId + "@endStatus", status, 300000);
            } else {
                logger.info(">>> 空的, 关不了");
            }
        }
    }
}
