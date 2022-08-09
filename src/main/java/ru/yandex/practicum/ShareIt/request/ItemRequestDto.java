package ru.yandex.practicum.ShareIt.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.ShareIt.item.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private Collection<ItemDto> items;

    @Data
    static class User {
        private final Long id;
        private final String name;
    }
}
