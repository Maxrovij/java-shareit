package ru.practicum.shareit.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "select * from COMMENTS where ITEM_ID = ?1", nativeQuery = true)
    List<Comment> findAllByItemId(Long itemId);
}
