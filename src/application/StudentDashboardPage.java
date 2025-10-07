package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class StudentDashboardPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final CommentManager cMgr;
    private final User student;

    private TableView<Question> questionTable;
    private TableView<Answer> answerTable;
    private TableView<Comment> commentTable;

    public StudentDashboardPage(DatabaseHelper db, QuestionManager qMgr,
                                AnswerManager aMgr, CommentManager cMgr, User student) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.cMgr = cMgr;
        this.student = student;
    }

    public void show(Stage stage) {
        questionTable = new TableView<>();

        TableColumn<Question, Integer> qIdCol = new TableColumn<>("ID");
        qIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Question, String> qTitleCol = new TableColumn<>("Title");
        qTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Question, String> qDescCol = new TableColumn<>("Description");
        qDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Question, String> qByCol = new TableColumn<>("Created By");
        qByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        questionTable.getColumns().addAll(qIdCol, qTitleCol, qDescCol, qByCol);
        questionTable.setPrefHeight(200);

        refreshQuestions(); // make sure this loads the ObservableList<Question>
        

        //Add Question
        Button addQBtn = new Button("Ask Question");
        TextField qTitleField = new TextField(); qTitleField.setPromptText("Title");
        TextField qDescField = new TextField(); qDescField.setPromptText("Description");
        addQBtn.setOnAction(e -> {
            try {
                qMgr.addQuestion(qTitleField.getText(), qDescField.getText(), student.getName());
                refreshQuestions();
                qTitleField.clear(); qDescField.clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        
        Button editQBtn = new Button("Edit Question");
        editQBtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            if (q != null && q.getCreatedBy().equals(student.getName())) {
                TextInputDialog dialog = new TextInputDialog(q.getTitle());
                dialog.setHeaderText("Edit your question");
                dialog.setContentText("New title:");
                dialog.showAndWait().ifPresent(newTitle -> {
                    try {
                        qMgr.updateQuestion(q.getId(), newTitle, q.getDescription());
                        refreshQuestions();
                    } catch (SQLException ex) { ex.printStackTrace(); }
                });
            }
        });

        VBox questionBox = new VBox(5, questionTable, new HBox(5, qTitleField, qDescField, addQBtn));

        //     Answer Table 
        answerTable = new TableView<>();
        TableColumn<Answer, String> aTextCol = new TableColumn<>("Answer");
        aTextCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
        TableColumn<Answer, String> aByCol = new TableColumn<>("Created By");
        aByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        answerTable.getColumns().addAll(aTextCol, aByCol);
        
        // Accepted Answers
        TableColumn<Answer, Boolean> acceptedCol = new TableColumn<>("Accepted");
        acceptedCol.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        answerTable.getColumns().addAll(aTextCol, aByCol, acceptedCol);

        //    Highlight accepted answers
        answerTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Answer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.isAccepted()) {
                    setStyle("-fx-background-color: lightgreen;");
                } else {
                    setStyle("");
                }
            }
        });

        TextField aField = new TextField(); aField.setPromptText("Your answer");
        Button addABtn = new Button("Add Answer");
        addABtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            if (q != null) {
                try {
                    aMgr.addAnswer(q.getId(), aField.getText(), student.getName());
                    refreshAnswers(q.getId());
                    aField.clear();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        Button acceptBtn = new Button("Mark Accepted");
        acceptBtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            Answer a = answerTable.getSelectionModel().getSelectedItem();
            if (q != null && a != null && q.getCreatedBy().equals(student.getName())) {
                try {
                    aMgr.markAcceptedAnswer(a.getId(), q.getId());
                    refreshAnswers(q.getId());
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });
        
        //      Edit Answer (only if created by this student)
        Button editABtn = new Button("Edit Answer");
        editABtn.setOnAction(e -> {
            Answer a = answerTable.getSelectionModel().getSelectedItem();
            if (a != null && a.getCreatedBy().equals(student.getName())) {
                TextInputDialog dialog = new TextInputDialog(a.getAnswerText());
                dialog.setHeaderText("Edit your answer");
                dialog.setContentText("New answer:");
                dialog.showAndWait().ifPresent(newText -> {
                    try {
                        aMgr.updateAnswer(a.getId(), newText);
                        refreshAnswers(a.getQuestionId());
                    } catch (SQLException ex) { ex.printStackTrace(); }
                });
            }
        });

        //      Mark Accepted (only if this student asked the question)
        Button markAcceptBtn = new Button("Mark Accepted");
        acceptBtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            Answer a = answerTable.getSelectionModel().getSelectedItem();
            if (q != null && a != null && q.getCreatedBy().equals(student.getName())) {
                try {
                    aMgr.markAcceptedAnswer(a.getId(), q.getId());
                    refreshAnswers(q.getId());
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        VBox answerBox = new VBox(5, answerTable, new HBox(5, aField, addABtn, acceptBtn));

        //     Comment Table 
        commentTable = new TableView<>();
        TableColumn<Comment, String> cTextCol = new TableColumn<>("Comment");
        cTextCol.setCellValueFactory(new PropertyValueFactory<>("text"));
        TableColumn<Comment, String> cByCol = new TableColumn<>("By");
        cByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        commentTable.getColumns().addAll(cTextCol, cByCol);

        TextField cField = new TextField(); cField.setPromptText("Add clarification");
        Button addCBtn = new Button("Add Comment");
        addCBtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            Answer a = answerTable.getSelectionModel().getSelectedItem();
            try {
                if (a != null) {
                    cMgr.addCommentToAnswer(a.getId(), cField.getText(), student.getName());
                    refreshCommentsForAnswer(a.getId());
                } else if (q != null) {
                    cMgr.addCommentToQuestion(q.getId(), cField.getText(), student.getName());
                    refreshCommentsForQuestion(q.getId());
                }
                cField.clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        
        //      Delete Comment (only if created by this student)
        Button delCBtn = new Button("Delete Comment");
        delCBtn.setOnAction(e -> {
            Comment c = commentTable.getSelectionModel().getSelectedItem();
            if (c != null && c.getCreatedBy().equals(student.getName())) {
                try {
                    cMgr.deleteComment(c.getId());
                    if (c.getQuestionId() != null) refreshCommentsForQuestion(c.getQuestionId());
                    if (c.getAnswerId() != null) refreshCommentsForAnswer(c.getAnswerId());
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        VBox commentBox = new VBox(5, commentTable, new HBox(5, cField, addCBtn));

        //     Layout 
        VBox rightPane = new VBox(10, answerBox, commentBox);
        SplitPane split = new SplitPane(questionBox, rightPane);
        split.setDividerPositions(0.4);

        BorderPane root = new BorderPane(split);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 1000, 600));
        stage.setTitle("Student Dashboard");
        stage.show();

        refreshQuestions();

        //    Selection listeners
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldQ, newQ) -> {
            if (newQ != null) {
                refreshAnswers(newQ.getId());
                refreshCommentsForQuestion(newQ.getId());
            }
        });
        answerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldA, newA) -> {
            if (newA != null) {
                refreshCommentsForAnswer(newA.getId());
            }
        });
    }

    private void refreshQuestions() {
        try {
            ObservableList<Question> data = FXCollections.observableArrayList(qMgr.getAllQuestions());
            questionTable.setItems(data);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshAnswers(int questionId) {
        try {
            ObservableList<Answer> data = FXCollections.observableArrayList(aMgr.getAnswersByQuestionId(questionId));
            answerTable.setItems(data);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshCommentsForQuestion(int questionId) {
        try {
            ObservableList<Comment> data = FXCollections.observableArrayList(cMgr.getCommentsForQuestion(questionId));
            commentTable.setItems(data);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshCommentsForAnswer(int answerId) {
        try {
            ObservableList<Comment> data = FXCollections.observableArrayList(cMgr.getCommentsForAnswer(answerId));
            commentTable.setItems(data);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}


