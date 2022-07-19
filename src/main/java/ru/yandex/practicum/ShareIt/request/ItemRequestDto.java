package ru.yandex.practicum.ShareIt.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private final Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    @Data
    static class User {
        private final Long id;
        private final String name;
    }
}
