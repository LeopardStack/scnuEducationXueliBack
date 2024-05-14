package com.scnujxjy.backendpoint.config;

import com.scnujxjy.backendpoint.constant.IOThreadPoolConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 自定义线程池
     * @return
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Scnu-Xueli-Executor-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 获取一个IO线程池
     *
     * @return
     */
    public static ThreadPoolTaskExecutor getIOTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(IOThreadPoolConstant.CORE_THREAD_NUM);
        executor.setMaxPoolSize(IOThreadPoolConstant.MAX_THREAD_NUM);
        executor.setQueueCapacity(IOThreadPoolConstant.QUEUE_LENGTH);
        executor.setKeepAliveSeconds(IOThreadPoolConstant.KEEP_ALIVE_TIME_SECONDS);
        executor.setThreadNamePrefix(IOThreadPoolConstant.THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(IOThreadPoolConstant.REJECTED_EXECUTION_HANDLER);
        executor.initialize();
        return executor;
    }

}

