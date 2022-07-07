package ru.yandex.practicum.ShareIt.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User add(User user);
    Optional<User> getById(Long id);
    Collection<User> getAll();
    void delete(Long id);
}
