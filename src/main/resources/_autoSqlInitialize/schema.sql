CREATE TABLE IF NOT EXISTS PHONEBOOK (
    user_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS TEST (
    c1 varchar(10),
    c2 varchar(10),
    c3 varchar(10)
);

CREATE TABLE IF NOT EXISTS POST_EX (
                         BOARD_NAME VARCHAR(100) NOT NULL,
                         POST_ID BIGINT auto_increment NOT NULL,
                         CREATE_AT DATETIME NOT NULL,
                         UPDATE_AT DATETIME NOT NULL,

                         USER_ID BIGINT NOT NULL,
                         USER_EMAIL VARCHAR(255),
                         USER_NAME VARCHAR(255),

                         BASE_FILE_PATH VARCHAR(1024),

                         PRIMARY KEY (BOARD_NAME, POST_ID),
                         CONSTRAINT FK_POSTEX_USER FOREIGN KEY (USER_ID) REFERENCES USERS(ID)
);

CREATE TABLE IF NOT EXISTS POST_FILES (
                            BOARD_NAME VARCHAR(100) NOT NULL,
                            POST_ID BIGINT NOT NULL,
                            FILE_PATH_NAME VARCHAR(1024),

                            CONSTRAINT FK_POSTFILES_POST FOREIGN KEY (BOARD_NAME, POST_ID)
                                REFERENCES POST_EX (BOARD_NAME, POST_ID)
);