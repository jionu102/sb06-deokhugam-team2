CREATE TABLE Books
(
  id            uuid      NOT NULL,
  title         varchar   NOT NULL,
  author        varchar   NOT NULL,
  description   varchar   NOT NULL,
  publisher     varchar   NOT NULL,
  published_date date NOT NULL,
  isbn          varchar  ,
  thumbnail_url varchar  ,
  review_count  integer   NOT NULL,
  rating_sum    float4    NOT NULL,
  created_at    timestamp NOT NULL,
  updated_at    timestamp NOT NULL,
  deleted       boolean   NOT NULL DEFAULT false,
  PRIMARY KEY (id)
);

CREATE TABLE Comments
(
  id         uuid      NOT NULL,
  user_id    uuid      NOT NULL,
  review_id  uuid      NOT NULL,
  content    varchar   NOT NULL,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,
  deleted    boolean   NOT NULL DEFAULT false,
  PRIMARY KEY (id)
);

CREATE TABLE Dashboard
(
  ranking_type varchar NOT NULL,
  period_type  varchar NOT NULL,
  score        float4  NOT NULL,
  PRIMARY KEY (ranking_type, period_type)
);

COMMENT ON COLUMN Dashboard.ranking_type IS '인기도서, 인기 리뷰, 파워 유저';

COMMENT ON COLUMN Dashboard.period_type IS '일간, 주간, 월간, 역대';

CREATE TABLE Notifications
(
  id           uuid      NOT NULL,
  user_id      uuid      NOT NULL,
  review_id    uuid      NOT NULL,
  review_title varchar   NOT NULL,
  content      varchar   NOT NULL,
  confirmed_at timestamp NOT NULL,
  created_at   timestamp NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE ReviewLikes
(
  user_id   uuid      NOT NULL,
  review_id uuid      NOT NULL,
  liked_at  timestamp NOT NULL,
  PRIMARY KEY (user_id, review_id)
);

CREATE TABLE Reviews
(
  id            uuid      NOT NULL,
  book_id       uuid      NOT NULL,
  user_id       uuid      NOT NULL,
  rating        integer   NOT NULL,
  content       varchar   NOT NULL,
  like_count    integer   NOT NULL,
  comment_count integer   NOT NULL,
  created_at    timestamp NOT NULL,
  updated_at    timestamp NOT NULL,
  deleted       boolean   NOT NULL DEFAULT false,
  PRIMARY KEY (id)
);

CREATE TABLE Users
(
  id         uuid      NOT NULL,
  email      varchar   NOT NULL,
  nickname   varchar   NOT NULL,
  password   varchar   NOT NULL,
  created_at timestamp NOT NULL,
  deleted    boolean   NOT NULL DEFAULT false,
  PRIMARY KEY (id)
);

ALTER TABLE Reviews
  ADD CONSTRAINT FK_Books_TO_Reviews
    FOREIGN KEY (book_id)
    REFERENCES Books (id);

ALTER TABLE Comments
  ADD CONSTRAINT FK_Reviews_TO_Comments
    FOREIGN KEY (review_id)
    REFERENCES Reviews (id);

ALTER TABLE ReviewLikes
  ADD CONSTRAINT FK_Reviews_TO_ReviewLikes
    FOREIGN KEY (user_id)
    REFERENCES Reviews (id);

ALTER TABLE Reviews
  ADD CONSTRAINT FK_Users_TO_Reviews
    FOREIGN KEY (user_id)
    REFERENCES Users (id);

ALTER TABLE ReviewLikes
  ADD CONSTRAINT FK_Users_TO_ReviewLikes
    FOREIGN KEY (review_id)
    REFERENCES Users (id);

ALTER TABLE Comments
  ADD CONSTRAINT FK_Users_TO_Comments
    FOREIGN KEY (user_id)
    REFERENCES Users (id);
