package ru.yandex.practicum.ShareIt.request;

import lombok.Data;
import ru.yandex.practicum.ShareIt.user.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private final Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
