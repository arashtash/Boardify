package com0.trello.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate dueDate;

    @ManyToMany(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private List<User> users;


    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Board board;

    private TaskStatus status;

    public Task() {
    }

    public Task(Long id, String name, LocalDate dueDate, List<User> users, Board board, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.users = users;
        this.board = board;
        this.status = status;
    }

    public Task(String name, TaskStatus status) {
        this.name = name;
        this.status = status;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}