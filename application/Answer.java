package application;

public class Answer {
	private int id;
    private int questionId;
    private String answerText;
    private String createdBy;
    private boolean isAccepted;

    public Answer(int id, int questionId, String answerText, String createdBy, boolean isAccepted) {
        this.id = id;
        this.questionId = questionId;
        this.answerText = answerText;
        this.createdBy = createdBy;
        this.isAccepted = isAccepted;
    }
    
    public Answer(int id, int questionId, String answerText, String createdBy) {
        this(id, questionId, answerText, createdBy, false);
    }

    // Getters
    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getAnswerText() { return answerText; }
    public String getCreatedBy() { return createdBy; }
    public boolean isAccepted() { return isAccepted; }
    
    // Setters
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    
    public void setAccepted(boolean accepted) {          
        this.isAccepted = accepted;
    }

    @Override
    public String toString() {
        return id + " [Q" + questionId + "]: " + answerText + " (" + createdBy + ")" +
               (isAccepted ? " [ACCEPTED]" : "");
    }
}

