package application;

public class Question {
	private int id;
    private String title;
    private String description;
    private String createdBy;

    public Question(int id, String title, String description, String createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCreatedBy() { return createdBy; }
    
    // Setters 
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }


    @Override
    public String toString() {
        return id + ": " + title + " - " + description + " (by " + createdBy + ")";
    }
}
