package com.egovalley.service;

import java.util.Map;

public interface MagicIceService {

    // 识别话术
    Map<String,Object> discernMessage(String message);

}
