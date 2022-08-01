package ru.yandex.practicum.ShareIt.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select * from BOOKING where BOOKER_ID = ?1", nativeQuery = true)
    List<Booking> findAllByBookerId(Long bookerId);
    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 and STATUS = ?2", nativeQuery = true)
    List<Booking> findAllByBooker_idAndStatus(Long bookerId, States Status);

    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 and STATUS = 'APPROVED' " +
            "and START_DATE < ?2 and END_DATE > ?2", nativeQuery = true)
    List<Booking> findAllCurrent(Long userId, LocalDateTime now);
    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 and STATUS = 'APPROVED' " +
            "and END_DATE < ?2", nativeQuery = true)
    List<Booking> findAllPast(Long userId, LocalDateTime now);
    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 and STATUS = 'APPROVED' " +
            "and START_DATE > ?2", nativeQuery = true)
    List<Booking> findAllFuture(Long userId, LocalDateTime now);
}
