package cl.dev.mmatush.poc.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(UploadExecutorProperties.class)
public class ExecutorConfiguration {

    public static final String UPLOAD_EXECUTOR = "uploadExecutor";

    private final UploadExecutorProperties props;

    public ExecutorConfiguration(UploadExecutorProperties props) {
        this.props = props;
    }

    @Bean(UPLOAD_EXECUTOR)
    @Profile("custom")
    public ThreadPoolTaskExecutor customExecutor() {
        ThreadPoolTaskExecutor e = baseExecutor();
        e.setCorePoolSize(props.custom().corePoolSize());
        e.setMaxPoolSize(props.custom().maxPoolSize());
        e.setQueueCapacity(props.custom().queueCapacity());
        e.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return e;
    }

    // Executors.newFixedThreadPool(N)
    @Bean(UPLOAD_EXECUTOR)
    @Profile("fixed")
    public ThreadPoolTaskExecutor fixedExecutor() {
        ThreadPoolTaskExecutor e = baseExecutor();
        e.setCorePoolSize(props.fixed().poolSize());
        e.setMaxPoolSize(props.fixed().poolSize());
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
        e.setKeepAliveSeconds((int) props.cached().keepAlive().toSeconds());
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
        SimpleAsyncTaskExecutor e = new SimpleAsyncTaskExecutor(props.threadNamePrefix() + "vt-");
        e.setVirtualThreads(true);
        e.setConcurrencyLimit(props.virtual().concurrencyLimit());
        e.setTaskTerminationTimeout(props.awaitTermination().toMillis());
        return e;
    }

    private ThreadPoolTaskExecutor baseExecutor() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setThreadNamePrefix(props.threadNamePrefix());
        e.setWaitForTasksToCompleteOnShutdown(true);
        e.setAwaitTerminationSeconds((int) props.awaitTermination().toSeconds());
        return e;
    }

}
