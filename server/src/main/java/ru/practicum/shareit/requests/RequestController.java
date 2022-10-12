package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GlobalVars;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader(GlobalVars.USER_HEADER) Long userId,
                              @RequestBody ItemRequestDto requestDto) {
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllForUser(@RequestHeader(GlobalVars.USER_HEADER) Long userId) {
        return requestService.getAllForUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllWithPagination(
            @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Long from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestService.getAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @RequestHeader(GlobalVars.USER_HEADER) Long userId,
            @PathVariable Long requestId) {
        return requestService.getById(userId, requestId);
    }
}

