package ru.yandex.practicum.ShareIt.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.item.ItemDto;
import ru.yandex.practicum.ShareIt.item.ItemService;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*TODO
* Возможно стоит пересмотреть конвертацию в BookingDto. Как вариант - передавать в метод ItemDto */

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    public BookingDto add(BookingRequestDto bookingRequestDto, Long userId) {
        userService.getById(userId);
        ItemDto iDto = itemService.getById(bookingRequestDto.getItemId());

        if (!iDto.getAvailable()) throw new IncorrectDataException("Item is not available!");

        Instant now = Instant.now();
        Instant startOfBookingRequest = bookingRequestDto.getStart().toInstant(ZoneOffset.UTC);
        Instant endOfBookingRequest = bookingRequestDto.getEnd().toInstant(ZoneOffset.UTC);
        if(endOfBookingRequest.isBefore(now) ||
                endOfBookingRequest.isBefore(startOfBookingRequest) ||
                startOfBookingRequest.isBefore(now)) throw new IncorrectDataException("Incorrect booking dates!");

        Booking booking = new Booking(bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                iDto.getId(), userId,
                BookingStatus.WAITING);
        return toDto(bookingRepository.save(booking));
    }

    public BookingDto setAvailable(Long userId, Long bookingId, boolean available) {
        Optional<Booking> b = bookingRepository.findById(bookingId);
        if(b.isEmpty()) throw new DataNotFoundException("Booking not found!");

        ItemDto itemDto = itemService.getById(b.get().getItem());
        if(!itemDto.getOwner().getId().equals(userId)) throw new IncorrectDataException("Ur not the owner!");

        Booking booking = b.get();
        booking.setStatus(available ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return toDto(bookingRepository.save(booking));
    }

    public BookingDto get(Long userId, Long bookingId) {
        Optional<Booking> b = bookingRepository.findById(bookingId);
        if (b.isEmpty()) throw new DataNotFoundException("Booking not found!");

        Booking booking = b.get();
        ItemDto itemDto = itemService.getById(booking.getItem());

        if (userId.equals(booking.getBooker()) || userId.equals(itemDto.getOwner().getId())) {
            return toDto(booking);
        } else throw new IncorrectDataException("You must be booker or owner!");
    }

    @Override
    public Collection<BookingDto> getAllForUser(Long userId, States state) {
        List<Booking> bookings = bookingRepository.findByBooker_id(userId);
        switch (state) {
            case REJECTED:

            default:
                return bookings.stream()
                        .sorted((booking1, booking2) -> {
                            if (booking1.getStart().isBefore(booking2.getStart())) return 1;
                            if (booking1.getStart().isAfter(booking2.getStart())) return -1;
                            return 0;
                        }).map(this::toDto).collect(Collectors.toList());
        }


    }


    private BookingDto toDto(Booking b) {
        ItemDto itemDto = itemService.getById(b.getItem());
        UserDto userDto = userService.getById(b.getBooker());
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
