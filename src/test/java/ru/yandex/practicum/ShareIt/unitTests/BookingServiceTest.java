package ru.yandex.practicum.ShareIt.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.ShareIt.booking.*;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.item.ItemDto;
import ru.yandex.practicum.ShareIt.item.ItemService;
import ru.yandex.practicum.ShareIt.item.ItemServiceImpl;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookingServiceTest {
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    ItemService itemService = Mockito.mock(ItemServiceImpl.class);
    UserService userService = Mockito.mock(UserService.class);
    BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);
    UserDto userDto = new UserDto(1L, "user1", "emailUser1@ya.ru");

    Booking last = new Booking(
            1L,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1),
            1L,
            1L,
            BookingStatus.WAITING);
    Booking next = new Booking(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            1L,
            1L,
            BookingStatus.WAITING);

    ItemDto iDto = new ItemDto(
            1L,
            "iDto Name 1",
            "iDto Description 1",
            true,
            new ItemDto.User(2L, "iDTO User Name 1"),
            null,
            null,
            last,
            next,
            List.of()
    );

    BookingRequestDto bRDto = new BookingRequestDto();

    Booking booking = new Booking(
            1L,
            bRDto.getStart(),
            bRDto.getEnd(),
            iDto.getId(),
            1L,
            BookingStatus.WAITING);

    @Test
    public void shouldAddBooking() {
        bRDto.setStart(LocalDateTime.now().plusDays(1));
        bRDto.setEnd(LocalDateTime.now().plusDays(2));
        bRDto.setItemId(1L);
        booking.setStart(bRDto.getStart());
        booking.setEnd(bRDto.getEnd());

        Mockito
                .when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(itemService.getById(1L, null))
                .thenReturn(iDto);
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        BookingDto resultDto = bookingService.add(bRDto, 1L);

        Assertions.assertEquals(resultDto.getId(), 1L);
        Assertions.assertNotNull(resultDto.getBooker());
        Assertions.assertEquals(resultDto.getStart(), bRDto.getStart());
        Assertions.assertEquals(resultDto.getEnd(), bRDto.getEnd());
        Assertions.assertNotNull(resultDto.getItem());
        Assertions.assertEquals(resultDto.getStatus(), BookingStatus.WAITING);
    }

    @Test
    public void shouldThrowExceptionIfOwnerTriesToAddBooking() {
        userDto.setId(2L);
        bRDto.setStart(LocalDateTime.now().plusDays(1));
        bRDto.setEnd(LocalDateTime.now().plusDays(2));
        bRDto.setItemId(1L);
        Mockito
                .when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(itemService.getById(1L, null))
                .thenReturn(iDto);

        final DataNotFoundException e = Assertions.assertThrows(DataNotFoundException.class,
                ()-> bookingService.add(bRDto, 2L));
        Assertions.assertEquals(e.getMessage(), "Just set 'Available' to FALSE you stupid!");
    }

    @Test
    public void shouldThrowExceptionWhenIncorrectBookingDates() {
        bRDto.setStart(LocalDateTime.now().plusDays(2));
        bRDto.setEnd(LocalDateTime.now().plusDays(1));
        bRDto.setItemId(1L);
        Mockito
                .when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(itemService.getById(1L, null))
                .thenReturn(iDto);


        final IncorrectDataException e = Assertions.assertThrows(IncorrectDataException.class,
                ()-> bookingService.add(bRDto, 1L));
        Assertions.assertEquals(e.getMessage(), "Incorrect booking dates!");

        bRDto.setStart(LocalDateTime.now().minusDays(2));
        bRDto.setEnd(LocalDateTime.now().plusDays(1));
        final IncorrectDataException e1 = Assertions.assertThrows(IncorrectDataException.class,
                ()-> bookingService.add(bRDto, 1L));
        Assertions.assertEquals(e1.getMessage(), "Incorrect booking dates!");

        bRDto.setStart(LocalDateTime.now().minusDays(2));
        bRDto.setEnd(LocalDateTime.now().minusDays(1));
        final IncorrectDataException e2 = Assertions.assertThrows(IncorrectDataException.class,
                ()-> bookingService.add(bRDto, 1L));
        Assertions.assertEquals(e2.getMessage(), "Incorrect booking dates!");
    }

    @Test
    public void shouldSetAvailable() {
        Mockito
                .when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(itemService.getById(1L, null))
                .thenReturn(iDto);
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.setAvailable(2L, 1L, false);
        Assertions.assertEquals(bookingDto.getStatus(), BookingStatus.REJECTED);

        BookingDto bookingDto1 = bookingService.setAvailable(2L, 1L , true);
        Assertions.assertEquals(bookingDto1.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void shouldThrowExceptionWhenAvailableSetNotByOwner() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        final DataNotFoundException e = Assertions.assertThrows(DataNotFoundException.class,
                ()-> bookingService.setAvailable(1L, 1L, true));
        Assertions.assertEquals(e.getMessage(), "Status can be changed only by owner!");
    }

    @Test
    public void shouldThrowExceptionWhenBookingNotFound() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        final DataNotFoundException e = Assertions.assertThrows(DataNotFoundException.class,
                ()-> bookingService.setAvailable(1L, 3L, true));
        Assertions.assertEquals(e.getMessage(), "Booking not found!");
    }

    @Test
    public void shouldThrowExceptionWhenStatusAlreadyAPPROVED() {
        booking.setStatus(BookingStatus.APPROVED);
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        final IncorrectDataException e = Assertions.assertThrows(IncorrectDataException.class,
                ()-> bookingService.setAvailable(2L, 1L, true));
        Assertions.assertEquals(e.getMessage(), "Status is already defined as 'APPROVED'");
    }

    @Test
    public void shouldThrowExceptionWhenUserIsNotTheOwner() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(itemService.getById(1L, null))
                .thenReturn(iDto);
        final IncorrectDataException e = Assertions.assertThrows(IncorrectDataException.class,
                ()-> bookingService.setAvailable(3L, 1L, true));
        Assertions.assertEquals(e.getMessage(), "Ur not the owner!");
    }
}
