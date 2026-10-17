package cl.dev.mmatush.poc.concurrent.impl;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BooleanSupplier;

@Component
@Profile("fixed")
public class ThreadPoolFixed implements ThreadPoolUpload {

    private final ThreadPoolExecutor executor;

    public ThreadPoolFixed() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
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
