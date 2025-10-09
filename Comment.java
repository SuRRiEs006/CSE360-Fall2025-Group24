package application;

public class Comment {
	private int id;
    private Integer questionId;
    private Integer answerId;
    private String text;
    private String createdBy;
    private Integer parentCommentId;


    public Comment(int id, Integer questionId, Integer answerId, String text, String createdBy, Integer parentCommentId) {
        this.id = id;
        this.questionId = questionId;
        this.answerId = answerId;
        this.text = text;
        this.createdBy = createdBy;
        this.parentCommentId = parentCommentId;
    }
    
    //Getters
    public int getId() { return id; }
    public Integer getQuestionId() { return questionId; }
    public Integer getAnswerId() { return answerId; }
    public String getText() { return text; }
    public String getCreatedBy() { return createdBy; }
    
    //Setter
    public void setText(String text) {
        this.text = text;
    }
}

