package com.codeit.sb06deokhugamteam2.notification.batch;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.NotificationException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

  private final JobLauncher jobLauncher;
  private final Job deleteAllNotificationsJob;

  @Scheduled(cron = "0 0 0 * * *")
  public void runDeleteAllNotificationsJob() {
    try
    {
      log.info("Starting Job Delete All Notifications");
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time",System.currentTimeMillis())
          .toJobParameters();

      jobLauncher.run(deleteAllNotificationsJob,jobParameters);
    }
    catch (Exception e)
    {
      throw new NotificationException(ErrorCode.COMMON_EXCEPTION,
          Map.of("message",e.getMessage(),"Scheduler","Notification scheduler exception occured. "),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
