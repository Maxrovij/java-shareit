package ru.practicum.shareit.items;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotNull
    @NotBlank
    private String text;
    private String author;
    private LocalDateTime publicationDate;
}
