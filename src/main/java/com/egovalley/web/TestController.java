package com.egovalley.web;

import com.egovalley.common.CommonsThreadPool;
import com.egovalley.utils.CacheUtils;
import com.egovalley.utils.VerifyTokenUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.LinkedBlockingDeque;

@Controller
@RequestMapping("/test")
@SuppressWarnings("unchecked")
public class TestController {

    private LinkedBlockingDeque<Object> d1;
    private LinkedBlockingDeque<Object> d2;
    private LinkedBlockingDeque<Object> d3;

    private static String secretKey;
    @Value("${verify.secret.key}")
    public void setSecretKey(String secretKey) {
        TestController.secretKey = secretKey;
    }

    @RequestMapping("/pool/{num}/{deque}")
    @ResponseBody
    public void poolTest(@PathVariable("num") int num, @PathVariable("deque") char deque, HttpServletRequest request) {
        try {
            System.out.println("num = " + num + "; deque = " + deque);
            String id = request.getSession().getId();
            CommonsThreadPool pool = CommonsThreadPool.getCommonsThreadPool();
            // 获取初始化池中的默认队列
            d1 = pool.getLinkedBlockingDeque();
            CacheUtils.put("deque1", d1);
            // 获取初始化后新创建的队列
            if (d2 == null) {
                d2 = new LinkedBlockingDeque<>(120);
                CacheUtils.put("deque2", d2);
            }
            if (d3 == null) {
                d3 = new LinkedBlockingDeque<>(140);
                CacheUtils.put("deque3", d3);
            }
            LinkedBlockingDeque<Object> deque1 = (LinkedBlockingDeque<Object>) CacheUtils.get("deque1");
            LinkedBlockingDeque<Object> deque2 = (LinkedBlockingDeque<Object>) CacheUtils.get("deque2");
            LinkedBlockingDeque<Object> deque3 = (LinkedBlockingDeque<Object>) CacheUtils.get("deque3");

            switch (deque) {
                case '1':
                    for (int i = 0; i < num; i++) {
                        pool.inPoolAddFirst("session = " + id + " 的 " + i, deque1);
                    }
                    break;
                case '2':
                    for (int i = 0; i < num; i++) {
                        pool.inPoolAddFirst("session = " + id + " 的 " + i, deque2);
                    }
                    break;
                case '3':
                    for (int i = 0; i < num; i++) {
                        pool.inPoolAddFirst("session = " + id + " 的 " + i, deque3);
                    }
                    break;
                default:
                    System.out.println("参数缺失");
                    break;
            }
            System.out.println("deque1.size() = " + deque1.size());
            System.out.println("deque2.size() = " + deque2.size());
            System.out.println("deque3.size() = " + deque3.size());
            System.out.println("--------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/verify")
    public void verifyTest() {
        System.out.println("secretKey = " + secretKey);
        String randomCode = RandomStringUtils.randomAlphanumeric(16);
        System.out.println("randomCode = " + randomCode);
        long startTime = System.currentTimeMillis() + 998 * 66 * 3;
        String token1 = VerifyTokenUtils.createTokenBySHA(secretKey, randomCode, startTime);
        System.out.println("token1 = " + token1);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis() + 998 * 66 * 3;
        String token2 = VerifyTokenUtils.createTokenBySHA(secretKey, randomCode, startTime);
        System.out.println("token2 = " + token2);
        long costTime = (endTime - startTime) / 1000;
        System.out.println("costTime = " + costTime);
        if (costTime > 3) {
            System.out.println("请求超时");
        } else if (!token2.equals(token1)) {
            System.out.println("签名不一致");
        } else {
            System.out.println("请求通过");
        }
    }

}
