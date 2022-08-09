package ru.yandex.practicum.ShareIt.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.exceptions.DataNotFoundException;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.item.ItemDto;
import ru.yandex.practicum.ShareIt.item.ItemService;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
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

        String description = requestDto.getDescription();
        if (description == null || description.isEmpty())
            throw new IncorrectDataException("Description must be defined!");

        ItemRequest itemRequest = new ItemRequest(null, description, userId, LocalDateTime.now());

        return toDto(requestRepository.save(itemRequest), userDto);
    }

    @Override
    public Collection<ItemRequestDto> getAllForUser(Long userId) {
        UserDto userDto = userService.getById(userId);
        List<ItemRequest> userRequests = requestRepository.findAllByUserId(userId);
        return userRequests
                .stream()
                .map(itemRequest -> toDto(itemRequest, userDto))
                .peek(i -> {
                    Collection<ItemDto> responses = itemService.getAllByRequestId(i.getId());
                    i.setItems(responses.isEmpty() ? List.of() : responses);
                })
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllWithPagination(Long userId, Long from, int size) {
        return null;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        UserDto userDto = userService.getById(userId);
        Optional<ItemRequest> maybeRequest = requestRepository.findById(requestId);
        if (maybeRequest.isEmpty()) throw new DataNotFoundException("Request not found!");

        ItemRequestDto dto = toDto(maybeRequest.get(), userDto);
        Collection<ItemDto> responses = itemService.getAllByRequestId(requestId);

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
