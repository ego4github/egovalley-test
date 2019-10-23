package com.egovalley.service.impl;

import com.egovalley.service.PoolTestService;
import org.springframework.stereotype.Service;

@Service("poolTestService")
public class PoolTestServiceImpl implements PoolTestService {

    @Override
    public void doSomething(Object object) {
        System.out.println("进来了" + object);
    }

}
