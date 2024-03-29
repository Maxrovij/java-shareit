package ru.practicum.shareit.bookings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GlobalVars;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto add(@RequestBody BookingRequestDto bookingRequestDto,
                          @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        log.info("Post запрос. ID пользователя: {}. Start: {}, End: {}, Item: {}",
                userId,
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                bookingRequestDto.getItemId());
        return bookingService.add(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setAvailable(@RequestParam Boolean approved,
                                   @RequestHeader(GlobalVars.USER_HEADER) Long userId,
                                   @PathVariable Long bookingId) {
        log.info("Patch запрос. ID пользователя: {}, bookingId: {}, approved = {}", userId, bookingId, approved);
        return bookingService.setAvailable(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(GlobalVars.USER_HEADER) Long userId, @PathVariable Long bookingId) {
        log.info("Get запрос. ID пользователя: {}, BookingId: {}", userId, bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllForUser(
            @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get запрос. ID пользователя: {}, state: {}", userId, state);
        return bookingService.getAllForUser(userId, state, size, from);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllForOwner(
            @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get/owner запрос. ID пользователя: {}, state: {}", userId, state);
        return bookingService.getAllForOwner(userId, state, size, from);
    }
}
