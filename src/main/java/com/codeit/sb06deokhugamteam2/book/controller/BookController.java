package com.codeit.sb06deokhugamteam2.book.controller;

import com.codeit.sb06deokhugamteam2.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteSoft(
            @PathVariable UUID bookId
    ) {
        bookService.deleteSoft(bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
