package ru.practicum.shareit.users;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
