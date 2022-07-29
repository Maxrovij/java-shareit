package ru.yandex.practicum.ShareIt.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto add(@RequestBody BookingRequestDto bookingRequestDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.add(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setAvailable(@RequestParam boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId) {
        return bookingService.setAvailable(userId, bookingId, approved);
    }
}
