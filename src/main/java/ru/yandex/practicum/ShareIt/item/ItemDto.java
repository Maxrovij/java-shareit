package ru.yandex.practicum.ShareIt.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    @Data
    static class User {
        private final Long id;
        private final String name;
    }

    @Data
    static class ItemRequest {
        private final Long id;
        private String description;
        private User requestor;
    }
}
