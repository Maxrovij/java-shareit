package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.items.ItemDto;
import ru.practicum.shareit.items.ItemService;
import ru.practicum.shareit.users.UserDto;
import ru.practicum.shareit.users.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService, ItemService itemService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.itemService = itemService;
    }


    @Override
    public ItemRequestDto add(Long userId, ItemRequestDto requestDto) {
        UserDto userDto = userService.getById(userId);

        ItemRequest itemRequest = new ItemRequest(null, requestDto.getDescription(), userId, LocalDateTime.now());

        return toDto(requestRepository.save(itemRequest), userDto);
    }

    @Override
    public Collection<ItemRequestDto> getAllForUser(Long userId) {
        UserDto userDto = userService.getById(userId);
        return requestRepository.findAllByUserId(userId)
                .stream()
                .map(itemRequest -> toDto(itemRequest, userDto))
                .peek(this::setResponses)
                .sorted((i1, i2) -> {
                    if (i1.getCreated().isBefore(i2.getCreated())) return 1;
                    if (i1.getCreated().isAfter(i2.getCreated())) return -1;
                    return 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllWithPagination(Long userId, Long from, Integer size) {
        userService.getById(userId);

        return requestRepository.findAllByParams(userId, size, from)
                .stream()
                .map(itemRequest -> {
                    UserDto requestor = userService.getById(itemRequest.getRequestor());
                    return setResponses(toDto(itemRequest, requestor));
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        UserDto userDto = userService.getById(userId);
        Optional<ItemRequest> maybeRequest = requestRepository.findById(requestId);
        if (maybeRequest.isEmpty()) throw new DataNotFoundException("Request not found!");

        return setResponses(toDto(maybeRequest.get(), userDto));
    }

    private ItemRequestDto setResponses(ItemRequestDto dto) {
        Collection<ItemDto> responses = itemService.getAllByRequestId(dto.getId());
        if (!responses.isEmpty()) dto.setItems(responses);
        return dto;
    }

    private ItemRequestDto toDto(ItemRequest ir, UserDto userDto) {
        return new ItemRequestDto(
                ir.getId(),
                ir.getDescription(),
                new ItemRequestDto.User(userDto.getId(), userDto.getName()),
                ir.getCreated(),
                List.of());
    }
}
