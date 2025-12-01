package com.codeit.sb06deokhugamteam2.dashboard.entity;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dashboard")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "entity_id")
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "ranking_type")
    private RankingType rankingType;

    @Enumerated(EnumType.STRING)    // enum을 db에 문자열로 저장
    @Column(nullable = false, name = "period_type")
    private PeriodType periodType;

    @Builder.Default
    @Column(nullable = false)
    private long rank = 0L;

    @Builder.Default
    @Column(nullable = false)
    private double score = 0.0;

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;
}
