package com0.trello.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private LocalDateTime created;
    private LocalDateTime lastUpdated;
    private int userID;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<Board> boards;

    @ManyToMany(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private List<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

    @Override
    public String toString() {
        String idAndTitleToString = "id=" + id + ", title='" + title + '\'';
        String datesToString = ", created=" + created +", lastUpdated=" + lastUpdated;
        String usersToString = ", userID=" + userID + ", users=" + users;

        return "Workspace{" +
                idAndTitleToString +
                datesToString +
                usersToString +
                '}';
    }

    public Workspace(int id, String title, LocalDateTime created, LocalDateTime lastUpdated, int userID, List<User> users) {
        this.id = id;
        this.title = title;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.userID = userID;
        this.users = users;
    }

    public Workspace( int userID, String title) {
        this.title = title;
        this.userID = userID;
    }

    public Workspace() {
    }

}
