package ru.yandex.practicum.ShareIt.user;

public class UserMapper {
    public static UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getName(), null);
    }
}
