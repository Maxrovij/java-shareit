package ru.yandex.practicum.ShareIt.jpaTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.request.RequestRepository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestJpaTest {
    private final TestEntityManager em;
    private final RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("description 1");
        request1.setRequestor(1L);
        request1.setCreated(now);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("description 2");
        request2.setRequestor(1L);
        request2.setCreated(now.plusDays(1));

        ItemRequest request3 = new ItemRequest();
        request3.setDescription("description 3");
        request3.setRequestor(2L);
        request3.setCreated(now.plusDays(2));

        em.merge(request1);
        em.merge(request2);
        em.merge(request3);
    }

    @Test
    @Order(1)
    public void shouldReturnRequestByRequestorId() {
        List<ItemRequest> result = requestRepository.findAllByUserId(1L);

        TypedQuery<ItemRequest> query = em.getEntityManager().createQuery(
                "select r from ItemRequest r where r.requestor = 1", ItemRequest.class);
        List<ItemRequest> queryResult = query.getResultList()
                .stream()
                .sorted(Comparator.comparing(ItemRequest::getId))
                .collect(Collectors.toList());

        Assertions.assertEquals(result.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getDescription(), "description 1");
        Assertions.assertEquals(queryResult.get(1).getDescription(), "description 2");
    }

    @Test
    @Order(2)
    public void shouldReturnByParams() {
        List<ItemRequest> result = requestRepository.findAllByParams(1L, 1, 0L);

        TypedQuery<ItemRequest> query = em.getEntityManager().createQuery(
                "select r from ItemRequest r where r.requestor <> 1 order by r.created",
                ItemRequest.class);
        List queryResult = query
                .setMaxResults(1)
                .getResultList();

        Assertions.assertEquals(result.size(), queryResult.size());
    }
}
