package ru.yandex.practicum.ShareIt.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.ShareIt.booking.Booking;
import ru.yandex.practicum.ShareIt.request.ItemRequest;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;

    @Data
    public static class User {
        private final Long id;
        private final String name;
    }
}
