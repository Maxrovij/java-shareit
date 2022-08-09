package ru.yandex.practicum.ShareIt.request;

import java.util.Collection;

public interface RequestService {
    ItemRequestDto add(Long userId, ItemRequestDto requestDto);

    Collection<ItemRequestDto> getAllForUser(Long userId);

    Collection<ItemRequestDto> getAllWithPagination(Long userId, Long from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
