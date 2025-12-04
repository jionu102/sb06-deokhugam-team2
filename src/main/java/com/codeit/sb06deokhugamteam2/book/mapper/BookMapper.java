package com.codeit.sb06deokhugamteam2.book.mapper;

import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.data.PopularBookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookDto toDto(Book book) {
        return BookDto.builder()
                .isbn(book.getIsbn())
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .description(book.getDescription())
                .publishedDate(book.getPublishedDate())
                .reviewCount(book.getReviewCount())
                .rating(ratingOperation(book.getReviewCount(), book.getRatingSum()))
                .thumbnailUrl(book.getThumbnailUrl())
                .publisher(book.getPublisher())
                .build();
    }

    private double ratingOperation(int reviewCount, double ratingSum) {
        if (reviewCount > 0 && ratingSum > 0) {
            return ratingSum / reviewCount;
        }
        return 0.0;
    }

    public PopularBookDto toDto(Dashboard dashboard, Book book, PeriodType period) {

        double rating;

        if (book.getReviewCount() == 0) {
            rating = 0.0;
        } else {
            rating = (double) book.getRatingSum() / book.getReviewCount();
        }

        return PopularBookDto.builder()
                .id(dashboard.getId())
                .bookId(dashboard.getEntityId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .thumbnailUrl(book.getThumbnailUrl())
                .period(period)
                .rank(dashboard.getRank())
                .score(dashboard.getScore())
                .reviewCount(book.getReviewCount())
                .rating(rating)
                .createdAt(dashboard.getCreatedAt())
                .build();
    }
}
