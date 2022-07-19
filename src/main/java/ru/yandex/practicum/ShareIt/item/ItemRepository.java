package ru.yandex.practicum.ShareIt.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Optional<Item> getById(Long itemId);

    Collection<Item> getAll();
}
