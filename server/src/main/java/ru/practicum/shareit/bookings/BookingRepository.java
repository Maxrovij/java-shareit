package ru.practicum.shareit.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 order by START_DATE limit ?2 offset ?3", nativeQuery = true)
    List<Booking> findAllByBookerId(Long bookerId, Integer size, Integer from);

    @Query(value = "select * from BOOKING where BOOKER_ID = ?1 and STATUS = ?2", nativeQuery = true)
    List<Booking> findAllByBooker_idAndStatus(Long bookerId, String status);

    @Query(value = "select b.ID, b.START_DATE, b.END_DATE, b.ITEM_ID, b.BOOKER_ID,b.STATUS from BOOKING as b " +
            "JOIN ITEMS I on b.ITEM_ID = I.ID where OWNER_ID = ?1 order by START_DATE limit ?2 offset ?3",
            nativeQuery = true)
    List<Booking> findAllByOwner(Long userId, Integer size, Integer from);

    @Query(value = "select * from BOOKING where ITEM_ID = ?1 order by START_DATE", nativeQuery = true)
    List<Booking> findAllByItem_Id(Long itemId);
}
