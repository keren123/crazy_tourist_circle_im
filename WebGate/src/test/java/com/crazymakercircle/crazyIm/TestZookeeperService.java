package com.crazymakercircle.crazyIm;

import com.crazymakercircle.Balance.ImLoadBalance;
import com.crazymakercircle.WebGateSpringApplication;
import com.crazymakercircle.entity.ImNode;
import com.crazymakercircle.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebGateSpringApplication.class)
@Slf4j
public class TestZookeeperService {
    
    @Resource
    private ImLoadBalance imLoadBalance;

    //测试用例： 测试 会话的 缓存 CRUD
    @Test
    public void testGetBestWorker() throws Exception {


        ImNode bestWorker = imLoadBalance.getBestWorker();


        System.out.println("bestWorker = " + bestWorker);

        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }



}
