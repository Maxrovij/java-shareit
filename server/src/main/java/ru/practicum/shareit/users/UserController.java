package ru.practicum.shareit.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        log.info("Post request. dto.name: {}, dto.email: {}", userDto.getName(), userDto.getEmail());
        return userService.createNew(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Patch request. userId: {} dto.name: {}, dto.email: {}",userId, userDto.getName(), userDto.getEmail());
        userDto.setId(userId);
        return userService.patch(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Get request. userId: {}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Get all request.");
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Delete request. userId: {}", userId);
        userService.delete(userId);
    }
}
