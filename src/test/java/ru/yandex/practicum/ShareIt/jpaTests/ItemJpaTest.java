package ru.yandex.practicum.ShareIt.jpaTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.ShareIt.item.Item;
import ru.yandex.practicum.ShareIt.item.ItemRepository;
import ru.yandex.practicum.ShareIt.user.User;

import javax.persistence.TypedQuery;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemJpaTest {

    private final TestEntityManager em;

    private final ItemRepository itemRepository;

    User user;

    @BeforeEach
    void addSomeItems() {
        user = new User(1L, "name", "maik@re.lu");
        em.merge(user);
        itemRepository.save(
                new Item(
                        null,
                        "Name",
                        "search",
                        true,
                        1L,
                        null));
        itemRepository.save(
                new Item(
                        null,
                        "search",
                        "Description",
                        true,
                        1L,
                        null));
        itemRepository.save(
                new Item(
                        null,
                        "wtf",
                        "Description",
                        true,
                        1L,
                        1L));

    }

    @Test
    @Order(1)
    public void shouldSearchByText() {
        String text = "search";

        List<Item> repoResult = itemRepository.searchByText(text);
        TypedQuery<Item> query = em.getEntityManager().createQuery(
                "select i from Item i where lower(i.name) like concat('%', :text, '%') " +
                        "or lower(i.description) like concat('%', :text, '%')", Item.class);
        List<Item> queryResult = query.setParameter("text", text).getResultList()
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        Assertions.assertEquals(repoResult.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getDescription(), "search");
        Assertions.assertEquals(queryResult.get(1).getName(), "search");
    }

    @Test
    @Order(2)
    public void shouldSearchByOwnerId() {
        List<Item> repoResult = itemRepository.searchAllByOwnerId(1L);
        TypedQuery<Item> query = em.getEntityManager().createQuery(
                "select i from Item as i where i.owner=1 ", Item.class);
        List<Item> queryResult = query.getResultList()
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        Assertions.assertEquals(repoResult.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getOwner(), 1L);
        Assertions.assertEquals(queryResult.get(1).getOwner(), 1L);
        Assertions.assertEquals(queryResult.get(2).getOwner(), 1L);
    }

    @Test
    @Order(3)
    public void shouldSearchByRequestId() {
        List<Item> repoResult = itemRepository.findAllByRequestId(1L);
        TypedQuery<Item> query = em.getEntityManager().createQuery(
                "select i from Item i where i.request = 1", Item.class);
        List<Item> queryResult = query.getResultList();

        Assertions.assertEquals(repoResult.size(), queryResult.size());
        Assertions.assertEquals(repoResult.get(0).getName(), "wtf");
        Assertions.assertEquals(queryResult.get(0).getName(), "wtf");
    }
}
