package com.egovalley.web;

import com.egovalley.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/visit")
public class VisitController {

    @Value("${count.daily.path}")
    private String dailyPath;
    @Value("${count.total.path}")
    private String totalPath;

    @RequestMapping("/initDailyAndTotal")
    @ResponseBody
    public Map<String, Object> dailyCount() {
        Map<String, Object> resultMap = new HashMap<>();
        String dailyCount = IOUtils.readCount(dailyPath);
        String totalCount = IOUtils.readCount(totalPath);
        resultMap.put("dailyCount", dailyCount);
        resultMap.put("totalCount", totalCount);
        return resultMap;
    }

}
