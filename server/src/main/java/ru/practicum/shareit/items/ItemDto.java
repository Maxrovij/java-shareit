package ru.practicum.shareit.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.bookings.Booking;
import ru.practicum.shareit.requests.ItemRequest;

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
