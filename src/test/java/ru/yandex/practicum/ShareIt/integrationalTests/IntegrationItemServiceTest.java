package ru.yandex.practicum.ShareIt.integrationalTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.ShareIt.item.Item;
import ru.yandex.practicum.ShareIt.item.ItemDto;
import ru.yandex.practicum.ShareIt.item.ItemService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemServiceTest {
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    public void shouldAddNewItem() {
        ItemDto iDto = new ItemDto(
                null,
                "iDto Name 1",
                "iDto Description 1",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
        ItemDto itemDto = itemService.addNew(iDto, 2L);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.owner =:id", Item.class);
        Item result = query.setParameter("id", 2L).getSingleResult();

        Assertions.assertEquals(itemDto.getId(), result.getId());
        Assertions.assertEquals(itemDto.getName(), result.getName());
        Assertions.assertEquals(itemDto.getDescription(), result.getDescription());
        Assertions.assertEquals(result.getName(), "iDto Name 1");
        Assertions.assertEquals(result.getDescription(), "iDto Description 1");
    }

    @Test
    public void shouldEditItemAndReturnById() {
        ItemDto iDto = new ItemDto(
                null,
                "iDto Name 1 Updated",
                "iDto Description 1 Updated",
                false,
                null,
                null,
                null,
                null,
                null,
                null
        );
        ItemDto itemDto = itemService.editItem(1L, 2L, iDto);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id =:id", Item.class);
        Item result = query.setParameter("id", 1L).getSingleResult();

        Assertions.assertEquals(itemDto.getId(), result.getId());
        Assertions.assertEquals(itemDto.getName(), result.getName());
        Assertions.assertEquals(itemDto.getDescription(), result.getDescription());

        Assertions.assertFalse(result.isAvailable());
        Assertions.assertEquals(result.getName(), "iDto Name 1 Updated");
        Assertions.assertEquals(result.getDescription(), "iDto Description 1 Updated");

        ItemDto itemDto1 = itemService.getById(1L, 2L);

        TypedQuery<Item> query1 = em.createQuery("select i from Item i where i.id =:id", Item.class);
        Item result1 = query1.setParameter("id", 1L).getSingleResult();

        Assertions.assertEquals(itemDto1.getId(), result1.getId());
        Assertions.assertEquals(itemDto1.getName(), result1.getName());
        Assertions.assertEquals(itemDto1.getDescription(), result1.getDescription());

        Assertions.assertEquals(result1.getName(), "iDto Name 1 Updated");
        Assertions.assertEquals(result1.getDescription(), "iDto Description 1 Updated");
        System.out.println("result1 isAvailable: " + result1.isAvailable());
        Assertions.assertFalse(result1.isAvailable());
    }

    @Test
    public void shouldReturnAllByOwnerId() {
        ItemDto itemDto4 = itemService.addNew(new ItemDto(
                null,
                "iDto Name 4",
                "iDto Description 4",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        ), 2L);
        ItemDto itemDto5 = itemService.addNew(new ItemDto(
                null,
                "iDto Name 5",
                "iDto Description 5",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        ), 2L);
        itemService.addNew(new ItemDto(
                null,
                "iDto Name 6",
                "iDto Description 6",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        ), 4L);
        itemService.addNew(new ItemDto(
                null,
                "iDto Name 7",
                "iDto Description 7",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        ), 6L);

        Collection<ItemDto> allForUser2 = itemService.getAllByOwnerId(2L);
        Assertions.assertEquals(3, allForUser2.size());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.owner = 2", Item.class);
        List<Item> result = query.getResultList()
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
        Assertions.assertEquals("iDto Name 1 Updated", result.get(0).getName());
        Assertions.assertEquals("iDto Description 1 Updated", result.get(0).getDescription());
        Assertions.assertEquals(itemDto4.getName(), result.get(1).getName());
        Assertions.assertEquals(itemDto4.getDescription(), result.get(1).getDescription());
        Assertions.assertEquals(itemDto5.getName(), result.get(2).getName());
        Assertions.assertEquals(itemDto5.getDescription(), result.get(2).getDescription());
    }
}
