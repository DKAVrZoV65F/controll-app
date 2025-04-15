package com.student.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private boolean activated;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }
}
