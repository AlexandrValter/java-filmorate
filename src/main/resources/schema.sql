CREATE TABLE IF NOT EXISTS USERS
(
    ID       INTEGER AUTO_INCREMENT,
   LOGIN    CHARACTER VARYING(20) NOT NULL,
    EMAIL    CHARACTER VARYING(50) NOT NULL,
    NAME     CHARACTER VARYING(50),
    BIRTHDAY DATE,
    CONSTRAINT USERS_PK
        PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS MPA_RATING
(
    ID_MPA_RATING INT NOT NULL,
    MEANING_MPA   CHARACTER VARYING(50) NOT NULL,
    CONSTRAINT MPA_RATING_PK
        PRIMARY KEY (ID_MPA_RATING)
);

CREATE TABLE IF NOT EXISTS FILMS
(
    ID           INT NOT NULL AUTO_INCREMENT,
    NAME         CHARACTER VARYING(50) NOT NULL,
    DESCRIPTION  CHARACTER VARYING(255),
    RELEASE_DATE DATE,
    DURATION     CHARACTER VARYING(20),
    ID_MPA       INT,
    PRIMARY KEY (ID),
    CONSTRAINT FILMS_ID_MPA_FK
        FOREIGN KEY (ID_MPA) REFERENCES MPA_RATING (ID_MPA_RATING)
);

CREATE TABLE IF NOT EXISTS FEEDS
(
    TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    USER_ID INT NOT NULL,
    EVENT_TYPE VARCHAR(10) NOT NULL,
    OPERATION VARCHAR(10) NOT NULL,
    EVENT_ID INTEGER AUTO_INCREMENT,
    ENTITY_ID INT NOT NULL,
    CONSTRAINT FEEDS_PK
        PRIMARY KEY (EVENT_ID),
    CONSTRAINT FEEDS_USERS_FK
        FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE IF NOT EXISTS LIKES
(
    FILM_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    CONSTRAINT LIKES_PK
        PRIMARY KEY (FILM_ID, USER_ID),
    CONSTRAINT LIKES_USER_ID_FK
        FOREIGN KEY (USER_ID) REFERENCES USERS (ID),
    CONSTRAINT LIKES_FILM_ID_FK
        FOREIGN KEY (FILM_ID) REFERENCES FILMS (ID)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP
(
    FROM_USER_ID INT NOT NULL,
    TO_USER_ID   INT NOT NULL,
    IS_BILATERAL BOOLEAN NOT NULL,
    CONSTRAINT FRIENDSHIP_PK
        PRIMARY KEY (FROM_USER_ID, TO_USER_ID),
    CONSTRAINT FROM_USER_ID_FK
        FOREIGN KEY (FROM_USER_ID) REFERENCES USERS (ID),
    CONSTRAINT TO_USER_ID_FK
        FOREIGN KEY (TO_USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE IF NOT EXISTS GENRES
(
    GENRE_ID INT NOT NULL,
    NAME     CHARACTER VARYING(50) NOT NULL,
    CONSTRAINT GENRE_PK
        PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE
(
    FILM_ID  INT NOT NULL,
    GENRE_ID INT NOT NULL,
    CONSTRAINT FILM_GENRE_PK
        PRIMARY KEY (FILM_ID, GENRE_ID),
    CONSTRAINT GENRE_FILM_ID_FK
        FOREIGN KEY (FILM_ID) REFERENCES FILMS (ID),
    CONSTRAINT GENRE_ID_FK
        FOREIGN KEY (GENRE_ID) REFERENCES GENRES (GENRE_ID)
);