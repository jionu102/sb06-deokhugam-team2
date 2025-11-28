package com.codeit.sb06deokhugamteam2.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
public class BookUpdateRequest {
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String author;
    @NotNull
    @NotBlank
    @Length(max = 1000)
    private String description;
    @NotBlank
    @NotNull
    private String publisher;
    @NotNull
    private LocalDate publishedDate;
}