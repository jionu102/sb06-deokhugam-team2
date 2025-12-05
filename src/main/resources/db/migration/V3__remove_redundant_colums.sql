/* ============================================
   V3 — Remove duplicated stat columns
   ============================================ */

/* 1. Books 테이블: review_count, rating_sum 컬럼 제거 */
ALTER TABLE books
DROP COLUMN review_count;

ALTER TABLE books
DROP COLUMN rating_sum;


/* 2. Reviews 테이블: like_count, comment_count 컬럼 제거 */
ALTER TABLE reviews
DROP COLUMN like_count;

ALTER TABLE reviews
DROP COLUMN comment_count;