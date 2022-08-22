package ru.yandex.practicum.ShareIt.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item as i " +
            "where lower(i.name) like (concat('%', ?1, '%')) " +
            " or lower(i.description) like (concat('%', ?1, '%'))")
    List<Item> searchByText(String text);

    @Query("select i from Item as i where i.owner=?1")
    List<Item> searchAllByOwnerId(Long id);
    @Query(value = "select * from ITEMS where REQUEST_ID = ?1", nativeQuery = true)
    List<Item> findAllByRequestId(Long id);

}
