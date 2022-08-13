package ru.yandex.practicum.ShareIt.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.ShareIt.booking.Booking;
import ru.yandex.practicum.ShareIt.request.ItemRequest;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;

    public ItemDto() {

    }

    @Data
    public static class User {
        private Long id;
        private String name;

        public User() {

        }

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
