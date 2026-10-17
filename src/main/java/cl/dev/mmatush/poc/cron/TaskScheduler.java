package cl.dev.mmatush.poc.cron;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import cl.dev.mmatush.poc.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskScheduler {

    private final ThreadPoolUpload threadPoolUpload;
    private final UploadService uploadService;

    @Scheduled(cron = "0/20 * * * * ?")
    @SchedulerLock(
            name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "PT30s",
            lockAtMostFor = "PT30s")
    public void scheduledTask() {
        log.info("Initializing TaskScheduler scheduledTask");
        var lista = new ArrayList<BooleanSupplier>();
        for(int i = 0; i < 25; i++) {
            lista.add(uploadService::upload);
        }
        doTask(lista);
        log.info("Finishing TaskScheduler scheduledTask");
    }

    private void doTask(List<BooleanSupplier> tasks) {
        threadPoolUpload.execute(tasks)
                .thenAccept(result -> {
                    if (Boolean.TRUE.equals(result)) {
                        log.info("Upload completed successfully");
                    } else {
                        log.error("Upload failed");
                    }
                }).exceptionally(ex -> {
                    log.error("Error during upload", ex);
                    return null;
                }).join();
    }

}
