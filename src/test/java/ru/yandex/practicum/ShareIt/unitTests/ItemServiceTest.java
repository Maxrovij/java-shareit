package ru.yandex.practicum.ShareIt.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.ShareIt.booking.BookingRepository;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.item.*;
import ru.yandex.practicum.ShareIt.request.RequestRepository;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ItemServiceTest {
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    final ItemService itemService = new ItemServiceImpl(itemRepository,
            userService,
            requestRepository,
            bookingRepository,
            commentRepository);

    UserDto owner;
    Item item;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = new UserDto(1L, "Owner Name", "Owner@email.ru");

        item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);
        item.setOwner(owner.getId());

        itemDto = new ItemDto();
        itemDto.setName("item name");
        itemDto.setDescription("item description");
        itemDto.setAvailable(true);
        itemDto.setOwner(new ItemDto.User(1L, "Owner Name"));
    }

    @Test
    public void shouldAddNEwItem() {
        Mockito.when(userService.getById(1L)).thenReturn(owner);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto result = itemService.addNew(itemDto, owner.getId());

        Assertions.assertEquals(result.getId(), 1L);
        Assertions.assertEquals(result.getName(), item.getName());
        Assertions.assertEquals(result.getDescription(), item.getDescription());
        Assertions.assertTrue(result.getAvailable());
    }

    @Test
    public void shouldThrowExceptionWhenWrongFieldsOrParams() {
        final IncorrectDataException exception = Assertions.assertThrows(
                IncorrectDataException.class, () -> itemService.addNew(itemDto, null));
        Assertions.assertEquals(exception.getMessage(), "Missing user id!");

        itemDto.setAvailable(null);
        final IncorrectDataException exception1 = Assertions.assertThrows(
                IncorrectDataException.class, () -> itemService.addNew(itemDto, 1L));
        Assertions.assertEquals(exception1.getMessage(), "Missing 'Available' parameter!");

        itemDto.setDescription("");
        final IncorrectDataException exception2 = Assertions.assertThrows(
                IncorrectDataException.class, () -> itemService.addNew(itemDto, 1L));
        Assertions.assertEquals(exception2.getMessage(), "Missing description!");

        itemDto.setName("");
        final IncorrectDataException exception3 = Assertions.assertThrows(
                IncorrectDataException.class, () -> itemService.addNew(itemDto, 1L));
        Assertions.assertEquals(exception3.getMessage(), "Missing name!");
    }

    @Test
    public void shouldEditItem() {
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));

        itemDto.setName("Updated");
        itemDto.setDescription("Updated description");
        itemDto.setAvailable(false);

        Item itemUpdated = new Item();
        itemUpdated.setId(1L);
        itemUpdated.setName(itemDto.getName());
        itemUpdated.setDescription(itemDto.getDescription());
        itemUpdated.setAvailable(itemDto.getAvailable());
        itemUpdated.setOwner(owner.getId());

        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(itemUpdated);
        Mockito.when(userService.getById(1L)).thenReturn(owner);

        ItemDto result = itemService.editItem(item.getId(), owner.getId(), itemDto);

        Assertions.assertEquals(result.getId(), itemUpdated.getId());
        Assertions.assertEquals(result.getName(), itemUpdated.getName());
        Assertions.assertEquals(result.getDescription(), itemUpdated.getDescription());
        Assertions.assertFalse(result.getAvailable());
    }

    @Test
    public void shouldThrowExceptionWhenEditedNotByOwner() {
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, ()-> itemService.editItem(item.getId(), 2L, itemDto));

        Assertions.assertEquals(exception.getMessage(), "Ur not the owner!");
    }

    @Test
    public void shouldGetByIdForAnyUser() {
        Mockito.when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        Mockito.when(userService.getById(1L)).thenReturn(owner);

        ItemDto result = itemService.getById(item.getId(), 2L);

        Assertions.assertEquals(result.getId(), 1L);
        Assertions.assertEquals(result.getName(), item.getName());
        Assertions.assertEquals(result.getDescription(), item.getDescription());
        Assertions.assertTrue(result.getAvailable());
    }

    @Test
    public void shouldReturnAllWithBookingsForOwner() {
        Mockito.when(itemRepository.searchAllByOwnerId(Mockito.any())).thenReturn(List.of(item));
        Mockito.when(userService.getById(1L)).thenReturn(owner);
        List<ItemDto> result = new ArrayList<>(itemService.getAllByOwnerId(1L));

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getName(), item.getName());
        Assertions.assertEquals(result.get(0).getDescription(), item.getDescription());
    }

    @Test
    public void shouldReturnAllByRequestId() {
        Mockito.when(itemRepository.findAllByRequestId(Mockito.any())).thenReturn(List.of(item));
        Mockito.when(userService.getById(1L)).thenReturn(owner);

        List<ItemDto> result = new ArrayList<>(itemService.getAllByRequestId(666L));
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getName(), item.getName());
        Assertions.assertEquals(result.get(0).getDescription(), item.getDescription());
    }

    @Test
    public void shouldSearchAvailable() {
        Item anotherItem = new Item();
        anotherItem.setId(2L);
        anotherItem.setName("anotherItem name");
        anotherItem.setDescription("anotherItem description");
        anotherItem.setAvailable(true);
        anotherItem.setOwner(owner.getId());

        Mockito.when(itemRepository.searchByText(Mockito.any())).thenReturn(List.of(item, anotherItem));
        Mockito.when(userService.getById(1L)).thenReturn(owner);

        List<ItemDto> result = new ArrayList<>(itemService.searchAvailableItems("666L"));
        Assertions.assertEquals(result.size(), 2);

        item.setAvailable(false);

        List<ItemDto> result1 = new ArrayList<>(itemService.searchAvailableItems("666L"));
        Assertions.assertEquals(result1.size(), 1);
    }

    @Test
    public void shouldReturnEmptyListIfSearchTextIsEmpty() {
        Collection<ItemDto> result = itemService.searchAvailableItems("");
        Assertions.assertTrue(result.isEmpty());
    }
}
