package com.taskflow.app;

public class Task {
    private long id;
    private String description;
    private boolean completed;
    private long createdAt;

    public Task(long id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
