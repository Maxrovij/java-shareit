package ru.practicum.shareit.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GlobalVars;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNew(
            @RequestBody ItemDto itemDto,
            @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        log.info("Post request. userId: {}, itemDto.name: {}, itemDto.description: {}",
                userId,
                itemDto.getName(),
                itemDto.getDescription());
        return itemService.addNew(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(
            @PathVariable Long itemId,
            @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Patch request. userId: {}, itemId: {}, itemDto.name: {}, itemDto.description: {}",
                userId,
                itemId,
                itemDto.getName(),
                itemDto.getDescription());
        return itemService.editItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        log.info("Get request. userId: {}, itemId: {}", userId, itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllByOwnerId(@RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        log.info("Get all for owner request. userId: {}", userId);
        return itemService.getAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchAvailableItemsByText(@RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Get/search request. text: {}", text);
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(GlobalVars.USER_HEADER) Long userId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Post/comment request. userId: {}, itemId: {}, commentText: {}", userId, itemId, commentDto.getText());
        return itemService.addComment(itemId, userId, commentDto);
    }
}

