MERGE INTO GENRES KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO GENRES KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO GENRES KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO GENRES KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO GENRES KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO GENRES KEY (genre_id) VALUES (6, 'Боевик');
MERGE INTO MPA_RATING KEY (ID_MPA_RATING) VALUES (1, 'G');
MERGE INTO MPA_RATING KEY (ID_MPA_RATING) VALUES (2, 'PG');
MERGE INTO MPA_RATING KEY (ID_MPA_RATING) VALUES (3, 'PG-13');
MERGE INTO MPA_RATING KEY (ID_MPA_RATING) VALUES (4, 'R');
MERGE INTO MPA_RATING KEY (ID_MPA_RATING) VALUES (5, 'NC-17');
INSERT INTO DIRECTORS(NAME) VALUES ('J.F. CAMERON');
INSERT INTO DIRECTORS(NAME) VALUES ('R. SCOTT');
INSERT INTO DIRECTORS(NAME) VALUES ('P. VERHOEVEN');

MERGE INTO MPA_RATING KEY (ID_MPA_RATING) VALUES (5, 'NC-17');