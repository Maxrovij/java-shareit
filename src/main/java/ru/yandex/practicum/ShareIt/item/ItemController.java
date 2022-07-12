package ru.yandex.practicum.ShareIt.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNew(
            @RequestBody ItemDto itemDto,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.addNew(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(
            @PathVariable Long itemId,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {
        return itemService.editItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchAvailableItemsByText(@RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.searchAvailableItems(text);
    }

}
