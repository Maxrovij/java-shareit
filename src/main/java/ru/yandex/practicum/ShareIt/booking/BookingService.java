package ru.yandex.practicum.ShareIt.booking;

import java.util.Collection;

public interface BookingService {
    BookingDto add(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto setAvailable(Long userId, Long bookingId, boolean available);

    BookingDto get(Long userId, Long bookingId);

    Collection<BookingDto> getAllForUser(Long userId, States state);
}
