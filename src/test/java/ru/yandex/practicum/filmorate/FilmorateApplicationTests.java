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
}