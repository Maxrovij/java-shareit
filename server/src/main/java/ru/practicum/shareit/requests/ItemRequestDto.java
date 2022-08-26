package ru.practicum.shareit.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.items.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Collection<ItemDto> items;

    public ItemRequestDto() {

    }
    @Data
    static class User {
        private final Long id;
        private final String name;
    }
}
