package com.codeit.sb06deokhugamteam2;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.sb06deokhugamteam2.notification.NotificationComponent;
import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationCreateRequest;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationUpdateRequest;
import com.codeit.sb06deokhugamteam2.notification.repository.NotificationRepository;
import com.codeit.sb06deokhugamteam2.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import java.lang.annotation.Documented;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotificationTest {

  @Autowired
  private NotificationComponent notificationComponent;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job readAllNoitificationsJob;

  @Autowired
  private NotificationRepository notificationRepository;

  private NotificationDto preSetupData;

  @BeforeEach
  void setup() {
    notificationRepository.deleteAll();
    NotificationCreateRequest request = new NotificationCreateRequest(UUID.randomUUID()
        ,UUID.randomUUID()
        ,"title"
        ,"content");

    preSetupData = notificationComponent.saveNotification(request);
  }

  @Test
  @DisplayName("Notification 저장 성공 테스트")
  @Transactional
  public void saveNotification() {
    NotificationCreateRequest request = new NotificationCreateRequest(UUID.randomUUID()
        ,UUID.randomUUID()
        ,"title"
        ,"content");

    NotificationDto dto = notificationComponent.saveNotification(request);
    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isNotNull();
    assertThat(dto.getReviewId()).isNotNull();
    assertThat(dto.getCreatedAt()).isNotNull();
    assertThat(dto.getUpdatedAt()).isNotNull();
    assertThat(dto.getConfirmed()).isEqualTo(false);
  }

  @Test
  @DisplayName("알림 읽기 변경 true")
  @Transactional
  public void updateReadStateTrueTest()
  {
    NotificationUpdateRequest request = new NotificationUpdateRequest(true);
    NotificationDto dto = notificationService.updateReadState(preSetupData.getId(),preSetupData.getUserId(),request);
    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isNotNull();
    assertThat(dto.getReviewId()).isNotNull();
    assertThat(dto.getCreatedAt()).isNotNull();
    assertThat(dto.getUpdatedAt()).isNotNull();
    assertThat(dto.getConfirmed()).isEqualTo(true);
  }

  @Test
  @DisplayName("알림 읽기 변경 false")
  @Transactional
  public void updateReadStateFalseTest()
  {
    NotificationUpdateRequest request = new NotificationUpdateRequest(false);
    NotificationDto dto = notificationService.updateReadState(preSetupData.getId(),preSetupData.getUserId(),request);
    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isNotNull();
    assertThat(dto.getReviewId()).isNotNull();
    assertThat(dto.getCreatedAt()).isNotNull();
    assertThat(dto.getUpdatedAt()).isNotNull();
    assertThat(dto.getUpdatedAt()).isEqualTo(dto.getCreatedAt());
    assertThat(dto.getConfirmed()).isEqualTo(false);
  }

  @Test
  @DisplayName("알림 일괄 읽기 성공 - batch 직접 실행")
  public void updateReadAllNotificationTest()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time",System.currentTimeMillis())
        .addString("userId",preSetupData.getUserId().toString())
        .toJobParameters();

    jobLauncher.run(readAllNoitificationsJob,jobParameters);

    Notification notification = notificationRepository.findByUserId(preSetupData.getUserId()).get().get(0);
    assertThat(notification).isNotNull();
    assertThat(notification.getId()).isEqualTo(preSetupData.getId());
    assertThat(notification.getUserId()).isEqualTo(preSetupData.getUserId());
    assertThat(notification.getConfirmedAt()).isNotEqualTo(preSetupData.getCreatedAt());
  }

  @Test
  @DisplayName("알림 일괄 읽기 성공 - service 통해 실행")
  public void updateAllNotificationServiceTest()
  {
    notificationService.updateAllReadState(preSetupData.getUserId());
    Notification notification = notificationRepository.findByUserId(preSetupData.getUserId()).get().get(0);
    assertThat(notification).isNotNull();
    assertThat(notification.getUserId()).isEqualTo(preSetupData.getUserId());
    assertThat(notification.getConfirmedAt()).isNotEqualTo(preSetupData.getCreatedAt());
  }
}
