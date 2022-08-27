package ru.practicum.shareit.items;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private String author;
    private LocalDateTime publicationDate;
}
