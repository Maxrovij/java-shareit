package ru.practicum.shareit.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("update User u set u.name=?1, u.email=?2 where u.id=?3")
    void patchUser(String name, String email, Long userId);
}
