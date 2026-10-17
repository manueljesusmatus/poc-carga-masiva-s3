package cl.dev.mmatush.poc.concurrent.impl;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import cl.dev.mmatush.poc.config.ExecutorConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BooleanSupplier;

@Slf4j
@Component
public class ThreadPoolUploadImpl implements ThreadPoolUpload {

    private final Executor executor;

    public ThreadPoolUploadImpl(@Qualifier(ExecutorConfiguration.UPLOAD_EXECUTOR) Executor executor) {
        this.executor = executor;
    }

    @Override
    public CompletableFuture<List<Boolean>> execute(List<BooleanSupplier> tasks) {
        List<CompletableFuture<Boolean>> futures;
        try {
            futures = tasks.stream()
                    .map(this::executeTask)
                    .toList();
        } catch (RejectedExecutionException e) {
            log.error("Error submitting tasks to executor", e);
            return CompletableFuture.failedFuture(e);
        }

        return collectTask(futures);
    }

    private CompletableFuture<Boolean> executeTask(BooleanSupplier task) {
        return CompletableFuture.supplyAsync(task::getAsBoolean, executor)
                .exceptionally(e -> {
                    log.error("Task execution failed", e);
                    return false;
                });
    }

    private static <T> CompletableFuture<List<T>> collectTask(List<CompletableFuture<T>> f) {
        return CompletableFuture
                .allOf(f.toArray(new CompletableFuture[0]))
                .thenApply(v -> f.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

}
