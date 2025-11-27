package com.codeit.sb06deokhugamteam2.book.dto.data;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class BookDto {
    private UUID id;
    private String title;
    private String author;
    private String publisher;
    private String description;
    private LocalDate publishedDate;
    private String isbn;
    private String thumbnailUrl;
    private int reviewCount;
    private double rating;
    private Instant createdAt;
    private Instant updatedAt;
}