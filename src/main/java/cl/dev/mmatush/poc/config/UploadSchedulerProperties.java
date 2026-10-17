package cl.dev.mmatush.poc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "upload.scheduler")
public record UploadSchedulerProperties(
        String cron,
        Duration lockAtLeastFor,
        Duration lockAtMostFor,
        int taskCount) {
}
