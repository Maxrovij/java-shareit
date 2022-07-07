package ru.yandex.practicum.ShareIt.item;

public class ItemMapper {

    public static ItemDto toDto(Item i) {
        ItemDto.ItemRequest itemRequest = null;

        if (i.getRequest() != null) {

            ItemDto.User requestor = new ItemDto.User(
                    i.getRequest().getRequestor().getId(),
                    i.getRequest().getRequestor().getName());

            itemRequest = new ItemDto.ItemRequest(i.getRequest().getId());
            itemRequest.setDescription(i.getDescription());
            itemRequest.setRequestor(requestor);
        }
        return new ItemDto(
                i.getId(),
                i.getName(),
                i.getDescription(),
                i.isAvailable(),
                new ItemDto.User(i.getOwner().getId(), i.getOwner().getName()), itemRequest
        );
    }

}
