package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GlobalVars;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient client;

	@PostMapping
	public ResponseEntity<Object> bookItem(@Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return client.addBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setApproved(@Positive @PathVariable Long bookingId,
											  @RequestParam Boolean approved,
											  @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
		return client.setApproved(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(GlobalVars.USER_HEADER) Long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return client.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsForUser(
			@RequestHeader(GlobalVars.USER_HEADER) Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking for user with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return client.getBookings("", userId, stateParam, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsForOwner(
			@RequestHeader(GlobalVars.USER_HEADER) Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		log.info("Getting bookings for owner with id {} and state {}", userId, stateParam);
		return client.getBookings("/owner", userId, stateParam, from, size);
	}
}
