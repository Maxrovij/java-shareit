package ru.yandex.practicum.ShareIt.item;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();

    public Item add(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Collection<Item> getAll() {
        return items.values();
    }
}
