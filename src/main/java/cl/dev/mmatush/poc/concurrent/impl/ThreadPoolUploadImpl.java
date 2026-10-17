package cl.dev.mmatush.poc.concurrent.impl;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import cl.dev.mmatush.poc.config.ExecutorConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

/**
 * El tipo de pool (custom, fixed, cached, single) lo define el profile activo
 * a traves del bean {@link ExecutorConfiguration#UPLOAD_EXECUTOR}.
 */
@Component
public class ThreadPoolUploadImpl implements ThreadPoolUpload {

    private final Executor executor;

    public ThreadPoolUploadImpl(@Qualifier(ExecutorConfiguration.UPLOAD_EXECUTOR) Executor executor) {
        this.executor = executor;
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
