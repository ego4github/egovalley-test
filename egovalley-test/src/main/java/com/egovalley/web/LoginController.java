package com.egovalley.web;

import com.egovalley.common.ChatWebSocket;
import com.egovalley.domain.EgoUser;
import com.egovalley.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ChatWebSocket chatWebSocket;

    /**
     * 用户登录
     * @param request
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public Map<String, Object> doLogin(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        try {
            String username = request.getParameter("inputUsername");
            String password = request.getParameter("inputPassword");
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                resultMap.put("resCode", 300);
                resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
                return resultMap;
            }
            logger.info(">>> username = " + username);
            logger.info(">>> password = " + password);

            EgoUser user = userService.doLogin(username, password);
            if (user == null) {
                resultMap.put("resCode", 400);
                resultMap.put("resMsg", "账户或密码错误!");
                return resultMap;
            }
            session.setAttribute("userInfo", user);
            resultMap.put("resCode", 200);
        } catch (Exception e) {
            logger.error(">>> 用户登录出错", e);
            resultMap.put("resCode", 500);
            resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
        }
        return resultMap;
    }

    /**
     * 测试用户登录
     * @param request
     * @return
     */
    /*@RequestMapping("/doLogin")
    @ResponseBody
    public Map<String, Object> doLogin(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        try {
            String username = request.getParameter("inputUsername");
            String password = request.getParameter("inputPassword");
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                resultMap.put("resCode", 300);
                resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
                return resultMap;
            }
            logger.info(">>> username = " + username);
            logger.info(">>> password = " + password);
            if (!("admin".equals(username) && "admin".equals(password))) {
                resultMap.put("resCode", 400);
                resultMap.put("resMsg", "账户或密码错误!");
                return resultMap;
            }
            EgoUser user = new EgoUser();
            user.setEgoUsername(username);
            user.setEgoPassword(password);
            user.setEgoNickname("测试");
            user.setEgoGender("1");
            session.setAttribute("userInfo", user);
            resultMap.put("resCode", 200);
        } catch (Exception e) {
            logger.error(">>> 用户登录出错", e);
            resultMap.put("resCode", 500);
            resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
        }
        return resultMap;
    }*/

    /**
     * 用户注销
     * @param request
     * @return
     */
    @RequestMapping("/doLogout")
    @ResponseBody
    public Map<String, Object> doLogout(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        try {
            session.invalidate();
            resultMap.put("resCode", 200);
        } catch (Exception e) {
            logger.error(">>> 用户注销出错", e);
            resultMap.put("resCode", 500);
            resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
        }
        return resultMap;
    }

    /**
     * 获取用户昵称和性别
     * @return
     */
    @RequestMapping("/getNicknameAndGender")
    @ResponseBody
    public Map<String, Object> getNicknameAndGender(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        try {
            EgoUser userInfo = (EgoUser) session.getAttribute("userInfo");
            String gender = userInfo.getEgoGender();
            resultMap.put("nickname", userInfo.getEgoNickname());
            resultMap.put("gender", "0".equals(gender) ? "小姐姐" : "1".equals(gender) ? "小哥哥" : "啊");
        } catch (Exception e) {
            logger.error(">>> 获取昵称&性别出错", e);
            resultMap.put("resCode", 500);
            resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
        }
        return resultMap;
    }

    /**
     * 获取所有在线用户
     * @return
     */
    @RequestMapping("/getOnlineUsers")
    @ResponseBody
    public List<String> getOnlineUsers() {
        List<String> onlineUsers = chatWebSocket.getOnlineUsers();
        return onlineUsers;
    }

    /**
     * 群发欢迎语句
     * @return
     */
    @RequestMapping("/welcomeToAll")
    @ResponseBody
    public void welcomeToAll() {
        chatWebSocket.broadcast(1);
    }

    /**
     * 群发欢送语句
     * @return
     */
    @RequestMapping("/goodbyeToAll")
    @ResponseBody
    public void goodbyeToAll() {
        chatWebSocket.broadcast(2);
    }

}
