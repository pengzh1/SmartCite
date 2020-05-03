package cn.edu.whu.irlab.smart_cite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/3/16 20:03
 * @desc 多线程配置类
 **/
@Configuration
@EnableAsync
public class ThreadExecutorConfig {

    @Bean
    public Executor executor(){
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);//线程池维护线程最少数量
        executor.setMaxPoolSize(8);//线程池维护最大线程数量
        executor.setQueueCapacity(15);//缓存队列
        /**
         * 对拒绝task的处理策略
         rejection-policy：当pool已经达到max size的时候，如何处理新任务
         CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(60);//允许的空闲时间
        executor.setThreadNamePrefix("DailyAsync-");//线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.initialize();
        return executor;
    }
}
