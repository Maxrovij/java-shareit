package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GlobalVars;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @Valid @RequestBody RequestDto requestDto) {
        return client.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        return client.getAllForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithPagination(
            @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return client.getAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @Positive @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @Positive @PathVariable Long requestId) {
        return client.getRequest(userId, requestId);
    }
}
