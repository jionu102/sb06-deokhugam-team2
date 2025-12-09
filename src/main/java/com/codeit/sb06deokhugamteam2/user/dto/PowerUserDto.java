package com.codeit.sb06deokhugamteam2.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//public record PowerUserDto(
//        UUID userId,
//        String nickname,
//        String period,
//        LocalDateTime createdAt,
//        long rank,
//        double score,
//        double reviewScoreSum,
//        long likeCount,
//        long commentCount
//) {
//}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PowerUserDto {
  UUID userId;
  String nickname;
  String period;
  LocalDateTime createdAt;
  long rank;
  double score;
  double reviewScoreSum;
  long likeCount;
  long commentCount;
}
