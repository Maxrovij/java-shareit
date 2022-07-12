package ru.yandex.practicum.ShareIt.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        return userService.createNew(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return userService.patch(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
