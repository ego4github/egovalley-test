package com.egovalley.web;

import com.egovalley.service.MagicIceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/ice")
public class MagicIceController {

    private static final Logger logger = LoggerFactory.getLogger(MagicIceController.class);

    @Autowired
    private MagicIceService magicIceService;

    @RequestMapping("/loveIce")
    @ResponseBody
    public Map<String, Object> intelligentIce(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        String message = request.getParameter("message");
        logger.info(">>> 识别话术: " + message);
        resultMap = magicIceService.discernMessage(message);
        return resultMap;
    }

    public static void main(String[] args) {
        /*int num = (int) (Math.random() * 6) + 1;
        System.out.println("num = " + num);*/

        /*String regex_hello = "^(.*你.*好.*)$";// .*内容.*内容.*  可以这么拼接
        String source = "你1好";
        boolean result = Pattern.matches(regex_hello, source);
        System.out.println("result = " + result);*/

        /*for (int i = 0; i < 100; i++) {
            int num = (int) (Math.random() * 5);
            if (num == 6) {
                System.out.println("num = " + num);
            } else {
                System.out.println("没有5");
            }
        }*/

        String src = "76SD987F89S3SD89FPAPHONE";
        String substring = src.substring(0, src.length() - 7);
        System.out.println("substring = " + substring);
    }

}
