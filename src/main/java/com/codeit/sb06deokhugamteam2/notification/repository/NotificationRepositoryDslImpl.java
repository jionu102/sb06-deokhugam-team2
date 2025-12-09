package com.codeit.sb06deokhugamteam2.notification.repository;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.entity.QNotification;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificaionCursorDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class NotificationRepositoryDslImpl implements NotificationRepositoryDsl{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Notification> findAllByUserId(NotificaionCursorDto dto) {
    QNotification n = QNotification.notification;

    return queryFactory.selectFrom(n)
        .where(
            n.userId.eq(dto.userId()),
            whereCondition(dto, n)
        )
        .orderBy(orderByDirection(dto.direction(),n.createdAt), orderByDirection(dto.direction(),n.id))
        .limit(dto.limit() + 1)
        .fetch();
  }

  private BooleanExpression whereCondition(NotificaionCursorDto dto, QNotification n) {
    if (dto == null
        || dto.after() == null
        || dto.cursor() == null
        || StringUtils.hasText(dto.cursor()) == false)
      return null;

    OrderSpecifier orderSpecifier = orderByDirection(dto.direction(), n.createdAt);
    if(orderSpecifier.getOrder().equals(Order.DESC))
    {
      return n.createdAt.lt(dto.after())
          .or(
              n.createdAt.eq(dto.after())
                  .and(n.id.loe(UUID.fromString(dto.cursor())))
          );
    }
    else
    {
      return n.createdAt.gt(dto.after())
          .or(
              n.createdAt.eq(dto.after())
                  .and(n.id.goe(UUID.fromString(dto.cursor())))
          );
    }
  }

  private OrderSpecifier orderByDirection(String direction, ComparableExpressionBase field) {
    if(StringUtils.hasText(direction) == false || direction.equalsIgnoreCase("desc")) {
      return field.desc();
    }

    return field.asc();
  }
}
