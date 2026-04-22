package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.database.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.database.DbUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;
    private final FilmService filmService;
    private final JdbcTemplate jdbc;

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setEmail("gena123@yandex.ru");
        user.setLogin("Gena_Baranov");
        user.setName("Gennadiy");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(user);
        user = userStorage.getUserById(1L);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testIsUserExist() {
        User user = new User();
        user.setEmail("gena123@yandex.ru");
        user.setLogin("Gena_Baranov");
        user.setName("Gennadiy");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(user);
        boolean isUserExist = userStorage.isUserExist(1L);
        Assertions.assertTrue(isUserExist);
    }

    @Test
    public void testAddFriendsAndGetAllFriends() {
        User user1 = new User();
        user1.setEmail("gena123@yandex.ru");
        user1.setLogin("Gena_Baranov");
        user1.setName("Gennadiy");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("vasia123@yandex.ru");
        user2.setLogin("Vasia_Baranov");
        user2.setName("Vasia");
        user2.setBirthday(LocalDate.of(1999, 2, 2));

        User user3 = new User();
        user3.setEmail("alex123@yandex.ru");
        user3.setLogin("Alex_Baranov");
        user3.setName("Alex");
        user3.setBirthday(LocalDate.of(1995, 3, 3));

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);
        userStorage.addFriend(2L, 1L);
        userStorage.removeFriend(1L, 3L);

        user2.setId(2L);

        List<User> friends = List.of(user2);

        Assertions.assertEquals(friends, userStorage.getFriendsById(1L));
    }

    @Test
    public void testGetFilmById() {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Very good film!");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(199L);
        film.setGenres(new TreeSet<>(List.of(new Genre(1L))));
        film.setMpa(new Mpa(1L));
        filmStorage.create(film);
        film = filmStorage.getFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testIsFilmExist() {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Very good film!");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(199L);
        film.setGenres(new TreeSet<>(List.of(new Genre(1L))));
        film.setMpa(new Mpa(1L));
        filmStorage.create(film);
        boolean isFilmExist = filmStorage.isFilmExist(1L);
        Assertions.assertTrue(isFilmExist);
    }

    @Test
    public void testAddLikesAndGetTopFilms() {
        User user1 = new User();
        user1.setEmail("gena123@yandex.ru");
        user1.setLogin("Gena_Baranov");
        user1.setName("Gennadiy");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("vasia123@yandex.ru");
        user2.setLogin("Vasia_Baranov");
        user2.setName("Vasia");
        user2.setBirthday(LocalDate.of(1999, 2, 2));

        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Very good film!");
        film1.setReleaseDate(LocalDate.of(1990, 1, 1));
        film1.setDuration(199L);
        film1.setGenres(new TreeSet<>(List.of(new Genre(1L))));
        film1.setMpa(new Mpa(1L));

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Very very good film!");
        film2.setReleaseDate(LocalDate.of(1995, 1, 1));
        film2.setDuration(199L);
        film2.setGenres(new TreeSet<>(List.of(new Genre(1L))));
        film2.setMpa(new Mpa(1L));

        Film film3 = new Film();
        film3.setName("Film3");
        film3.setDescription("Very very very good film!");
        film3.setReleaseDate(LocalDate.of(1999, 1, 1));
        film3.setDuration(199L);
        film3.setGenres(new TreeSet<>(List.of(new Genre(1L))));
        film3.setMpa(new Mpa(1L));

        userStorage.create(user1);
        userStorage.create(user2);

        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.create(film3);

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user2.getId());
        filmStorage.addLike(film3.getId(), user2.getId());
        filmStorage.removeLike(film2.getId(), user1.getId());

        film1.setLikes(new HashSet<>(List.of(1L, 2L)));
        film3.setLikes(new HashSet<>(List.of(2L)));

        List<Film> films = List.of(film1, film3, film2);

        Assertions.assertEquals(films, filmService.getPopularFilms(3));
    }
}