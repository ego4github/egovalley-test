package com.egovalley.web;

import com.egovalley.common.CommonsDataPool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/pool/{num}")
    @ResponseBody
    public void poolTest(@PathVariable("num") int num, HttpServletRequest request) {
        String id = request.getSession().getId();
        CommonsDataPool pool = CommonsDataPool.getCommonsDataPool();
        for (int i = 0; i < num; i++) {
            pool.inPoolAddFirst("session = " + id + " 的 " + i);
        }
    }

}
