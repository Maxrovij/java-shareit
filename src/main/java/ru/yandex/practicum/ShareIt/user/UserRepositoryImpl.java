package ru.yandex.practicum.ShareIt.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public User add(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public void delete(Long id) {
        users.remove(id);
    }
}
