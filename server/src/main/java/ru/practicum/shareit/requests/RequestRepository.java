package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(value = "select * from REQUESTS where REQUESTOR_ID = ?1", nativeQuery = true)
    List<ItemRequest> findAllByUserId(Long userId);

    @Query(value = "select * from REQUESTS where REQUESTOR_ID != ?1 order by CREATION_DATE limit ?2 offset ?3",
            nativeQuery = true)
    List<ItemRequest> findAllByParams(Long userId, Integer size, Long from);
}
