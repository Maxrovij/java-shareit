package ru.yandex.practicum.ShareIt.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.user.User;
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

    private Long nextId = 0L;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
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
        Item item = new Item(getNextId(), new User(owner.getId(), owner.getName(), owner.getEmail()));
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        return ItemMapper.toDto(itemRepository.add(item));
    }

    public ItemDto editItem(Long itemId, Long userId, ItemDto itemDto) {
        Optional<Item> maybeItem = itemRepository.getById(itemId);
        if (maybeItem.isPresent()) {
            Item itemToUpdate = maybeItem.get();

            if (!itemToUpdate.getOwner().getId().equals(userId))
                throw new DataNotFoundException("Ur not the owner!");

            if (itemDto.getName() != null && !itemDto.getName().isEmpty())
                itemToUpdate.setName(itemDto.getName());

            if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty())
                itemToUpdate.setDescription(itemDto.getDescription());

            if (itemDto.getAvailable() != null)
                itemToUpdate.setAvailable(itemDto.getAvailable());

            return ItemMapper.toDto(itemRepository.add(itemToUpdate));
        }
        throw new DataNotFoundException(String.format("Item with id %d not found!", itemId));
    }

    public ItemDto getById(Long itemId) {
        Optional<Item> maybeItem = itemRepository.getById(itemId);
        if (maybeItem.isPresent()) return ItemMapper.toDto(maybeItem.get());
        throw new DataNotFoundException(String.format("Item with id %d not found!", itemId));
    }

    public Collection<ItemDto> getAllByOwnerId(Long ownerId) {
        return itemRepository.getAll()
                .stream()
                .filter((item) -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> searchAvailableItems(String text) {
        if (text.equals("")) return List.of();
        String t = text.toLowerCase().trim();
        return itemRepository.getAll()
                .stream()
                .filter((item) -> item.getName().toLowerCase().contains(t)
                        || item.getDescription().toLowerCase().contains(t)
                        && item.isAvailable())
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        nextId = nextId + 1;
        return nextId;
    }
}
