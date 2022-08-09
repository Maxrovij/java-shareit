package ru.yandex.practicum.ShareIt.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final CommentRepository commentRepository;

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           RequestRepository requestRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
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
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null);

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

    public Collection<ItemDto> getAllByRequestId(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return items.stream().map(this::toDto).collect(Collectors.toList());
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

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {

        if (itemRepository.findById(itemId).isEmpty()) throw new DataNotFoundException("Item not found!");

        if (commentDto.getText().isEmpty()) throw new IncorrectDataException("Write something!");

        List<Booking> itemBookings = bookingRepository.findAllByItem_Id(itemId);
        if (itemBookings.size() == 0) throw new DataNotFoundException("Item have never been booked!");

        LocalDateTime now = LocalDateTime.now();
        if (validateItemBooking(itemBookings, userId, now)) {
            Comment comment = commentRepository.save(new Comment(
                    null,
                    commentDto.getText(),
                    itemId,
                    userId,
                    now));
            return commentToDto(comment);
        }
        throw new IncorrectDataException("Not valid comment");
    }

    private ItemDto toDto(Item i) {
        ItemRequest itemRequest = null;
        if (i.getRequest() != null) {
            Optional<ItemRequest> maybeRequest = requestRepository.findById(i.getRequest());
            if (maybeRequest.isPresent()) itemRequest = maybeRequest.get();
            else throw new DataNotFoundException("Request not found!");
        }

        UserDto userDto = userService.getById(i.getOwner());

        List<Comment> comments = commentRepository.findAllByItemId(i.getId());

        return new ItemDto(
                i.getId(),
                i.getName(),
                i.getDescription(),
                i.isAvailable(),
                new ItemDto.User(userDto.getId(), userDto.getName()),
                i.getRequest(),
                itemRequest,
                null,
                null,
                comments.size() == 0 ? List.of() : comments.stream()
                        .map(this::commentToDto)
                        .collect(Collectors.toList()));
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
            log.info("setBookings(). now: {}, lastBooking.end: {}, nextBooking.start: {}",
                    now, last.getEnd(), next.getStart());
            itemDto.setLastBooking(last);
            itemDto.setNextBooking(next);
        }
        return itemDto;
    }

    private boolean validateItemBooking(List<Booking> bookings, Long userId, LocalDateTime now) {
        boolean valid = false;
        for (Booking b : bookings) {
            if (b.getBookerId().equals(userId) && b.getEnd().isBefore(now)) valid = true;
        }
        return valid;
    }

    private CommentDto commentToDto(Comment comment) {
        UserDto userDto = userService.getById(comment.getAuthor());
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                userDto.getName(),
                comment.getCreated());
    }
}
