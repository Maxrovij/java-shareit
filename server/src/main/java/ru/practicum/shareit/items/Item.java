package ru.practicum.shareit.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Item() {
    }

    public Item(String name, String description, boolean available, Long owner, Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }

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
