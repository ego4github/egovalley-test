package com.egovalley.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Value("${ws.webSocketUrl}")
    private String wsWebSocketUrl;
    @Value("${wss.webSocketUrl}")
    private String wssWebSocketUrl;

    @RequestMapping("/surprise")
    public String surprise() {
        return "welcome-first";
    }

    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/joke-1")
    public String joke1() {
        return "common/joke-1";
    }

    @RequestMapping("/joke-2")
    public String joke2() {
        return "common/joke-2";
    }

    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/grid")
    public String grid() {
        return "bootstrap/bootstrap-grid";
    }

    @RequestMapping("/msg")
    public String msg() {
        return "common/msg";
    }

    @RequestMapping("/register-ok")
    public String registerOk() {
        return "common/register-ok";
    }

    @RequestMapping("/chat")
    public ModelAndView chat(HttpServletRequest request, ModelAndView mv) {
        String url = request.getScheme();
        logger.info(">>> webSocket协议: " + url);
        mv.setViewName("chat");
        if (!url.contains("https")) {
            mv.addObject("webSocketUrl", wsWebSocketUrl);
        } else {
            mv.addObject("webSocketUrl", wssWebSocketUrl);
        }
        return mv;
    }

    @RequestMapping("/magic-ice")
    public String magicIce() {
        return "magic-ice";
    }

    @RequestMapping("/touch-talk")
    public String touchTalk() {
        return "touch-talk";
    }

    @RequestMapping("/asr-test")
    public String asrTest() {
        return "asr-test";
    }

    @RequestMapping("/asr-test2")
    public ModelAndView asrTest2(HttpServletRequest request, ModelAndView mv) {
        String url = request.getScheme();
        logger.info(">>> webSocket协议: " + url);
        mv.setViewName("asr-test2");
        if (!url.contains("https")) {
            mv.addObject("webSocketUrl", wsWebSocketUrl);
        } else {
            mv.addObject("webSocketUrl", wssWebSocketUrl);
        }
        return mv;
    }

}
