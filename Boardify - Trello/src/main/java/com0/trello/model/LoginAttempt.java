package com0.trello.model;

public class LoginAttempt {
    private String email;
    private String password;

    public LoginAttempt(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword() { return password;
    }
    public void setPassword(String password) { this.password = password;
    }
    public String getEmail() { return email;
    }
    public void setEmail(String email) { this.email = email;
    }
}