package com.egovalley.web;

import com.aliyuncs.exceptions.ClientException;
import com.egovalley.common.Constant;
import com.egovalley.domain.EgoUser;
import com.egovalley.service.UserService;
import com.egovalley.utils.SmsDemo;
import com.egovalley.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private static String redisHost;
    @Value("${redis.host}")
    private void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    private static int redisPort;
    @Value("${redis.port}")
    private void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param request
     * @return
     */
    @RequestMapping("/doRegister")
    @ResponseBody
    public Map<String, Object> doRegister(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        String inputPhone = request.getParameter("inputPhone"),
                inputCheckCode = request.getParameter("inputCheckCode");
        logger.info(">>> 注册手机号: " + inputPhone);
        logger.info(">>> 注册验证码: " + inputCheckCode);
        try {
            // 校验验证码
            Jedis jedis = new Jedis(redisHost, redisPort);
            jedis.connect();
            String checkCode = jedis.get("checkCode" + inputPhone);
            logger.info(">>> redis验证码: " + checkCode);
            jedis.disconnect();
            if (!inputCheckCode.equals(checkCode)) {
                logger.info(">>> 验证码错误");
                resultMap.put("resCode", 300);
                resultMap.put("resMsg", "验证码错老~");
                return resultMap;
            }
            logger.info(">>> 验证码正确");
            Date date = new Date();
            EgoUser egoUser = new EgoUser(
                    UUIDUtils.getId(),
                    "1",
                    request.getParameter("inputUsername"),
                    request.getParameter("inputPassword"),
                    request.getParameter("inputNickname"),
                    request.getParameter("inputGender"),
                    request.getParameter("inputBirthday"),
                    request.getParameter("inputPhone"),
                    request.getParameter("inputEmail"),
                    date,
                    Constant.ADMINISTRATOR,
                    date,
                    Constant.ADMINISTRATOR,
                    "0"
            );
            // 执行注册
            userService.doRegister(egoUser);
            resultMap.put("resCode", 200);
        } catch (Exception e) {
            logger.error(">>> 注册异常", e);
            resultMap.put("resCode", 500);
            resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
        }
        return resultMap;
    }

    /**
     * 获取验证码
     * @param request
     * @return
     */
    @RequestMapping("/getCheckCode")
    @ResponseBody
    public Map<String, Object> getCheckCode(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String flag = request.getParameter("flag"),
                    templateCode = "";
            if ("1".equals(flag)) {// flag = 1, 注册模板
                templateCode = Constant.TEMPLATE_CODE_REGISTER;
            } else if ("2".equals(flag)) {// flag = 2, 登录模板
                templateCode = Constant.TEMPLATE_CODE_LOGIN;
            }
            if (StringUtils.isBlank(templateCode)) {
                resultMap.put("resCode", 300);
                resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
                return resultMap;
            }
            logger.info(">>> 验证码模板 templateCode = " + templateCode);
            // 获取表单中填写的手机号
            String inputPhone = request.getParameter("inputPhone");
            logger.info(">>> 目标手机号 inputPhone = " + inputPhone);
            logger.info(">>> 正在使用该手机号发送验证码短信...");
            // 使用该手机号发送注册码短信
            SmsDemo.sendSms(
                    redisHost,
                    redisPort,
                    inputPhone,
                    Constant.SIGN_NAME_EGOVALLEY,
                    templateCode,
                    UUIDUtils.getSixNum()
            );
            logger.info(">>> 验证码短信发送成功");
            resultMap.put("resCode", 200);
        } catch (ClientException e) {
            resultMap.put("resCode", 500);
            resultMap.put("resMsg", "系统繁忙, 请稍后再试!");
        }
        return resultMap;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("47.111.172.115", 6379);
        jedis.connect();
        Set<String> keys = jedis.keys("*");
        for (String key :
                keys) {
            System.out.println(key);
        }
        jedis.disconnect();

        /*JedisUtils jedis = JedisUtils.getInstance();
        boolean exists = jedis.exists("testKey");
        System.out.println("exists = " + exists);*/

        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
        String str = sdf.format(d);
        System.out.println("str = " + str);*/

        /*int gender = 2;
        String sex = gender == 0 ? "小姐姐" : gender == 1 ? "小哥哥" : "人妖";
        System.out.println("sex = " + sex);*/
    }

}
