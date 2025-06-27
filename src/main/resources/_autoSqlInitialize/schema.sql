CREATE TABLE IF NOT EXISTS PHONEBOOK (
    user_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS TEST (
    c1 varchar(10),
    c2 varchar(10),
    c3 varchar(10)
);

create table PUBLIC.POST_EXAMPLE
(
    BOARD_ID       BIGINT NOT NULL,
    BOARD_NAME     CHARACTER VARYING(100) NOT NULL,
    POST_ID        BIGINT auto_increment,
    CREATE_AT      TIMESTAMP NOT NULL,
    UPDATE_AT      TIMESTAMP NOT NULL,

    USER_ID        BIGINT NOT NULL,
    USER_EMAIL     CHARACTER VARYING(255),
    USER_NAME      CHARACTER VARYING(255),

    TITLE          VARCHAR(200),
    CONTENT        TEXT,

    primary key (BOARD_ID, POST_ID),
    constraint FK_POSTEXAMPLE_USER
        foreign key (USER_ID) references PUBLIC.USERS (ID)
);

create table PUBLIC.POST_FILES
(
    BOARD_ID       BIGINT NOT NULL,
    POST_ID        BIGINT NOT NULL,

    FILE_NAME      CHARACTER VARYING(255),
    FILE_ORDER     INT,
    FILE_PATH      CHARACTER VARYING(255),

    constraint FK_POSTFILES_POST
        foreign key (BOARD_ID, POST_ID) references PUBLIC.POST_EXAMPLE (BOARD_ID, POST_ID)
);