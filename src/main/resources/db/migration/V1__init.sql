CREATE TABLE Books
(
    id             uuid      NOT NULL,
    title          varchar   NOT NULL,
    author         varchar   NOT NULL,
    description    varchar   NOT NULL,
    publisher      varchar   NOT NULL,
    published_date date      NOT NULL,
    isbn           varchar,
    thumbnail_url  varchar,
    review_count   integer   NOT NULL,
    rating_sum     integer   NOT NULL,
    created_at     timestamp NOT NULL,
    updated_at     timestamp NOT NULL,
    deleted        boolean   NOT NULL DEFAULT false,
    PRIMARY KEY (id)
);

CREATE TABLE BookStats
(
    book_id      uuid    NOT NULL,
    review_count integer NOT NULL,
    rating_sum   integer NOT NULL,
    PRIMARY KEY (book_id)
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
    id           uuid      NOT NULL,
    entity_id    uuid      NOT NULL,
    ranking_type varchar   NOT NULL,
    period_type  varchar   NOT NULL,
    rank         bigint    NOT NULL,
    score        float4    NOT NULL,
    created_at   timestamp NOT NULL,
    PRIMARY KEY (id)
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

CREATE TABLE ReviewStats
(
    review_id     uuid    NOT NULL,
    like_count    integer NOT NULL,
    comment_count integer NOT NULL,
    PRIMARY KEY (review_id)
);

CREATE TABLE Users
(
    id         uuid      NOT NULL,
    email      varchar   NOT NULL,
    nickname   varchar   NOT NULL,
    password   varchar   NOT NULL,
    created_at timestamp NOT NULL,
    deleted_at timestamp NULL,
    PRIMARY KEY (id)
);

ALTER TABLE Reviews
    ADD CONSTRAINT FK_Reviews_Book
        FOREIGN KEY (book_id)
            REFERENCES Books (id)
            ON DELETE CASCADE;

ALTER TABLE Comments
    ADD CONSTRAINT FK_Comments_Review
        FOREIGN KEY (review_id)
            REFERENCES Reviews (id)
            ON DELETE CASCADE;

ALTER TABLE ReviewLikes
    ADD CONSTRAINT FK_ReviewLikes_Review
        FOREIGN KEY (review_id)
            REFERENCES Reviews (id)
            ON DELETE CASCADE;

ALTER TABLE Reviews
    ADD CONSTRAINT FK_Reviews_User
        FOREIGN KEY (user_id)
            REFERENCES Users (id)
            ON DELETE CASCADE;

ALTER TABLE ReviewLikes
    ADD CONSTRAINT FK_ReviewLikes_User
        FOREIGN KEY (user_id)
            REFERENCES Users (id)
            ON DELETE CASCADE;

ALTER TABLE Comments
    ADD CONSTRAINT FK_Comments_User
        FOREIGN KEY (user_id)
            REFERENCES Users (id)
            ON DELETE CASCADE;

ALTER TABLE BookStats
    ADD CONSTRAINT FK_Books_TO_BookStats
        FOREIGN KEY (book_id)
            REFERENCES Books (id);

ALTER TABLE ReviewStats
    ADD CONSTRAINT FK_Reviews_TO_ReviewStats
        FOREIGN KEY (review_id)
            REFERENCES Reviews (id);

CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT    NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT    NOT NULL,
    CREATE_TIME      TIMESTAMP NOT NULL,
    START_TIME       TIMESTAMP DEFAULT NULL,
    END_TIME         TIMESTAMP DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     TIMESTAMP,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
        references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP,
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
        references BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
