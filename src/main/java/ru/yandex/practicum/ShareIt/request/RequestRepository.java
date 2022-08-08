package ru.yandex.practicum.ShareIt.request;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

}
