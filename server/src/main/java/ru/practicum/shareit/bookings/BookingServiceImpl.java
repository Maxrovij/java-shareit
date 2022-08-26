package ru.practicum.shareit.bookings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectDataException;
import ru.practicum.shareit.items.ItemDto;
import ru.practicum.shareit.items.ItemService;
import ru.practicum.shareit.users.UserDto;
import ru.practicum.shareit.users.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    public BookingDto add(BookingRequestDto bookingRequestDto, Long userId) {
        userService.getById(userId);
        ItemDto iDto = itemService.getById(bookingRequestDto.getItemId(), null);

        if (iDto.getOwner().getId().equals(userId))
            throw new DataNotFoundException("Just set 'Available' to FALSE you stupid!");

        if (!iDto.getAvailable()) throw new IncorrectDataException("Item is not available!");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfBookingRequest = bookingRequestDto.getStart();
        LocalDateTime endOfBookingRequest = bookingRequestDto.getEnd();
        log.info("now: {}, requestStart: {}, requestEnd: {}", now, startOfBookingRequest, endOfBookingRequest);

        if (endOfBookingRequest.isBefore(startOfBookingRequest))
            throw new IncorrectDataException("Incorrect booking dates!");

        Booking booking = new Booking(
                startOfBookingRequest,
                endOfBookingRequest,
                iDto.getId(),
                userId,
                BookingStatus.WAITING);
        return toDto(bookingRepository.save(booking));
    }

    public BookingDto setAvailable(Long userId, Long bookingId, Boolean available) {

        Optional<Booking> b = bookingRepository.findById(bookingId);
        if (b.isEmpty()) throw new DataNotFoundException("Booking not found!");
        Booking booking = b.get();
        if (booking.getBookerId().equals(userId))
            throw new DataNotFoundException("Status can be changed only by owner!");
        if (booking.getStatus().equals(BookingStatus.APPROVED) && available)
            throw new IncorrectDataException("Status is already defined as 'APPROVED'");

        ItemDto itemDto = itemService.getById(booking.getItem(), null);
        if (!itemDto.getOwner().getId().equals(userId)) throw new IncorrectDataException("Ur not the owner!");

        booking.setStatus(available ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return toDto(bookingRepository.save(booking));
    }

    public BookingDto get(Long userId, Long bookingId) {
        Optional<Booking> b = bookingRepository.findById(bookingId);
        if (b.isEmpty()) throw new DataNotFoundException("Booking not found!");

        Booking booking = b.get();
        ItemDto itemDto = itemService.getById(booking.getItem(), null);

        if (userId.equals(booking.getBookerId()) || userId.equals(itemDto.getOwner().getId())) {
            return toDto(booking);
        } else throw new DataNotFoundException("You must be booker or owner!");
    }

    @Override
    public Collection<BookingDto> getAllForUser(Long userId, String state) {
        States s;
        try {
            s = States.valueOf(States.class, state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }

        userService.getById(userId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (s) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings);
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_idAndStatus(userId, String.valueOf(BookingStatus.REJECTED));
                return toCollectionDto(bookings);
            case WAITING:
                bookings = bookingRepository.findAllByBooker_idAndStatus(userId, String.valueOf(BookingStatus.WAITING));
                return toCollectionDto(bookings);
            case CURRENT:
                bookings = bookingRepository.findAllByBookerId(userId);
                log.info("getAllForUser(), 'CURRENT' state. now = {}", now);
                return toCollectionDto(bookings
                        .stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList()));
            case PAST:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings
                        .stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList()));
            case FUTURE:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings
                        .stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList()));
            default:
                return List.of();
        }
    }

    @Override
    public Collection<BookingDto> getAllForOwner(Long userId, String state) {

        Collection<ItemDto> allItems = itemService.getAllByOwnerId(userId);
        if (allItems.size() == 0) throw new DataNotFoundException("You don't have any items!");

        LocalDateTime now = LocalDateTime.now();
        List<Booking> allByOwner = bookingRepository.findAllByOwner(userId);

        switch (States.valueOf(States.class, state)) {
            case ALL:
                return toCollectionDto(allByOwner);
            case REJECTED:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList()));
            case WAITING:

                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList()));
            case CURRENT:
                log.info("getAllForOwner(), 'CURRENT' state. now = {}", now);
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStart().isBefore(now) &&
                                booking.getEnd().isAfter(now))
                        .collect(Collectors.toList()));
            case PAST:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList()));
            case FUTURE:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList()));
            default:
                return List.of();
        }
    }

    private Collection<BookingDto> toCollectionDto(List<Booking> bookings) {
        if (bookings.isEmpty()) throw new DataNotFoundException("Bookings not found!");
        return bookings.stream()
                .map(this::toDto)
                .sorted((booking1, booking2) -> {
                    if (booking1.getStart().isBefore(booking2.getStart())) return 1;
                    if (booking1.getStart().isAfter(booking2.getStart())) return -1;
                    return 0;
                }).collect(Collectors.toList());
    }

    private BookingDto toDto(Booking b) {
        ItemDto itemDto = itemService.getById(b.getItem(), null);
        UserDto userDto = userService.getById(b.getBookerId());
        return new BookingDto(
                b.getId(),
                b.getStart(),
                b.getEnd(),
                new BookingDto.Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription()),
                new BookingDto.User(userDto.getId(), userDto.getName()),
                b.getStatus()
        );
    }
}
