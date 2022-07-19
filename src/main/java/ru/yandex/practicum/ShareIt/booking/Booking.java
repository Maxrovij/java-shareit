package ru.yandex.practicum.ShareIt.booking;

import lombok.Data;
import ru.yandex.practicum.ShareIt.item.Item;
import ru.yandex.practicum.ShareIt.user.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private final Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
