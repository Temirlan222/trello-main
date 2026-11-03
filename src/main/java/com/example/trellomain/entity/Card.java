package com.example.trellomain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_list_id", nullable = false)
    private TaskList taskList;

    protected Card() {
    }

    public Card(TaskList taskList, @NotNull String title, String description, Priority priority, LocalDate due_date) {
        this.taskList = taskList;
        this.title = title;
        this.description = description;
        if (priority != null) {
            this.priority = priority;
        }
        this.dueDate = due_date;
    }

    public Card(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @PrePersist
    private void setCreationTime() {
        this.createdAt = LocalDateTime.now();
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

}
