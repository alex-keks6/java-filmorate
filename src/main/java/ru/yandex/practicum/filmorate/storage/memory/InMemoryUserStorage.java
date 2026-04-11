package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAll() {
        return users.values();
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public List<User> getFriendsById(Long id) {
        return users.get(id).getFriends().stream().map(this::getUserById).toList();
    }

    public boolean isUserExist(Long id) {
        return users.containsKey(id);
    }

    private Long getNextId() {
        long maxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++maxId;
    }
}
