package com.example.trellomain.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_lists")
public class TaskList {
    public TaskList(Long id, String name, Board board, List<Card> cards, Boolean isDone) {
        this.id = id;
        this.name = name;
        this.board = board;
        this.cards = cards;
        this.isDone = isDone;
    }

    public TaskList(String name, Board board) {
        this.name = name;
        this.board = board;
    }

    public TaskList(String name, Board board, Boolean isDone) {
        this.name = name;
        this.board = board;
        this.isDone = isDone;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "taskList", cascade = CascadeType.REMOVE)
    @OrderBy("createdAt ASC, id ASC")
    List<Card> cards = new ArrayList<>();

    private Boolean isDone = false;

    protected TaskList() {
    }

    public List<Card> getCards() {
        return cards;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDone() {
        return isDone;
    }

    public Board getBoard() {
        return board;
    }
}
