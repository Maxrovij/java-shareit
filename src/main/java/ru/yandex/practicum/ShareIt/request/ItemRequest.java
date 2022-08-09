package ru.yandex.practicum.ShareIt.request;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "requestor_id", nullable = false)
    private Long requestor;
    @Column(name = "creation_date")
    private LocalDateTime created;

    public ItemRequest() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemRequest that = (ItemRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
