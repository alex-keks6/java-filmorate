package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User newUser);

    User getUserById(Long id);

    List<User> getFriendsById(Long id);

    boolean isUserExist(Long id);
}
