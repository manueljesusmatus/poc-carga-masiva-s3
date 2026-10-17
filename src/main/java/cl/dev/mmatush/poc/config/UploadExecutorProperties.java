package cl.dev.mmatush.poc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "upload.executor")
public record UploadExecutorProperties(
        String threadNamePrefix,
        Duration awaitTermination,
        Custom custom,
        Fixed fixed,
        Cached cached,
        Virtual virtual) {

    public record Custom(int corePoolSize, int maxPoolSize, int queueCapacity) {
    }

    public record Fixed(int poolSize) {
    }

    public record Cached(Duration keepAlive) {
    }

    public record Virtual(int concurrencyLimit) {
    }
}
