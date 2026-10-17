package cl.dev.mmatush.poc.concurrent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface ThreadPoolUpload {

    CompletableFuture<Boolean> execute(List<BooleanSupplier> tasks);

}
