package cl.dev.mmatush.poc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class UploadService {

    public boolean upload() {
        try {
            log.info("Starting upload");
            Thread.sleep(Duration.ofSeconds(5).toMillis());
            log.info("Upload completed");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

}