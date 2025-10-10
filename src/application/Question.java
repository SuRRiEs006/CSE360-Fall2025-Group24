package application;

import java.time.LocalDateTime;

public class Question {
    private int id;
    private String title;
    private String description;
    private String createdBy;
    private boolean resolved;  
    private LocalDateTime createdAt;


    public Question(int id, String title, String description, String createdBy, LocalDateTime createdAt) {
        this(id, title, description, createdBy, false, createdAt); // default unresolved
    }

    public Question(int id, String title, String description, String createdBy, boolean resolved, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.resolved = resolved;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }

    //     Getters 
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCreatedBy() { return createdBy; }
    public boolean isResolved() { return resolved; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Derived property for UI table
    public String getStatus() {
        return resolved ? "Resolved" : "Unresolved";
    }

    //     Setters 
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }

    @Override
    public String toString() {
        return id + ": " + title + " - " + description 
               + " (by " + createdBy + ", " + getStatus() 
               + ", asked " + createdAt + ")";
    }
}
