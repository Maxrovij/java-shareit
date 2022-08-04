package ru.yandex.practicum.ShareIt.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.booking.Booking;
import ru.yandex.practicum.ShareIt.booking.BookingRepository;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.request.RequestRepository;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           RequestRepository requestRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.bookingRepository = bookingRepository;
    }

    public ItemDto addNew(ItemDto itemDto, Long userId) {
        if (userId == null)
            throw new IncorrectDataException("Missing user id!");

        if (itemDto.getName() == null || itemDto.getName().isEmpty())
            throw new IncorrectDataException("Missing name!");
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty())
            throw new IncorrectDataException("Missing description!");
        if (itemDto.getAvailable() == null)
            throw new IncorrectDataException("Missing 'Available' parameter!");

        UserDto owner = userService.getById(userId);
        Item item = new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner.getId(),
                itemDto.getRequest() != null ? itemDto.getRequest().getId() : null);

        return toDto(itemRepository.save(item));
    }

    public ItemDto editItem(Long itemId, Long userId, ItemDto itemDto) {
        Optional<Item> maybeItem = itemRepository.findById(itemId);
        if (maybeItem.isEmpty()) throw new DataNotFoundException(String.format("Item with id %d not found!", itemId));

        Item itemToUpdate = maybeItem.get();

        if (!itemToUpdate.getOwner().equals(userId))
            throw new DataNotFoundException("Ur not the owner!");

        if (itemDto.getName() != null && !itemDto.getName().isEmpty())
            itemToUpdate.setName(itemDto.getName());

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty())
            itemToUpdate.setDescription(itemDto.getDescription());

        if (itemDto.getAvailable() != null)
            itemToUpdate.setAvailable(itemDto.getAvailable());

        return toDto(itemRepository.save(itemToUpdate));

    }

    public ItemDto getById(Long itemId, Long userId) {
        Optional<Item> maybeItem = itemRepository.findById(itemId);
        if (maybeItem.isEmpty()) throw new DataNotFoundException(String.format("Item with id %d not found!", itemId));
        ItemDto itemDto = toDto(maybeItem.get());

        if (itemDto.getOwner().getId().equals(userId)) {
            return setBookings(itemDto);
        }

        return itemDto;
    }

    public Collection<ItemDto> getAllByOwnerId(Long ownerId) {
        return itemRepository.searchAllByOwnerId(ownerId)
                .stream()
                .map(this::toDto)
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> searchAvailableItems(String text) {
        if (text.equals("")) return List.of();
        String t = text.toLowerCase().trim();
        return itemRepository.searchByText(t)
                .stream()
                .filter(Item::isAvailable)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ItemDto toDto(Item i) {
        ItemRequest itemRequest = null;
        if (i.getRequest() != null) {
            Optional<ItemRequest> maybeRequest = requestRepository.findById(i.getRequest());
            if (maybeRequest.isPresent()) itemRequest = maybeRequest.get();
            else throw new DataNotFoundException("Request not found!");
        }

        UserDto userDto = userService.getById(i.getOwner());

        return new ItemDto(
                i.getId(),
                i.getName(),
                i.getDescription(),
                i.isAvailable(),
                new ItemDto.User(userDto.getId(), userDto.getName()),
                itemRequest,
                null,
                null);
    }

    private ItemDto setBookings(ItemDto itemDto) {
        List<Booking> allForItem = bookingRepository.findAllByItem_Id(itemDto.getId());
        LocalDateTime now = LocalDateTime.now();
        if (!allForItem.isEmpty()) {
            Booking last = allForItem.get(0);
            Booking next = allForItem.get(allForItem.size() - 1);
            for (Booking b : allForItem) {
                if (b.getEnd().isBefore(now) && b.getEnd().isAfter(last.getEnd())) last = b;
                if (b.getStart().isAfter(now) && b.getStart().isBefore(next.getStart())) next = b;
            }
            itemDto.setLastBooking(last);
            itemDto.setNextBooking(next);
        }
        return itemDto;
    }
}
