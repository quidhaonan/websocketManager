package com.lmyxlf.websocket_manager.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 */
@Configuration
@EnableAsync //开启异步任务支持
public class ThreadPoolOfAsyncConfig implements AsyncConfigurer {

    private static final int CORE_POLL_SIZE = 150;

    private static final int MAX_POLL_SIZE = 200;

    private static final int QUEUE_CAPACITY = 1000;

    private static final int KEEP_ALIVE_SECONDS = 60;

    @Bean("async_executor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POLL_SIZE);//核心线程数
        taskExecutor.setMaxPoolSize(MAX_POLL_SIZE);//最大线程数
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);//队列最大长度
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);//线程池维护线程所允许的空闲时间
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);//用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        taskExecutor.setAwaitTerminationSeconds(KEEP_ALIVE_SECONDS);//设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//线程池对拒绝任务(无线程可用)的处理策略
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}