package ru.yandex.practicum.ShareIt.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.ShareIt.exceptions.IncorrectDataException;
import ru.yandex.practicum.ShareIt.item.ItemService;
import ru.yandex.practicum.ShareIt.item.ItemServiceImpl;
import ru.yandex.practicum.ShareIt.request.*;
import ru.yandex.practicum.ShareIt.user.UserDto;
import ru.yandex.practicum.ShareIt.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestServiceTest {
    RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    UserService userService = Mockito.mock(UserService.class);
    ItemService itemService = Mockito.mock(ItemServiceImpl.class);
    RequestService requestService = new RequestServiceImpl(requestRepository, userService, itemService);
    UserDto userDto = new UserDto(1L, "user1", "emailUser1@ya.ru");

    @Test
    public void shouldAddNewRequestWithCorrectParameters() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                null,
                "Some description",
                null,
                null,
                null);

        LocalDateTime now = LocalDateTime.now();

        ItemRequest iRequest = new ItemRequest(
                1L,
                itemRequestDto.getDescription(),
                userDto.getId(),
                now);

        Mockito
                .when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(iRequest);

        ItemRequestDto responseDto = requestService.add(userDto.getId(), itemRequestDto);

        Assertions.assertEquals(responseDto.getId(), 1L);
        Assertions.assertEquals(responseDto.getDescription(), "Some description");
        Assertions.assertNotNull(responseDto.getRequestor());
        Assertions.assertEquals(responseDto.getCreated(), now);
        Assertions.assertEquals(responseDto.getItems(), List.of());

    }

    @Test
    public void shouldThrowExceptionWhenDescriptionIsEmpty() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                null,
                "",
                null,
                null,
                null);

        Mockito
                .when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);

        final IncorrectDataException exception = Assertions.assertThrows(
                IncorrectDataException.class,
                () -> requestService.add(userDto.getId(), itemRequestDto));
        Assertions.assertEquals(exception.getMessage(), "Description must be defined!");
    }

    @Test
    public void shouldReturnAllForUser() {

        List<ItemRequest> allForUser = List.of(
                new ItemRequest(1L, "description1", 1L, LocalDateTime.now().minusDays(3)),
                new ItemRequest(2L, "description2", 1L, LocalDateTime.now().minusDays(1)),
                new ItemRequest(3L, "description3", 1L, LocalDateTime.now().minusDays(2))
        );

        Mockito
                .when(userService.getById(1L))
                .thenReturn(userDto);

        Mockito
                .when(requestRepository.findAllByUserId(1L))
                .thenReturn(allForUser);

        Mockito
                .when(itemService.getAllByRequestId(Mockito.anyLong()))
                .thenReturn(List.of());

        List<ItemRequestDto> methodResponse = new ArrayList<>(requestService.getAllForUser(1L));


        Assertions.assertEquals(methodResponse.size(), 3);
        Assertions.assertTrue(
                methodResponse.get(0).getCreated().isAfter(methodResponse.get(1).getCreated()) &&
                        methodResponse.get(1).getCreated().isAfter(methodResponse.get(2).getCreated()));
        Assertions.assertEquals(methodResponse.get(0).getDescription(), "description2");
        Assertions.assertEquals(methodResponse.get(1).getDescription(), "description3");
        Assertions.assertEquals(methodResponse.get(2).getDescription(), "description1");
    }

    @Test
    public void shouldThrowExceptionWithWrongPaginationParams() {
        Mockito
                .when(userService.getById(1L))
                .thenReturn(userDto);

        final IncorrectDataException e = Assertions.assertThrows(IncorrectDataException.class,
                () -> requestService.getAllWithPagination(1L, 0L, -1));
        Assertions.assertEquals(e.getMessage(), String.format("Error! 'size'= %d. must be greater than '0' ", -1));

        final IncorrectDataException e1 = Assertions.assertThrows(IncorrectDataException.class,
                () -> requestService.getAllWithPagination(1L, 0L, 0));
        Assertions.assertEquals(e1.getMessage(), String.format("Error! 'size'= %d. must be greater than '0' ", 0));

        final IncorrectDataException e2 = Assertions.assertThrows(IncorrectDataException.class,
                () -> requestService.getAllWithPagination(1L, -1L, 10));
        Assertions.assertEquals(e2.getMessage(), String.format("Error! 'from'= %d must be positive or '0' ", -1));
    }

}
