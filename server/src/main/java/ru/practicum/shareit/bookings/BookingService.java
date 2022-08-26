package ru.practicum.shareit.bookings;

import java.util.Collection;

public interface BookingService {
    BookingDto add(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto setAvailable(Long userId, Long bookingId, Boolean available);

    BookingDto get(Long userId, Long bookingId);

    Collection<BookingDto> getAllForUser(Long userId, String state);

    Collection<BookingDto> getAllForOwner(Long userId, String state);
}
