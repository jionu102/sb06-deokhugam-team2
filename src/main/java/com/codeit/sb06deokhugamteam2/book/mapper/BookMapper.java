package com.codeit.sb06deokhugamteam2.book.mapper;

import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
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
}