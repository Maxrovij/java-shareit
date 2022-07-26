package ru.yandex.practicum.ShareIt.item;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Item {
    @Id
    private Long id;
    @Column(name = "name", nullable = false, length = 250)
    private String name;
    @Column(name = "description", nullable = false, length = 600)
    private String description;
    @Column(name = "available", nullable = false)
    private boolean available;
    @Column(name = "owner_id", nullable = false)
    private Long owner;
    @Column(name = "request_id")
    private Long request;

    public Item(){}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return id != null && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
