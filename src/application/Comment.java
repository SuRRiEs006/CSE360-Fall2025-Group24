package application;

public class Comment {
	private int id;
    private Integer questionId;
    private Integer answerId;
    private String text;
    private String createdBy;

    public Comment(int id, Integer questionId, Integer answerId, String text, String createdBy) {
        this.id = id;
        this.questionId = questionId;
        this.answerId = answerId;
        this.text = text;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public Integer getQuestionId() { return questionId; }
    public Integer getAnswerId() { return answerId; }
    public String getText() { return text; }
    public String getCreatedBy() { return createdBy; }
}

