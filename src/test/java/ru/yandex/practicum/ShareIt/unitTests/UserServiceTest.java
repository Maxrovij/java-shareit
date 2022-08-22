package ru.yandex.practicum.ShareIt.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.ShareIt.exceptions.DataAlreadyExistsException;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.user.User;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserRepository;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    final UserService userService = new UserService(userRepository);

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "userDto Name", "UserDto@Email.com");
        userDto = new UserDto(1L, "userDto Name", "UserDto@Email.com");
    }

    @Test
    public void shouldAddUser() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto result = userService.createNew(userDto);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    public void shouldThrowExceptionWhenIncorrectFields() {
        userDto.setName("");

        final IncorrectDataException exception = Assertions.assertThrows(
                IncorrectDataException.class, () -> userService.createNew(userDto));
        Assertions.assertEquals(exception.getMessage(), "Invalid name");

        userDto.setEmail("mail.ru");
        final IncorrectDataException exception1 = Assertions.assertThrows(
                IncorrectDataException.class, () -> userService.createNew(userDto));
        Assertions.assertEquals(exception1.getMessage(), "Invalid email!");

        userDto.setEmail("");
        final IncorrectDataException exception2 = Assertions.assertThrows(
                IncorrectDataException.class, () -> userService.createNew(userDto));
        Assertions.assertEquals(exception2.getMessage(), "Missing email!");
    }

    @Test
    public void shouldThrowExceptionWhenEmailAlreadyUsed() {
        Mockito.when(userRepository.save(Mockito.any())).thenThrow(new DataIntegrityViolationException("E"));

        final DataAlreadyExistsException exception = Assertions.assertThrows(
                DataAlreadyExistsException.class, () -> userService.createNew(userDto));
        Assertions.assertEquals(exception.getMessage(), "This email is already used!");
    }

    @Test
    public void shouldPatchUser() {
        userDto.setName("Updated");
        userDto.setEmail("email@Updated.com");
        User userEdited = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userEdited);

        UserDto result = userService.patch(userDto);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        final  DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, ()-> userService.patch(userDto));
        Assertions.assertEquals(exception.getMessage(), String.format("User with id %d not found!", userDto.getId()));
    }

    @Test
    public void shouldGetUserById() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(user.getId());
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFoundById() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        final  DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, ()-> userService.getById(userDto.getId()));
        Assertions.assertEquals(exception.getMessage(), String.format("User with id %d not found!", userDto.getId()));
    }

    @Test
    public void shouldReturnAllUsers() {
        User user1 = new User(2L, "userDto 2 Name", "UserDto2@Email.com");
        User user2 = new User(3L, "userDto 3 Name", "UserDto3@Email.com");

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, user1, user2));

        List<UserDto> result = userService.getAll()
                .stream()
                .sorted(Comparator.comparing(UserDto::getId))
                .collect(Collectors.toList());

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(result.get(0).getId(), 1);
        Assertions.assertEquals(result.get(0).getName(), "userDto Name");
        Assertions.assertEquals(result.get(0).getEmail(), "UserDto@Email.com");
        Assertions.assertEquals(result.get(1).getId(), 2);
        Assertions.assertEquals(result.get(1).getName(), "userDto 2 Name");
        Assertions.assertEquals(result.get(1).getEmail(), "UserDto2@Email.com");
        Assertions.assertEquals(result.get(2).getId(), 3);
        Assertions.assertEquals(result.get(2).getName(), "userDto 3 Name");
        Assertions.assertEquals(result.get(2).getEmail(), "UserDto3@Email.com");
    }
}
