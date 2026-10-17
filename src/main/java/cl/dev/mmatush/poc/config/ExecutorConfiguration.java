package cl.dev.mmatush.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorConfiguration {

    public static final String UPLOAD_EXECUTOR = "uploadExecutor";

    private static final int AWAIT_TERMINATION_SECONDS = 60;

    @Bean(UPLOAD_EXECUTOR)
    @Profile("custom")
    public ThreadPoolTaskExecutor customExecutor() {
        ThreadPoolTaskExecutor e = baseExecutor();
        e.setCorePoolSize(5);
        e.setMaxPoolSize(5);
        e.setQueueCapacity(10);
        e.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return e;
    }

    // Executors.newFixedThreadPool(N)
    @Bean(UPLOAD_EXECUTOR)
    @Profile("fixed")
    public ThreadPoolTaskExecutor fixedExecutor() {
        ThreadPoolTaskExecutor e = baseExecutor();
        e.setCorePoolSize(5);
        e.setMaxPoolSize(5);
        return e;
    }

    // Executors.newCachedThreadPool()
    @Bean(UPLOAD_EXECUTOR)
    @Profile("cached")
    public ThreadPoolTaskExecutor cachedExecutor() {
        ThreadPoolTaskExecutor e = baseExecutor();
        e.setCorePoolSize(0);
        e.setMaxPoolSize(Integer.MAX_VALUE);
        e.setQueueCapacity(0);
        e.setKeepAliveSeconds(60);
        return e;
    }

    // Executors.newSingleThreadExecutor()
    @Bean(UPLOAD_EXECUTOR)
    @Profile("single")
    public ThreadPoolTaskExecutor singleExecutor() {
        ThreadPoolTaskExecutor e = baseExecutor();
        e.setCorePoolSize(1);
        e.setMaxPoolSize(1);
        return e;
    }

    // Executors.newVirtualThreadPerTaskExecutor() + limite de concurrencia
    @Bean(UPLOAD_EXECUTOR)
    @Profile("virtual")
    public SimpleAsyncTaskExecutor virtualExecutor() {
        SimpleAsyncTaskExecutor e = new SimpleAsyncTaskExecutor("upload-vt-");
        e.setVirtualThreads(true);
        e.setConcurrencyLimit(5);
        e.setTaskTerminationTimeout(AWAIT_TERMINATION_SECONDS * 1000L);
        return e;
    }

    private static ThreadPoolTaskExecutor baseExecutor() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setThreadNamePrefix("upload-");
        e.setWaitForTasksToCompleteOnShutdown(true);
        e.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        return e;
    }
}
