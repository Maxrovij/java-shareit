package ru.yandex.practicum.ShareIt.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.ShareIt.item.CommentDto;
import ru.yandex.practicum.ShareIt.item.ItemController;
import ru.yandex.practicum.ShareIt.item.ItemDto;
import ru.yandex.practicum.ShareIt.item.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    MockMvc mockMvc;

    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = new ItemDto(
                1L,
                "iDto Name 1",
                "iDto Description 1",
                true,
                new ItemDto.User(1L, "owner 1 name"),
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    public void addItem() throws Exception {
        Mockito.when(itemService.addNew(Mockito.any(), Mockito.any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description" ,is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void editItem() throws Exception {
        itemDto.setId(2L);
        itemDto.setName("nameUpdated");
        itemDto.setAvailable(false);

        Mockito
                .when(itemService.editItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 2L)
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description" ,is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void getItemById() throws Exception {
        Mockito
                .when(itemService.getById(Mockito.any(), Mockito.any()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                    .header("X-Sharer-User-Id", 1L)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description" ,is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void getAllByOwnerId() throws Exception {
        ItemDto itemDto1 = new ItemDto(
                2L,
                "iDto Name 2",
                "iDto Description 2",
                true,
                new ItemDto.User(1L, "owner 1 name"),
                null,
                null,
                null,
                null,
                null
        );
        ItemDto itemDto2 = new ItemDto(
                3L,
                "iDto Name 3",
                "iDto Description 3",
                true,
                new ItemDto.User(1L, "owner 1 name"),
                null,
                null,
                null,
                null,
                null
        );
        Mockito
                .when(itemService.getAllByOwnerId(Mockito.any()))
                .thenReturn(List.of(itemDto, itemDto1, itemDto2));

        mockMvc.perform(get("/items")
                    .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(itemDto,itemDto1,itemDto2))));
    }

    @Test
    public void searchByText() throws Exception {
        ItemDto itemDto1 = new ItemDto(
                2L,
                "iDto Name 2",
                "iDto Description 2",
                true,
                new ItemDto.User(1L, "owner 1 name"),
                null,
                null,
                null,
                null,
                null
        );
        ItemDto itemDto2 = new ItemDto(
                3L,
                "iDto Name 3",
                "iDto Description 3",
                true,
                new ItemDto.User(1L, "owner 1 name"),
                null,
                null,
                null,
                null,
                null
        );
        Mockito
                .when(itemService.searchAvailableItems(Mockito.any()))
                .thenReturn(List.of(itemDto, itemDto1, itemDto2));

        mockMvc.perform(get("/items/search").param("text", "some Text")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.TEXT_PLAIN)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(itemDto, itemDto1, itemDto2))));
    }

    @Test
    public void addComment() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        CommentDto commentDto = new CommentDto(
                1L,
                "comment Text",
                1L,
                1L,
                "authorName",
                LocalDateTime.now()
        );

        Mockito.when(itemService.addComment(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(commentDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.item", is(commentDto.getItem()), Long.class))
                .andExpect(jsonPath("$.author", is(commentDto.getAuthor()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }
}
