package ru.yandex.practicum.ShareIt.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class User {
    private final Long id;
    private String name;
    private String email;
}
