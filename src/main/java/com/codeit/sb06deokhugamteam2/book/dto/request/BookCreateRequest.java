package com.codeit.sb06deokhugamteam2.book.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookCreateRequest {
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    private String description;
    @NotNull
    private String publisher;
    @NotNull
    private LocalDate publishedDate;
    @NotNull
    private String isbn;
}
