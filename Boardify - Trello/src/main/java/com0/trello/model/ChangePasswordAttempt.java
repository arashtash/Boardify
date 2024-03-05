package com0.trello.model;

public class ChangePasswordAttempt {
    private String email;
    private String securityAnswer;
    private String password;

    public ChangePasswordAttempt(String email, String securityAnswer, String password) {
        this.email = email;
        this.securityAnswer = securityAnswer;
        this.password = password;
    }

    public String getPassword() { return password;
    }
    public void setPassword(String password) { this.password = password;
    }
    public String getSecurityAnswer() { return securityAnswer;
    }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer;
    }
    public String getEmail() { return email;
    }
    public void setEmail(String email) { this.email = email;
    }
}