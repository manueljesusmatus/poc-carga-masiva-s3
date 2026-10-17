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
public class UploadJob {

    private final ThreadPoolUpload threadPoolUpload;
    private final UploadService uploadService;
    private final UploadSchedulerProperties props;

    @Scheduled(cron = "${upload.scheduler.cron}")
    @SchedulerLock(
            name = "UploadJob_scheduledTask",
            lockAtLeastFor = "${upload.scheduler.lock-at-least-for}",
            lockAtMostFor = "${upload.scheduler.lock-at-most-for}")
    public void scheduledTask() {
        log.info("Initializing UploadJob scheduledTask");
        var lista = new ArrayList<BooleanSupplier>();
        for(int i = 0; i < props.taskCount(); i++) {
            lista.add(uploadService::upload);
        }
        doTask(lista);
        log.info("Finishing UploadJob scheduledTask");
    }

    private void doTask(List<BooleanSupplier> tasks) {
        threadPoolUpload.execute(tasks)
                .thenAccept(result ->
                    log.info("Upload results: {}", result))
                .exceptionally(ex -> {
                    log.error("Error during upload", ex);
                    return null;
                }).join();
    }

}
