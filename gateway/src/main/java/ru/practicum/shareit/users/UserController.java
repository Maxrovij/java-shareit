package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.IncorrectDataException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getName().isEmpty() || userDto.getName() == null)
            throw new IncorrectDataException("Name must be defined!");
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
            throw new IncorrectDataException("Email must be defined!");
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!userDto.getEmail().contains("@")) throw new IncorrectDataException("Invalid email!");
        }
        log.info("Adding new user with name {} and email {} .", userDto.getName(), userDto.getEmail());
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> editUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!userDto.getEmail().contains("@")) throw new IncorrectDataException("Invalid email!");
        }

        log.info("Editing user with id = {}", userId);
        userDto.setId(userId);
        return client.edit(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long userId) {
        log.info("Getting user by id = {}", userId);
        return client.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return client.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Deleting user with id = {}", userId);
        client.delete(userId);
    }
}
