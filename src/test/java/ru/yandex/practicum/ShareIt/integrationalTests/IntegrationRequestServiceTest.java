package ru.yandex.practicum.ShareIt.integrationalTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.request.ItemRequestDto;
import ru.yandex.practicum.ShareIt.request.RequestService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationRequestServiceTest {
    private final EntityManager em;
    private final RequestService requestService;

    @Test()
    @Order(1)
    public void shouldAddRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("request Description");
        ItemRequestDto resultDto  = requestService.add(6L, requestDto);

        Assertions.assertEquals(resultDto.getDescription(), requestDto.getDescription());
        TypedQuery<ItemRequest> query = em.createQuery(
                "select i from ItemRequest i", ItemRequest.class);
        List<ItemRequest> result = query.getResultList();

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getDescription(), requestDto.getDescription());
        Assertions.assertEquals(result.get(0).getId(), 1L);
        Assertions.assertEquals(result.get(0).getRequestor(), 6L);
    }

    @Test
    @Order(2)
    public void shouldReturnAllForUser() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("request 2 Description");
        requestService.add(6L, requestDto);

        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("request 3 Description");
        requestService.add(6L, requestDto1);

        TypedQuery<ItemRequest> query = em.createQuery(
                "select i from ItemRequest i where i.requestor = 6", ItemRequest.class);
        List<ItemRequest> result = query.getResultList();
        Assertions.assertEquals(result.size(), 3);
    }

    @Test
    @Order(3)
    public void shouldReturnAllWithParams() {
        Collection<ItemRequestDto> result = requestService.getAllWithPagination(5L, 0L, 1);
        Assertions.assertEquals(result.size(), 1);

        Query query = em.createNativeQuery(
                "select * from REQUESTS where REQUESTOR_ID != 5 order by CREATION_DATE limit 1 offset 0",
                ItemRequest.class);

        List requestList = query.getResultList();
        Assertions.assertEquals(requestList.size(), result.size());
    }

    @Test
    @Order(4)
    public void shouldReturnRequestById() {
        ItemRequestDto itemRequestDto = requestService.getById(6L, 1L);
        Assertions.assertEquals(itemRequestDto.getId(), 1L);

        TypedQuery<ItemRequest> query = em.createQuery(
                "select i from ItemRequest i where i.id = 1", ItemRequest.class);

        ItemRequest itemRequest = query.getSingleResult();

        Assertions.assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }
}
