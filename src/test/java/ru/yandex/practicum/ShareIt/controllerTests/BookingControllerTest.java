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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.ShareIt.booking.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                BookingStatus.WAITING
        );
        bookingRequestDto = new BookingRequestDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                null,
                BookingStatus.WAITING
        );
    }

    @Test
    public void add() throws Exception {
        Mockito.when(bookingService.add(Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(bookingRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(BookingStatus.WAITING)), String.class));
    }

    @Test
    public void setAvailable() throws Exception {
        bookingDto.setStatus(BookingStatus.REJECTED);
        Mockito.when(bookingService.setAvailable(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                    .header("X-Sharer-User-Id", 1L)
                    .param("approved", "false")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(BookingStatus.REJECTED)), String.class));
    }

    @Test
    public void get() throws Exception {
        Mockito.when(bookingService.get(Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                    .header("X-Sharer-User-Id", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(BookingStatus.WAITING)), String.class));
    }

    @Test
    public void getAllForUser() throws Exception {
        BookingDto bookingDto1 = new BookingDto(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                BookingStatus.WAITING
        );
        BookingDto bookingDto2 = new BookingDto(
                3L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                BookingStatus.WAITING
        );
        Mockito
                .when(bookingService.getAllForUser(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(bookingDto, bookingDto1, bookingDto2));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                    .header("X-Sharer-User-Id", 1L)
                    .param("state", "ALL")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(
                        bookingDto, bookingDto1, bookingDto2))));
    }

    @Test
    public void getAllForOwner() throws Exception {
        BookingDto bookingDto1 = new BookingDto(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                BookingStatus.WAITING
        );
        BookingDto bookingDto2 = new BookingDto(
                3L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                BookingStatus.WAITING
        );
        Mockito
                .when(bookingService.getAllForOwner(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(bookingDto, bookingDto1, bookingDto2));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(
                        bookingDto, bookingDto1, bookingDto2))));
    }
}
