package application;

import databasePart1.DatabaseHelper;
import application.Question;
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
import java.util.Optional;

public class QuestionManagementPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final User user;

    private TableView<Question> questionTable;
    private TableView<Answer> answerTable;

    public QuestionManagementPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.user = user;
    }

    public void show(Stage stage) {
        Label title = new Label("Manage Questions & Answers");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Question Table
        questionTable = new TableView<>();
        TableColumn<Question, Integer> qIdCol = new TableColumn<>("ID");
        qIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Question, String> qTitleCol = new TableColumn<>("Title");
        qTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Question, String> qDescCol = new TableColumn<>("Description");
        qDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Question, String> createdByCol = new TableColumn<>("Created By");
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        questionTable.getColumns().addAll(qIdCol, qTitleCol, qDescCol, createdByCol);
        questionTable.setPrefHeight(200);
        refreshQuestions();

        // Answer Table
        answerTable = new TableView<>();
        TableColumn<Answer, Integer> aIdCol = new TableColumn<>("ID");
        aIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Answer, Integer> qRefCol = new TableColumn<>("Question ID");
        qRefCol.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        TableColumn<Answer, String> aTextCol = new TableColumn<>("Answer");
        aTextCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
        TableColumn<Answer, String> aCreatedByCol = new TableColumn<>("Created By");
        aCreatedByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        answerTable.getColumns().addAll(aIdCol, qRefCol, aTextCol, aCreatedByCol);
        answerTable.setPrefHeight(200);
        refreshAnswers();

        // Question Controls
        TextField titleField = new TextField();
        titleField.setPromptText("Question title");
        TextField descField = new TextField();
        descField.setPromptText("Question description");

        Button addQBtn = new Button("Add Question");
        addQBtn.setOnAction(e -> {
            try {
                qMgr.addQuestion(titleField.getText(), descField.getText(), user.getName());
                refreshQuestions();
                titleField.clear();
                descField.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button updateQBtn = new Button("Update Question");
        updateQBtn.setOnAction(e -> {
            Question selectedQ = questionTable.getSelectionModel().getSelectedItem();
            if (selectedQ != null) {
                TextInputDialog titleDialog = new TextInputDialog(selectedQ.getTitle());
                titleDialog.setHeaderText("Update Question Title");
                titleDialog.setContentText("New title:");
                Optional<String> newTitleOpt = titleDialog.showAndWait();

                if (newTitleOpt.isPresent()) {
                    String newTitle = newTitleOpt.get();
                    TextInputDialog descDialog = new TextInputDialog(selectedQ.getDescription());
                    descDialog.setHeaderText("Update Question Description");
                    descDialog.setContentText("New description:");
                    descDialog.getDialogPane().setPrefHeight(300);   
                    descDialog.getDialogPane().setPrefWidth(400); 

                    descDialog.showAndWait().ifPresent(newDesc -> {
                        try {
                            qMgr.updateQuestion(selectedQ.getId(), newTitle, newDesc);
                            refreshQuestions();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });

        Button manageAnswersBtn = new Button("Manage Answers for Selected Question");
        manageAnswersBtn.setOnAction(event -> {
            Question selectedQ = questionTable.getSelectionModel().getSelectedItem();
            if (selectedQ != null) {
                new AnswerManagementPage(db, qMgr, aMgr, user, selectedQ).show(stage);
            }
        });

        Button deleteQBtn = new Button("Delete Question");
        deleteQBtn.setOnAction(event -> {
            Question questionToDelete = questionTable.getSelectionModel().getSelectedItem();
            if (questionToDelete != null) {
                try {
                    qMgr.deleteQuestion(questionToDelete.getId());
                    refreshQuestions();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox qControls = new HBox(10, titleField, descField, addQBtn, updateQBtn, deleteQBtn, manageAnswersBtn);

        // Answer Controls
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
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button updateABtn = new Button("Update Answer");
        updateABtn.setOnAction(e -> {
            Answer selectedAnswer = answerTable.getSelectionModel().getSelectedItem();
            if (selectedAnswer != null) {
                TextInputDialog dialog = new TextInputDialog(selectedAnswer.getAnswerText());
                dialog.setHeaderText("Update Answer");
                dialog.setContentText("New answer text:");
                dialog.showAndWait().ifPresent(newText -> {
                    try {
                        aMgr.updateAnswer(selectedAnswer.getId(), newText);
                        refreshAnswers();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });

        Button deleteABtn = new Button("Delete Answer");
        deleteABtn.setOnAction(e -> {
            Answer answerToDelete = answerTable.getSelectionModel().getSelectedItem();
            if (answerToDelete != null) {
                try {
                    aMgr.deleteAnswer(answerToDelete.getId());
                    refreshAnswers();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox aControls = new HBox(10, answerField, addABtn, updateABtn, deleteABtn);

        // Back Button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> new AdminDashboardPage(db, qMgr, aMgr, user).show(stage));

        VBox root = new VBox(15, title, questionTable, qControls, answerTable, aControls, backBtn);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Question Management");
        stage.show();
    }

    private void refreshQuestions() {
        try {
            ObservableList<Question> data = FXCollections.observableArrayList(qMgr.getAllQuestions());
            questionTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshAnswers() {
        try {
            ObservableList<Answer> data = FXCollections.observableArrayList(aMgr.getAllAnswers());
            answerTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}