package ru.yandex.practicum.ShareIt.integrationalTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.ShareIt.user.User;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceTest {
    private final EntityManager em;
    private final UserService userService;

    @AfterAll
    public void createSomeMoreUsers() {
        userService.createNew(new UserDto(null, "user4name", "user4@email.ru"));
        userService.createNew(new UserDto(null, "user5name", "user5@email.ru"));
        userService.createNew(new UserDto(null, "user6name", "user6@email.ru"));
    }
    @Test
    public void shouldAddNewUser() {
        UserDto userDto = new UserDto(null, "user1name", "user1@email.ru");
        UserDto responseUserDto = userService.createNew(userDto);
        Assertions.assertEquals(responseUserDto.getId(), 1);
        Assertions.assertEquals(responseUserDto.getName(), userDto.getName());
        Assertions.assertEquals(responseUserDto.getEmail(), userDto.getEmail());

        TypedQuery<User> query = em.createQuery(
                "select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", "user1@email.ru").getSingleResult();
        Assertions.assertEquals(user.getId(), responseUserDto.getId());
        Assertions.assertEquals(user.getName(), responseUserDto.getName());
        Assertions.assertEquals(user.getEmail(), responseUserDto.getEmail());

    }

    @Test
    public void shouldPatchUser() {
        UserDto userDto = new UserDto(1L, "user1nameUpdated", "user1Updated@email.ru");
        UserDto responseUserDto = userService.patch(userDto);
        Assertions.assertEquals(responseUserDto.getId(), 1L);
        Assertions.assertEquals(responseUserDto.getName(), "user1nameUpdated");
        Assertions.assertEquals(responseUserDto.getEmail(), "user1Updated@email.ru");

        TypedQuery<User> query = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1L).getSingleResult();
        Assertions.assertEquals(user.getId(), 1);
        Assertions.assertEquals(user.getName(), "user1nameUpdated");
        Assertions.assertEquals(user.getEmail(), "user1Updated@email.ru");
    }

    @Test
    public void shouldReturnUserDtoById() {
        UserDto userDto = userService.getById(1L);
        Assertions.assertEquals(userDto.getName(), "user1nameUpdated");
        Assertions.assertEquals(userDto.getEmail(), "user1Updated@email.ru");

        TypedQuery<User> query = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1L).getSingleResult();
        Assertions.assertEquals(user.getId(), 1);
        Assertions.assertEquals(user.getName(), "user1nameUpdated");
        Assertions.assertEquals(user.getEmail(), "user1Updated@email.ru");
    }

    @Test
    public void shouldReturnAllUsers() {
        userService.createNew(new UserDto(null, "user2name", "user2@email.ru"));
        userService.createNew(new UserDto(null, "user3name", "user3@email.ru"));

        Collection<UserDto> resultList = userService.getAll();
        Assertions.assertEquals(resultList.size(), 3);

        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        List<User> result = query.getResultList();
        Assertions.assertEquals(result.size(), 3);

        TypedQuery<User> query1 = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user1 = query1.setParameter("id", 1L).getSingleResult();
        TypedQuery<User> query2 = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user2 = query2.setParameter("id", 2L).getSingleResult();
        TypedQuery<User> query3 = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user3 = query3.setParameter("id", 3L).getSingleResult();

        Assertions.assertTrue(result.contains(user1));
        Assertions.assertTrue(result.contains(user2));
        Assertions.assertTrue(result.contains(user3));

        Assertions.assertEquals(user1.getEmail(), "user1Updated@email.ru");
        Assertions.assertEquals(user1.getName(), "user1nameUpdated");
        Assertions.assertEquals(user2.getEmail(), "user2@email.ru");
        Assertions.assertEquals(user2.getName(), "user2name");
        Assertions.assertEquals(user3.getEmail(), "user3@email.ru");
        Assertions.assertEquals(user3.getName(), "user3name");
    }

    @Test
    public void shouldDeleteUser() {
        userService.delete(1L);

        Collection<UserDto> resultList = userService.getAll();
        Assertions.assertEquals(resultList.size(), 2);

        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        List<User> result = query.getResultList();
        Assertions.assertEquals(result.size(), 2);

        TypedQuery<User> query2 = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user2 = query2.setParameter("id", 2L).getSingleResult();
        TypedQuery<User> query3 = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user3 = query3.setParameter("id", 3L).getSingleResult();

        Assertions.assertTrue(result.contains(user2));
        Assertions.assertTrue(result.contains(user3));

        userService.delete(3L);

        Collection<UserDto> resultList1 = userService.getAll();
        Assertions.assertEquals(resultList1.size(), 1);

        TypedQuery<User> query4 = em.createQuery("select u from User u", User.class);
        List<User> result4 = query4.getResultList();
        Assertions.assertEquals(result4.size(), 1);

        TypedQuery<User> query22 = em.createQuery(
                "select u from User u where u.id = :id", User.class);
        User user22 = query22.setParameter("id", 2L).getSingleResult();

        Assertions.assertTrue(result4.contains(user22));
    }

}
