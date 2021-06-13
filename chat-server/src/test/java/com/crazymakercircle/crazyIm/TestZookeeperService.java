package com.crazymakercircle.crazyIm;

import com.crazymakercircle.imServer.distributed.ImWorker;
import com.crazymakercircle.imServer.distributed.WorkerRouter;
import com.crazymakercircle.imServer.startup.ServerApplication;
import com.crazymakercircle.util.JsonUtil;
import com.crazymakercircle.util.Logger;
import com.crazymakercircle.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@Slf4j
public class TestZookeeperService {


    @Test
    public void testNodeName() throws Exception {
        for (int i = 0; i < 2; i++) {

            Class cls = ImWorker.class;
            Constructor<?> cons = cls.getDeclaredConstructor();//获得私有构造方法
            cons.setAccessible(true);
            ImWorker worker = (ImWorker) cons.newInstance();

            worker.init();
            Logger.cfo("worker = " + JsonUtil.pojoToJson(worker.getLocalNodeInfo()));
        }
        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }


    @Test
    public void testWorkerFound() throws Exception {
        Class cls = WorkerRouter.class;
        Constructor<?> cons = cls.getDeclaredConstructor();//获得私有构造方法
        cons.setAccessible(true);
        WorkerRouter workerRouter = (WorkerRouter) cons.newInstance();
        workerRouter.setRunAfterAdd(null);
        workerRouter.setRunAfterRemove(null);
        workerRouter.init();


        for (int i = 0; i < 4; i++) {

            ThreadUtil.getIoIntenseTargetThreadPool().submit(() -> {
                try {
                    Class cls1 = ImWorker.class;

                    Constructor<?> cons1 = cls1.getDeclaredConstructor();//获得私有构造方法

                    cons1.setAccessible(true);
                    ImWorker worker = (ImWorker) cons1.newInstance();

                    worker.init();
                    Logger.cfo("worker = " + JsonUtil.pojoToJson(worker.getLocalNodeInfo()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }

    @Test
    public void testIncBalance() throws Exception {

        Class cls = ImWorker.class;
        Constructor<?> cons = cls.getDeclaredConstructor();//获得私有构造方法
        cons.setAccessible(true);
        ImWorker worker = (ImWorker) cons.newInstance();

        worker.init();
        Logger.cfo("worker = " + JsonUtil.pojoToJson(worker.getLocalNodeInfo()));

        ThreadUtil.scheduleAtFixedRate(() -> {
            worker.incBalance();
        }, 1, TimeUnit.SECONDS);

        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }

}
