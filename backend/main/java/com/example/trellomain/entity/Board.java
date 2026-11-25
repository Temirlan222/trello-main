package com.example.trellomain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Board")
public class Board {
    @Id
    @SequenceGenerator(
            name = "board_sequence",
            sequenceName = "board_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "board_sequence"
    )
    private Long id;

    private String name;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<TaskList> lists = new ArrayList<>();

    protected Board() {
    }

    public Board(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public List<TaskList> getLists() {
        return lists;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    private void setCreationTime() {
        this.createdAt = LocalDateTime.now();
    }
}
