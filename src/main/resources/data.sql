--INSERT INTO users(email, login, name, birthday)
--VALUES ('victor@mail.ru', 'victor_first', 'Victor', '1990-01-01');
--INSERT INTO users(email, login, name, birthday)
--VALUES ('gena@mail.ru', 'gena_second', 'Gena', '2000-02-02');
--INSERT INTO users(email, login, name, birthday)
--VALUES ('tolik@mail.ru', 'tolik_third', 'Tolik', '1999-03-03');

INSERT INTO rating(name)
VALUES ('G');
INSERT INTO rating(name)
VALUES ('PG');
INSERT INTO rating(name)
VALUES ('PG-13');
INSERT INTO rating(name)
VALUES ('R');
INSERT INTO rating(name)
VALUES ('NC-17');

--INSERT INTO films(name, description, release_date, duration, rating_id)
--VALUES ('film1', 'Test film1 description', '1990-01-01', 210, 1);
--INSERT INTO films(name, description, release_date, duration, rating_id)
--VALUES ('film2', 'Test film2 description', '2000-02-02', 220, 2);
--INSERT INTO films(name, description, release_date, duration, rating_id)
--VALUES ('film3', 'Test film3 description', '2010-03-03', 230, 3);

--INSERT INTO friendship(user_id, friend_id, is_accept)
--VALUES (1, 2, TRUE);
--INSERT INTO friendship(user_id, friend_id, is_accept)
--VALUES (1, 3, FALSE);
--INSERT INTO friendship(user_id, friend_id, is_accept)
--VALUES (3, 2, TRUE);

--INSERT INTO film_like(film_id, user_id)
--VALUES (1, 1);
--INSERT INTO film_like(film_id, user_id)
--VALUES (1, 2);
--INSERT INTO film_like(film_id, user_id)
--VALUES (2, 3);

INSERT INTO genre(name)
VALUES ('Комедия');
INSERT INTO genre(name)
VALUES ('Драма');
INSERT INTO genre(name)
VALUES ('Мультфильм');
INSERT INTO genre(name)
VALUES ('Триллер');
INSERT INTO genre(name)
VALUES ('Документальный');
INSERT INTO genre(name)
VALUES ('Боевик');

--INSERT INTO film_genre(film_id, genre_id)
--VALUES (1, 1);
--INSERT INTO film_genre(film_id, genre_id)
--VALUES (1, 6);
--INSERT INTO film_genre(film_id, genre_id)
--VALUES (2, 3);
--INSERT INTO film_genre(film_id, genre_id)
--VALUES (2, 4);
--INSERT INTO film_genre(film_id, genre_id)
--VALUES (3, 5);