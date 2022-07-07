package ru.yandex.practicum.ShareIt.item;

import java.util.Collection;

public interface ItemService {
    Item addNew(ItemDto itemDto, Long id);
    Item editItem(Long itemId, Long userId, ItemDto itemDto);
    Item getById(Long id);
    Collection<Item> getAllByOwnerId(Long ownerId);
    Collection<Item> searchAvailableItems(String text);
}
