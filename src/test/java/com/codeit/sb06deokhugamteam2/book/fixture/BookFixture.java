package com.codeit.sb06deokhugamteam2.book.fixture;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.entity.BookStats;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class BookFixture {
    public static Book createBook(int count) {
        Book book = Book.builder()
                .thumbnailUrl("thumbnail" + count + "url")
                .title("title" + count)
                .description("description" + count)
                .publisher("publisher" + count)
                .isbn(String.format("%013d", count))
                .publishedDate(LocalDate.now())
                .author("author" + count)
                .build();
        BookStats bookStats = createBookStats(book);
        book.setBookStats(bookStats);
        return book;
    }

    public static List<Book> createBooks(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(BookFixture::createBook)
                .toList();
    }

    private static BookStats createBookStats(Book book) {
        BookStats bookStats = new BookStats();
        bookStats.setBook(book);
        return bookStats;
    }
}
