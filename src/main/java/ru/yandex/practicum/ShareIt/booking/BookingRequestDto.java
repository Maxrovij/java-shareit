package ru.yandex.practicum.ShareIt.booking;

import java.time.LocalDateTime;

public class BookingRequestDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
