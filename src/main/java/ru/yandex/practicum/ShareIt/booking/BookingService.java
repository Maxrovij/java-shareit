package ru.yandex.practicum.ShareIt.booking;

public interface BookingService {
    BookingDto add(BookingRequestDto bookingRequestDto, Long userId);
}
