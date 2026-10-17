package cl.dev.mmatush.poc.concurrent.impl;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;

@Component
@Profile("single")
public class ThreadPoolSingle implements ThreadPoolUpload {

    public final ExecutorService executor;

    public ThreadPoolSingle() {
        this.executor = Executors.newSingleThreadExecutor();
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
