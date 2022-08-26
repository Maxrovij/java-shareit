package ru.practicum.shareit.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Query("update Item i set i.name=?1, i.description=?2, i.available=?3 where i.id=?4")
    void patch(String name, String description, Boolean available, Long id);
    @Query(" select i from Item as i " +
            "where lower(i.name) like (concat('%', ?1, '%')) " +
            " or lower(i.description) like (concat('%', ?1, '%'))")
    List<Item> searchByText(String text);

    @Query("select i from Item as i where i.owner=?1")
    List<Item> searchAllByOwnerId(Long id);
    @Query(value = "select i from Item as i where i.request=?1")
    List<Item> findAllByRequestId(Long id);

}
