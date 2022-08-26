package ru.practicum.shareit.users;

import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
@Data
@Validated
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
