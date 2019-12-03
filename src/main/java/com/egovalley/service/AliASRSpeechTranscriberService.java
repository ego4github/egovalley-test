package com.egovalley.service;

import java.util.Map;

public interface AliASRSpeechTranscriberService {

    /**
     * 开流
     */
    Map<String, Object> initTranscriber(String sessionId);

    /**
     * 传流
     */
    void transcriberProcessing(String sessionId, byte[] audioData);

    /**
     * 关流
     */
    Map<String, Object> shutdownTranscriber(String sessionId);

}
