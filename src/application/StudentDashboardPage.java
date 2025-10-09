package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class StudentDashboardPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;

    private TableView<Question> questionTable;
    private TableView<Answer> answerTable;
    private TableView<Comment> commentTable;

    public StudentDashboardPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, UserManager uMgr, CommentManager cMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
        this.user = user;
    }

    public void show(Stage stage) {
        Label title = new Label("Student Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //  Question Table 
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
        refreshQuestions();

        //     Answer Table 
        answerTable = new TableView<>();
        TableColumn<Answer, Integer> aIdCol = new TableColumn<>("ID");
        aIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Answer, Integer> qRefCol = new TableColumn<>("Question ID");
        qRefCol.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        TableColumn<Answer, String> aTextCol = new TableColumn<>("Answer");
        aTextCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
        TableColumn<Answer, String> aByCol = new TableColumn<>("Created By");
        aByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        answerTable.getColumns().addAll(aIdCol, qRefCol, aTextCol, aByCol);
        answerTable.setPrefHeight(200);
        refreshAnswers();

        //  Comment Table
        commentTable = new TableView<>();
        TableColumn<Comment, Integer> cIdCol = new TableColumn<>("ID");
        cIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Comment, String> cTextCol = new TableColumn<>("Comment");
        cTextCol.setCellValueFactory(new PropertyValueFactory<>("text"));
        TableColumn<Comment, String> cByCol = new TableColumn<>("Created By");
        cByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        commentTable.getColumns().addAll(cIdCol, cTextCol, cByCol);
        commentTable.setPrefHeight(150);
        
        //     Add selection listeners 
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldQ, newQ) -> {
            if (newQ != null) {
                refreshCommentsForQuestion(newQ.getId());
                answerTable.getSelectionModel().clearSelection(); // keep context clean
            }
        });

        answerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldA, newA) -> {
            if (newA != null) {
                refreshCommentsForAnswer(newA.getId());
                questionTable.getSelectionModel().clearSelection(); // keep context clean
            }
        });

        //     Question Controls 
        TextField titleField = new TextField();
        titleField.setPromptText("Question title");
        TextField descField = new TextField();
        descField.setPromptText("Question description");

        Button addQBtn = new Button("Ask Question");
        addQBtn.setOnAction(e -> {
            try {
                qMgr.addQuestion(titleField.getText(), descField.getText(), user.getName());
                refreshQuestions();
                titleField.clear();
                descField.clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        Button updateQBtn = new Button("Update Question");
        updateQBtn.setOnAction(e -> {
            Question selected = questionTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(user.getName())) {
                TextInputDialog dialog = new TextInputDialog(selected.getTitle());
                dialog.setHeaderText("Update Question Title");
                dialog.setContentText("New title:");
                dialog.showAndWait().ifPresent(newTitle -> {
                    try {
                        qMgr.updateQuestion(selected.getId(), newTitle, selected.getDescription());
                        refreshQuestions();
                    } catch (SQLException ex) { ex.printStackTrace(); }
                });
            } else {
                warn("You can only update your own questions.");
            }
        });

        Button deleteQBtn = new Button("Delete Question");
        deleteQBtn.setOnAction(e -> {
            Question selected = questionTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(user.getName())) {
                try {
                    qMgr.deleteQuestion(selected.getId());
                    refreshQuestions();
                } catch (SQLException ex) { ex.printStackTrace(); }
            } else {
                warn("You can only delete your own questions.");
            }
        });

        HBox qControls = new HBox(10, titleField, descField, addQBtn, updateQBtn, deleteQBtn);

        //     Answer Controls 
        TextField answerField = new TextField();
        answerField.setPromptText("Answer text");

        Button addABtn = new Button("Add Answer");
        addABtn.setOnAction(e -> {
            Question selectedQ = questionTable.getSelectionModel().getSelectedItem();
            if (selectedQ != null) {
                try {
                    aMgr.addAnswer(selectedQ.getId(), answerField.getText(), user.getName());
                    refreshAnswers();
                    answerField.clear();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        Button updateABtn = new Button("Update Answer");
        updateABtn.setOnAction(e -> {
            Answer selected = answerTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(user.getName())) {
                TextInputDialog dialog = new TextInputDialog(selected.getAnswerText());
                dialog.setHeaderText("Update Answer");
                dialog.setContentText("New answer:");
                dialog.showAndWait().ifPresent(newText -> {
                    try {
                        aMgr.updateAnswer(selected.getId(), newText);
                        refreshAnswers();
                    } catch (SQLException ex) { ex.printStackTrace(); }
                });
            } else {
                warn("You can only update your own answers.");
            }
        });

        Button deleteABtn = new Button("Delete Answer");
        deleteABtn.setOnAction(e -> {
            Answer selected = answerTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(user.getName())) {
                try {
                    aMgr.deleteAnswer(selected.getId());
                    refreshAnswers();
                } catch (SQLException ex) { ex.printStackTrace(); }
            } else {
                warn("You can only delete your own answers.");
            }
        });

        HBox aControls = new HBox(10, answerField, addABtn, updateABtn, deleteABtn);

        //     Comment Controls 
        TextField commentField = new TextField();
        commentField.setPromptText("Comment text");

        Button addCBtn = new Button("Add Comment");
        addCBtn.setOnAction(e -> {
            String text = commentField.getText().trim();
            if (text.isEmpty()) return;

            Question selectedQ = questionTable.getSelectionModel().getSelectedItem();
            Answer selectedA = answerTable.getSelectionModel().getSelectedItem();

            try {
                if (selectedQ != null) {
                    cMgr.addCommentToQuestion(selectedQ.getId(), text, user.getName());
                    refreshCommentsForQuestion(selectedQ.getId());
                } else if (selectedA != null) {
                    cMgr.addCommentToAnswer(selectedA.getId(), text, user.getName());
                    refreshCommentsForAnswer(selectedA.getId());
                }
                commentField.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteCBtn = new Button("Delete Comment");
        deleteCBtn.setOnAction(e -> {
            Comment selected = commentTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(user.getName())) {
                try {
                    cMgr.deleteComment(selected.getId());
                    Question q = questionTable.getSelectionModel().getSelectedItem();
                    Answer a = answerTable.getSelectionModel().getSelectedItem();
                    if (q != null) refreshCommentsForQuestion(q.getId());
                    else if (a != null) refreshCommentsForAnswer(a.getId());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                warn("You can only delete your own comments.");
            }
        });

        HBox cControls = new HBox(10, commentField, addCBtn, deleteCBtn);

        //     Layout 
        VBox root = new VBox(15, title, questionTable, qControls, answerTable, aControls, commentTable, cControls);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 900, 700));
        stage.setTitle("Student Dashboard");
        stage.show();
    } 

    //   Refresh Methods
    private void refreshQuestions() {
        try {
            ObservableList<Question> data =
                FXCollections.observableArrayList(qMgr.getAllQuestions());
            questionTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshAnswers() {
        try {
            ObservableList<Answer> data =
                FXCollections.observableArrayList(aMgr.getAllAnswers());
            answerTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCommentsForQuestion(int questionId) {
        try {
            ObservableList<Comment> data =
                FXCollections.observableArrayList(cMgr.getCommentsForQuestion(questionId));
            commentTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCommentsForAnswer(int answerId) {
        try {
            ObservableList<Comment> data =
                FXCollections.observableArrayList(cMgr.getCommentsForAnswer(answerId));
            commentTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //     Utility for warnings 
    private void warn(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
} 




