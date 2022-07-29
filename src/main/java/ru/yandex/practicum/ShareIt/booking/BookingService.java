package ru.yandex.practicum.ShareIt.booking;

public interface BookingService {
    BookingDto add(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto setAvailable(Long userId, Long bookingId, boolean available);
}
