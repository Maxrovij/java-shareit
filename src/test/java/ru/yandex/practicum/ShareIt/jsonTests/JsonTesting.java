package ru.yandex.practicum.ShareIt.jsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.yandex.practicum.ShareIt.request.ItemRequestDto;
import ru.yandex.practicum.ShareIt.user.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonTesting {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonRequestTester;

    @Test
    void testItemRequestDto() throws IOException {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "sdfsdf", null, now, List.of());

        JsonContent<ItemRequestDto> result = jacksonRequestTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("sdfsdf");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
