package cl.dev.mmatush.poc.concurrent.impl;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BooleanSupplier;

@Component
@Profile("custom")
public class ThreadPoolCustom implements ThreadPoolUpload {

    private final TaskExecutor executor;

    public ThreadPoolCustom() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setCorePoolSize(5);
        e.setMaxPoolSize(5);
        e.setQueueCapacity(10);
        e.setThreadNamePrefix("upload-");
        e.setWaitForTasksToCompleteOnShutdown(true);
        e.setAwaitTerminationSeconds(60);
        e.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        e.initialize();
        this.executor = e;
    }

    @Override
    public CompletableFuture<Boolean> execute(List<BooleanSupplier> tasks) {
        List<CompletableFuture<Boolean>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(task::getAsBoolean, executor))
                .toList();

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .allMatch(CompletableFuture::join));
    }

}
