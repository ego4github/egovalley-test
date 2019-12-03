package com.egovalley.web;

import com.egovalley.service.ASRAudioDataService;
import com.egovalley.service.AliASRSpeechTranscriberService;
import com.egovalley.utils.ByteUtils;
import com.egovalley.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/asr")
@SuppressWarnings("unchecked")
public class ASRController {

    private static final Logger logger = LoggerFactory.getLogger(ASRController.class);

    @Autowired
    private ASRAudioDataService asrAudioDataService;
    @Autowired
    private AliASRSpeechTranscriberService aliASRSpeechTranscriberService;

    @Value("${ali.asr.preFileSavePath}")
    private String preFileSavePath;
    @Value("${ali.asr.sufFileSavePath}")
    private String sufFileSavePath;

    @RequestMapping("/transcriber")
    @ResponseBody
    public Map<String, Object> speechTranscriber(@RequestBody String jsonParam) throws Exception {
        Map<String, Object> paramMap = JsonUtils.jsonToMap(jsonParam);
        Map<String, Object> resultMap = new HashMap<>();
        String sessionId = "" + paramMap.get("sessionId");
        String status = "" + paramMap.get("status");
        if (StringUtils.isBlank(sessionId) || "null".equals(sessionId)
                || StringUtils.isBlank(status) && !"null".equals(status)) {
            logger.info(">>> 当前ASR请求的sessionId或status缺失");
            resultMap.put("resCode", "300");
            resultMap.put("resMsg", "当前请求缺少sessionId或status");
            return resultMap;
        }
        asrAudioDataService.transcriberService(paramMap);
        resultMap.put("resCode", "200");
        resultMap.put("resMsg", "结束请求调用成功");
        return resultMap;
    }

    @RequestMapping("/asrPart")
    @ResponseBody
    public Map<String, Object> asrPartTest(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("audioData");
        String sessionId = params.getParameter("sessionId");
        logger.info(">>> sessionId = " + sessionId);
        MultipartFile file;
        for (MultipartFile multipartFile : files) {
            file = multipartFile;
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    logger.info(">>> bytes.length = " + bytes.length);
                    File f = new File(preFileSavePath + sessionId + sufFileSavePath);
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();
                    fos.close();
                    // 开ASR
                    Map<String, Object> initMap = aliASRSpeechTranscriberService.initTranscriber(sessionId);
                    logger.info(">>> initMap = " + initMap);
                    byte[] sufBytes = new byte[81920];
                    if (bytes.length < sufBytes.length) {
                        logger.info(">>> 小于81920");
                        System.arraycopy(bytes, 0, sufBytes, 0, bytes.length);
                    } else {
                        logger.info(">>> 大于81920");
                        System.arraycopy(bytes, 0, sufBytes, 0, sufBytes.length);
                    }
                    logger.info(">>> sufBytes.length = " + sufBytes.length);
                    // 传ASR
                    aliASRSpeechTranscriberService.transcriberProcessing(sessionId, sufBytes);
                    // 关ASR
                    Map<String, Object> shutdownMap = aliASRSpeechTranscriberService.shutdownTranscriber(sessionId);
                    logger.info(">>> shutdownMap = " + shutdownMap);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                logger.info(">>> 莫得");
            }
        }
        resultMap.put("resCode", "200");
        resultMap.put("resMsg", "Success");
        return resultMap;
    }

    @RequestMapping("/writeFile")
    @ResponseBody
    public void writeFileTest(@RequestBody String jsonParam) {
        System.out.println("来咯来咯");
        byte[] files = new byte[] {1,2,3};
        ByteUtils.writeFile(files, "莫得路子");
    }

}
