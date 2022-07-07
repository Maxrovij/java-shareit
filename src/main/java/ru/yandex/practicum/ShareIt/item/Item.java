package ru.yandex.practicum.ShareIt.item;

import lombok.Data;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.user.User;

@Data
public class Item {
    private final Long id;
    private String name;
    private String description;
    private boolean available;
    private final User owner;
    private ItemRequest request;
}
