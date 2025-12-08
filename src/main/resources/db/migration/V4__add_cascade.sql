/* 1. BookStats: 기존 FK 제거 */
ALTER TABLE book_stats
DROP CONSTRAINT fk_books_to_bookstats;

/*    BookStats: FK 재생성 with ON DELETE CASCADE */
ALTER TABLE book_stats
    ADD CONSTRAINT book_stats_book_id_fkey
        FOREIGN KEY (book_id)
            REFERENCES books (id)
            ON DELETE CASCADE;


/* 2. ReviewStats: 기존 FK 제거 */
ALTER TABLE review_stats
DROP CONSTRAINT fk_reviews_to_reviewstats;

/*    ReviewStats: FK 재생성 with ON DELETE CASCADE */
ALTER TABLE review_stats
    ADD CONSTRAINT review_stats_review_id_fkey
        FOREIGN KEY (review_id)
            REFERENCES reviews (id)
            ON DELETE CASCADE;