package ru.yandex.practicum.ShareIt.booking;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BookingRequestDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
