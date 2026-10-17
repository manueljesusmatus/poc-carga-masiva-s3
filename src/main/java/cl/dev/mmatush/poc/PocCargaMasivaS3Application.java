package cl.dev.mmatush.poc;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class PocCargaMasivaS3Application {

	public static void main(String[] args) {
		SpringApplication.run(PocCargaMasivaS3Application.class, args);
	}

}
