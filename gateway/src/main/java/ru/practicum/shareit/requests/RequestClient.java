package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(
                                serverUrl + "/requests"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> addRequest(Long userId, RequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getAllForUser(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllWithPagination(Long userId, int from, int size) {
        return get("/all?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getRequest(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
