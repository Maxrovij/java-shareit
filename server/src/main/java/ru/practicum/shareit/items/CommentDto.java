package ru.practicum.shareit.items;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private Long item;
    private Long author;
    private String authorName;
    private LocalDateTime created;

    public CommentDto() {

    }
}
