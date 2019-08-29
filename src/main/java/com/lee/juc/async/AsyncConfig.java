package com.lee.juc.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TODO 需要在启动类上加上@EnableAsync注解
 * 不显式的实现 AsyncConfigurer ，我们可以在Spring环境中配置多个 Executor 类型的 Bean，
 * 在使用 @Async 注解时，将注解的 value 指定为你 Executor 类型的 BeanName，
 * 就可以使用指定的线程池来作为任务的载体，这样就使用线程池也更加灵活。
 */
@Configuration
public class AsyncConfig {

    private static final int MAX_POOL_SIZE = 50;

    private static final int CORE_POOL_SIZE = 20;

    @Bean("taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setThreadNamePrefix("async-task-thread-pool");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
