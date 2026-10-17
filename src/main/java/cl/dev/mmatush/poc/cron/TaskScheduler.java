package cl.dev.mmatush.poc.cron;

import cl.dev.mmatush.poc.concurrent.ThreadPoolUpload;
import cl.dev.mmatush.poc.config.UploadSchedulerProperties;
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
    private final UploadSchedulerProperties props;

    @Scheduled(cron = "${upload.scheduler.cron}")
    @SchedulerLock(
            name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "${upload.scheduler.lock-at-least-for}",
            lockAtMostFor = "${upload.scheduler.lock-at-most-for}")
    public void scheduledTask() {
        log.info("Initializing TaskScheduler scheduledTask");
        var lista = new ArrayList<BooleanSupplier>();
        for(int i = 0; i < props.taskCount(); i++) {
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
