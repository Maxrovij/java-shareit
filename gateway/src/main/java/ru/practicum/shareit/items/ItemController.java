package ru.practicum.shareit.items;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GlobalVars;
import ru.practicum.shareit.exceptions.IncorrectDataException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto,
                                          @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty())
            throw new IncorrectDataException("Name must be defined!");
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty())
            throw new IncorrectDataException("Description must be defined!");
        if (itemDto.getAvailable() == null)
            throw new IncorrectDataException("Available must be defined!");
        log.info("Adding new Item. UserId = {}", userId);
        return client.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        log.info("Editing item with id = {}", itemId);
        return client.editItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @PathVariable Long itemId,
                                          @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        log.info("Get item by id = {} and userId = {}", itemId, userId);
        return client.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByOwnerId(@Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        log.info("Getting items by ownerId = {}", userId);
        return client.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItemsByText(
            @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @RequestParam(name = "text", defaultValue = "") String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        log.info("Searching items by text: {}", text);
        return client.getAvailableByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @PathVariable Long itemId,
                                             @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
                                             @RequestBody CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty())
            throw new IncorrectDataException("Write something!");
        log.info("Adding comment to item with id = {} and comment text: {}", itemId, commentDto.getText());
        return client.addComment(userId, itemId, commentDto);
    }
}
