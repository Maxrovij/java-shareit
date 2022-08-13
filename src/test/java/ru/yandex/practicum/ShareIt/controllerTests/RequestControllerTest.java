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
import ru.yandex.practicum.ShareIt.request.ItemRequestDto;
import ru.yandex.practicum.ShareIt.request.RequestController;
import ru.yandex.practicum.ShareIt.request.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    MockMvc mockMvc;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        itemRequestDto = new ItemRequestDto(
                1L,
                "request description",
                null,
                LocalDateTime.now(),
                List.of());
    }

    @Test
    public void add() throws Exception {
        Mockito
                .when(requestService.add(Mockito.any(), Mockito.any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    public void getById() throws Exception {
        Mockito.when(requestService.getById(Mockito.any(), Mockito.any())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                    .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    public void getAllForUser() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                2L,
                "request 2 description",
                null,
                LocalDateTime.now(),
                List.of());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                1L,
                "request description",
                null,
                LocalDateTime.now(),
                List.of());

        Mockito
                .when(requestService.getAllForUser(Mockito.any()))
                .thenReturn(List.of(itemRequestDto, itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests")
                    .header("X-Sharer-User-Id", 1L)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(
                                        itemRequestDto,
                                        itemRequestDto1,
                                        itemRequestDto2))));
    }

    @Test
    public void getAllWithPagination() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                2L,
                "request 2 description",
                null,
                LocalDateTime.now(),
                List.of());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                1L,
                "request description",
                null,
                LocalDateTime.now(),
                List.of());
        Mockito
                .when(requestService.getAllWithPagination(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(itemRequestDto, itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", "1")
                .param("from", "10")
                .param("size", "20")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(
                        itemRequestDto, itemRequestDto1, itemRequestDto2))));
    }
}
