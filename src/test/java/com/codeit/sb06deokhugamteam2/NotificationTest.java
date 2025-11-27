package com.codeit.sb06deokhugamteam2;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.sb06deokhugamteam2.notification.NotificationComponent;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationCreateRequest;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationUpdateRequest;
import com.codeit.sb06deokhugamteam2.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import java.lang.annotation.Documented;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

  private NotificationDto preSetupData;

  @BeforeEach
  void setup() {
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
}
