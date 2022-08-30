package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.IncorrectDataException;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(
                                serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> setApproved(Long userId, Long bookingId, boolean isApproved) {
        return patch("/" + bookingId + "?approved=" + isApproved, userId);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(String path, Long userId, String stateParam, Integer from, Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectDataException(String.format("{\"error\": \"Unknown state: %s\" }",
                        stateParam)));
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get(path + "?state={state}&from={from}&size={size}", userId, parameters);
    }



}
