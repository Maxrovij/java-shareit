package ru.yandex.practicum.ShareIt.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @Data
    static class Item {
        private final Long id;
        private final String name;
        private final String description;
    }

    @Data
    static class User {
        private final Long id;
        private final String name;
    }
}
