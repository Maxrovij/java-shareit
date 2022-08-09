package ru.yandex.practicum.ShareIt.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(value = "select * from REQUESTS where REQUESTOR_ID = ?1", nativeQuery = true)
    List<ItemRequest> findAllByUserId(Long userId);
}
