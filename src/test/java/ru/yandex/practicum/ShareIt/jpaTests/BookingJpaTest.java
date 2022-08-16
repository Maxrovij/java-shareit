package ru.yandex.practicum.ShareIt.jpaTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.ShareIt.booking.Booking;
import ru.yandex.practicum.ShareIt.booking.BookingRepository;
import ru.yandex.practicum.ShareIt.booking.BookingStatus;
import ru.yandex.practicum.ShareIt.item.Item;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingJpaTest {
    private final TestEntityManager em;

    private final BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setItem(1L);
        booking.setBookerId(1L);
        booking.setStatus(BookingStatus.WAITING);

        Booking booking1 = new Booking();
        booking1.setStart(now.plusDays(3));
        booking1.setEnd(now.plusDays(4));
        booking1.setItem(3L);
        booking1.setBookerId(2L);
        booking1.setStatus(BookingStatus.APPROVED);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusDays(5));
        booking2.setEnd(now.plusDays(6));
        booking2.setItem(1L);
        booking2.setBookerId(2L);
        booking2.setStatus(BookingStatus.REJECTED);

        em.merge(booking);
        em.merge(booking1);
        em.merge(booking2);
    }

    @Test
    @Order(1)
    public void shouldReturnAllByBookerId() {
        List<Booking> result = bookingRepository.findAllByBookerId(2L);

        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "select b from Booking b where b.bookerId = 2", Booking.class);
        List<Booking> queryResult = query.getResultList()
                .stream()
                .sorted(Comparator.comparing(Booking::getId))
                .collect(Collectors.toList());

        Assertions.assertEquals(result.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getItem(), 3);
        Assertions.assertEquals(queryResult.get(1).getItem(), 1);
    }

    @Test
    @Order(2)
    public void shouldReturnAllByBookerIdAndStatus() {
        List<Booking> result = bookingRepository.findAllByBooker_idAndStatus(
                2L, String.valueOf(BookingStatus.REJECTED));

        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "select b from Booking b where b.bookerId = 2 and b.status = :status", Booking.class);
        List<Booking> queryResult = query.setParameter(
                "status",
                BookingStatus.REJECTED).getResultList();

        Assertions.assertEquals(result.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    @Order(3)
    public void shouldReturnAllByItemId() {
        List<Booking> result = bookingRepository.findAllByItem_Id(1L);

        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "select b from Booking b where b.item = 1", Booking.class);
        List<Booking> queryResult = query.getResultList()
                .stream()
                .sorted(Comparator.comparing(Booking::getId))
                .collect(Collectors.toList());

        Assertions.assertEquals(result.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getItem(), 1);
        Assertions.assertEquals(queryResult.get(1).getItem(), 1);
        Assertions.assertEquals(queryResult.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(queryResult.get(1).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    @Order(4)
    public void shouldReturnAllForOwner() {
        Item item1 = new Item(
                1L,
                "item1 name",
                "item1 description",
                true,
                3L,
                null);

        Item item2 = new Item(
                2L,
                "item1 name",
                "item1 description",
                true,
                3L,
                null);

        em.merge(item1);
        em.merge(item2);
        List<Booking> repoResult = bookingRepository.findAllByOwner(3L);

        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "select b from Booking b join Item i on b.item = i.id where i.owner = 3", Booking.class);
        List<Booking> queryResult = query.getResultList()
                .stream()
                .sorted(Comparator.comparing(Booking::getId))
                .collect(Collectors.toList());
        Assertions.assertEquals(repoResult.size(), queryResult.size());
        Assertions.assertEquals(queryResult.get(0).getItem(), 1);
        Assertions.assertEquals(queryResult.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(queryResult.get(1).getItem(), 1);
        Assertions.assertEquals(queryResult.get(1).getStatus(), BookingStatus.REJECTED);
    }
}
