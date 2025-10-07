package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableRowExpanderColumn;
import java.util.List;

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
    	Label title = new Label("Student Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 				 Question Table 
        questionTable = new TableView<>();
        TableColumn<Question, Integer> qIdCol = new TableColumn<>("ID");
        qIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Question, String> qTitleCol = new TableColumn<>("Title");
        qTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Question, String> qDescCol = new TableColumn<>("Description");
        qDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Question, String> qByCol = new TableColumn<>("Created By");
        qByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        
        TableRowExpanderColumn<Question> qExpander = new TableRowExpanderColumn<>(param -> {
            VBox box = new VBox(5);
            try {
                List<Comment> comments = cMgr.getCommentsForQuestion(param.getValue().getId());
                if (comments.isEmpty()) {
                    box.getChildren().add(new Label("No comments yet."));
                } else {
                    for (Comment c : comments) {
                        Label lbl = new Label(c.getCreatedBy() + ": " + c.getText());
                        lbl.setWrapText(true);
                        lbl.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 3;");
                        box.getChildren().add(lbl);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return box;
        });

        // Add expander as the first column
        questionTable.getColumns().addAll(qExpander, qIdCol, qTitleCol, qDescCol, qByCol);

        questionTable.setPrefHeight(200);
        refreshQuestions(); 
  
        // Question controls
        Button addQBtn = new Button("Ask Question");
        addQBtn.setOnAction(e -> {
            Dialog<Question> dialog = new Dialog<>();
            dialog.setTitle("Ask Question");
            dialog.setHeaderText("Enter your question:");

            TextField titleField = new TextField();
            TextArea descField = new TextArea();
            descField.setWrapText(true);

            VBox content = new VBox(10, new Label("Title:"), titleField, new Label("Description:"), descField);
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    return new Question(0, titleField.getText(), descField.getText(), student.getName());
                }
                return null;
            });

            dialog.showAndWait().ifPresent(q -> {
                try {
                    qMgr.addQuestion(q.getTitle(), q.getDescription(), q.getCreatedBy());
                    refreshQuestions();
                } catch (SQLException ex) { ex.printStackTrace(); }
            });
        });

        Button editQBtn = new Button("Edit Question");
        editQBtn.setOnAction(e -> {
            Question selected = questionTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(student.getName())) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Edit Question");
                TextArea descField = new TextArea(selected.getDescription());
                dialog.getDialogPane().setContent(descField);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                dialog.setResultConverter(btn -> btn == ButtonType.OK ? descField.getText() : null);

                dialog.showAndWait().ifPresent(newDesc -> {
                    try {
                        qMgr.updateQuestion(selected.getId(), selected.getTitle(), newDesc);
                        refreshQuestions();
                    } catch (SQLException ex) { ex.printStackTrace(); }
                });
            }
        });

        HBox qControls = new HBox(10, addQBtn, editQBtn);

        // 			 Answer Table 
        answerTable = new TableView<>();
        TableColumn<Answer, Integer> aIdCol = new TableColumn<>("ID");
        aIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Answer, Integer> qRefCol = new TableColumn<>("Question ID");
        qRefCol.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        TableColumn<Answer, String> aTextCol = new TableColumn<>("Answer");
        aTextCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
        TableColumn<Answer, String> aByCol = new TableColumn<>("Created By");
        aByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        TableColumn<Answer, Boolean> acceptedCol = new TableColumn<>("Accepted");
        acceptedCol.setCellValueFactory(new PropertyValueFactory<>("isAccepted"));

        // expander column 
        TableRowExpanderColumn<Answer> aExpander = new TableRowExpanderColumn<>(param -> {
            VBox box = new VBox(5);
            try {
                List<Comment> comments = cMgr.getCommentsForAnswer(param.getValue().getId());
                if (comments.isEmpty()) {
                    box.getChildren().add(new Label("No comments yet."));
                } else {
                    for (Comment c : comments) {
                        Label lbl = new Label(c.getCreatedBy() + ": " + c.getText());
                        lbl.setWrapText(true);
                        lbl.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 3;");
                        box.getChildren().add(lbl);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return box;
        });

        // Add expander as the first column
        answerTable.getColumns().addAll(aExpander, aIdCol, qRefCol, aTextCol, aByCol, acceptedCol);

        answerTable.setPrefHeight(200);

        // Answer controls
        Button addABtn = new Button("Add Answer");
        addABtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            if (q == null) {
                new Alert(Alert.AlertType.WARNING, "Select a question first.").showAndWait();
                return;
            }
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Add Answer");
            TextArea answerField = new TextArea();
            dialog.getDialogPane().setContent(answerField);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.setResultConverter(btn -> btn == ButtonType.OK ? answerField.getText() : null);

            dialog.showAndWait().ifPresent(text -> {
                try {
                    aMgr.addAnswer(q.getId(), text, student.getName());
                    refreshAnswers(q.getId());
                } catch (SQLException ex) { ex.printStackTrace(); }
            });
        });

        Button editABtn = new Button("Edit Answer");
        editABtn.setOnAction(e -> {
            Answer selected = answerTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getCreatedBy().equals(student.getName())) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Edit Answer");
                TextArea field = new TextArea(selected.getAnswerText());
                dialog.getDialogPane().setContent(field);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                dialog.setResultConverter(btn -> btn == ButtonType.OK ? field.getText() : null);

                dialog.showAndWait().ifPresent(newText -> {
                    try {
                        aMgr.updateAnswer(selected.getId(), newText);
                        refreshAnswers(selected.getQuestionId());
                    } catch (SQLException ex) { ex.printStackTrace(); }
                });
            }
        });

        Button acceptABtn = new Button("Mark Accepted");
        acceptABtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            Answer a = answerTable.getSelectionModel().getSelectedItem();
            if (q != null && a != null && q.getCreatedBy().equals(student.getName())) {
                try {
                    aMgr.markAcceptedAnswer(a.getId(), q.getId());
                    refreshAnswers(q.getId());
                } catch (SQLException ex) { ex.printStackTrace(); }
            }

        });
        HBox aControls = new HBox(10, addABtn, editABtn, acceptABtn);
    	
        //  Comment Table 
        commentTable = new TableView<>();
        TableColumn<Comment, String> cTextCol = new TableColumn<>("Comment");
        cTextCol.setCellValueFactory(new PropertyValueFactory<>("text"));
        TableColumn<Comment, String> cByCol = new TableColumn<>("By");
        cByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        commentTable.getColumns().setAll(cTextCol, cByCol);
        commentTable.setPrefHeight(150);

        TextField cField = new TextField(); cField.setPromptText("Add clarification");
        Button addCBtn = new Button("Add Comment");
        addCBtn.setOnAction(e -> {
            Question q = questionTable.getSelectionModel().getSelectedItem();
            Answer a = answerTable.getSelectionModel().getSelectedItem();

            if (q == null && a == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a question or answer first.").showAndWait();
                return;
            }

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Add Comment");
            dialog.setHeaderText("Write your comment:");

            TextArea field = new TextArea();
            field.setWrapText(true);
            field.setPrefRowCount(4);

            dialog.getDialogPane().setContent(field);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btn -> btn == ButtonType.OK ? field.getText().trim() : null);

            dialog.showAndWait().ifPresent(text -> {
                if (text.isEmpty()) return;
                try {
                    if (a != null) {
                        cMgr.addCommentToAnswer(a.getId(), text, student.getName());
                        refreshCommentsForAnswer(a.getId());
                    } else {
                        cMgr.addCommentToQuestion(q.getId(), text, student.getName());
                        refreshCommentsForQuestion(q.getId());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to add comment.").showAndWait();
                }
            });
        });
        
        Button editCBtn = new Button("Edit Comment");
        editCBtn.setOnAction(e -> {
            Comment selected = commentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a comment to edit.").showAndWait();
                return;
            }
            if (!selected.getCreatedBy().equals(student.getName())) {
                new Alert(Alert.AlertType.WARNING, "You can only edit your own comments.").showAndWait();
                return;
            }

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Edit Comment");
            dialog.setHeaderText("Update your comment:");

            TextArea field = new TextArea(selected.getText());
            field.setWrapText(true);
            field.setPrefRowCount(4);

            dialog.getDialogPane().setContent(field);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btn -> btn == ButtonType.OK ? field.getText() : null);

            dialog.showAndWait().ifPresent(newText -> {
                try {
                    cMgr.updateComment(selected.getId(), newText);
                    selected.setText(newText); // update in table model
                    commentTable.refresh();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to update comment.").showAndWait();
                }
            });
        });

        // Delete Comment Button
        Button delCBtn = new Button("Delete Comment");
        delCBtn.setOnAction(e -> {
            Comment selected = commentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a comment to delete.").showAndWait();
                return;
            }
            if (!selected.getCreatedBy().equals(student.getName())) {
                new Alert(Alert.AlertType.WARNING, "You can only delete your own comments.").showAndWait();
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this comment?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Delete Comment");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        cMgr.deleteComment(selected.getId());

                        if (selected.getQuestionId() != null) {
                            refreshCommentsForQuestion(selected.getQuestionId());
                            questionTable.refresh();
                        } else if (selected.getAnswerId() != null) {
                            refreshCommentsForAnswer(selected.getAnswerId());
                            answerTable.refresh();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Failed to delete comment.").showAndWait();
                    }
                }
            });
        });
        
        HBox cControls = new HBox(10, cField, addCBtn, editCBtn, delCBtn);

        // Layout
        VBox root = new VBox(15,
                title,
                questionTable, qControls,
                answerTable, aControls,
                commentTable, cControls
        );
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 900, 700));
        stage.setTitle("Student Dashboard");
        stage.show();

        // Selection listeners     
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldQ, newQ) -> {
            if (newQ != null) {
                refreshAnswers(newQ.getId());
            } else {
                answerTable.getItems().clear();
            }
        });
    }       
        
    private void refreshQuestions() {
        try {
            ObservableList<Question> data =
                    FXCollections.observableArrayList(qMgr.getAllQuestions());
            questionTable.setItems(data != null ? data : FXCollections.observableArrayList());
        } catch (SQLException e) {
            e.printStackTrace();
            questionTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void refreshAnswers(int questionId) {
        try {
            ObservableList<Answer> data =
                    FXCollections.observableArrayList(aMgr.getAnswersByQuestionId(questionId));
            answerTable.setItems(data != null ? data : FXCollections.observableArrayList());
        } catch (SQLException e) {
            e.printStackTrace();
            answerTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void refreshCommentsForQuestion(int questionId) {
        try {
            ObservableList<Comment> data =
                    FXCollections.observableArrayList(cMgr.getCommentsForQuestion(questionId));
            commentTable.setItems(data != null ? data : FXCollections.observableArrayList());
        } catch (SQLException e) {
            e.printStackTrace();
            commentTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void refreshCommentsForAnswer(int answerId) {
        try {
            ObservableList<Comment> data =
                    FXCollections.observableArrayList(cMgr.getCommentsForAnswer(answerId));
            commentTable.setItems(data != null ? data : FXCollections.observableArrayList());
        } catch (SQLException e) {
            e.printStackTrace();
            commentTable.setItems(FXCollections.observableArrayList());
        }
    }
}



