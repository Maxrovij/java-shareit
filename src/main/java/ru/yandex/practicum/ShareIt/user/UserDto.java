package ru.yandex.practicum.ShareIt.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;

    public UserDto() {

    }
}
