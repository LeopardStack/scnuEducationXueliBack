package com.scnujxjy.backendpoint.oldSysDataExport;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.*;

@Slf4j
public class TestThread {

    // 定义一个共享队列
    private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>();

    private void consumeData(String x){
        try {
            String poll = sharedQueue.poll(5, TimeUnit.SECONDS);
            log.info("消费者消费了 " + poll);
        }catch (Exception e){
            log.error("消费者消费失败 " + e.toString());
        }
    }


    private void produceData(String x){
        try {
            sharedQueue.put(x);
            log.info("生产者山产了 " + x);
        }catch (Exception e){
            log.error("生产者山产失败 " + e.toString());
        }
    }

    @Test
    public void test1() throws InterruptedException {
        int startYear = 2015;
        int endYear = 2019;
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        String jxd_jc = "cosumer";
        // 启动消费者线程
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> {
                consumeData(jxd_jc + finalI);
            });
        }

        // 生产数据
        for (int i = endYear; i >= startYear; i--) {
            produceData(String.valueOf(i));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }
}
