package ru.practicum.shareit.items;

import java.util.Collection;

public interface ItemService {
    ItemDto addNew(ItemDto itemDto, Long id);

    ItemDto editItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getById(Long id, Long userId);

    Collection<ItemDto> getAllByOwnerId(Long ownerId);

    Collection<ItemDto> searchAvailableItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

    Collection<ItemDto> getAllByRequestId(Long id);
}
