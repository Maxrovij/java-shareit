package ru.yandex.practicum.ShareIt.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.request.RequestRepository;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final RequestRepository requestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.requestRepository = requestRepository;
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

    public ItemDto getById(Long itemId) {
        Optional<Item> maybeItem = itemRepository.findById(itemId);
        if (maybeItem.isPresent()) return toDto(maybeItem.get());
        throw new DataNotFoundException(String.format("Item with id %d not found!", itemId));
    }

    public Collection<ItemDto> getAllByOwnerId(Long ownerId) {
        return itemRepository.searchAllByOwnerId(ownerId)
                .stream()
                .map(this::toDto)
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
        if(i.getRequest() != null) {
            Optional<ItemRequest> maybeRequest = requestRepository.findById(i.getRequest());
            if(maybeRequest.isPresent()) itemRequest = maybeRequest.get();
            else throw new DataNotFoundException("Request not found!");
        }

        UserDto userDto = userService.getById(i.getOwner());

        return new ItemDto(
                i.getId(),
                i.getName(),
                i.getDescription(),
                i.isAvailable(),
                new ItemDto.User(userDto.getId(), userDto.getName()),
                itemRequest);
    }
}
