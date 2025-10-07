package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * UserInterface provides the main user-facing screens:
 * - Menu
 * - Questions CRUD
 * - Answers CRUD
 */
public class UserInterface {

    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final Stage primaryStage;
    private final User currentUser;

    // Panes for navigation
    private final Pane paneMenu = new Pane();
    private final Pane paneQuestions = new Pane();
    private final Pane paneAnswers = new Pane();
    private final StackPane theRoot = new StackPane();

    // Shared text areas
    private TextArea txtQuestionsList;
    private TextArea txtAnswersList;

    public UserInterface(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, Stage stage, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.primaryStage = stage;
        this.currentUser = user;
    }

    /** Entry point after login */
    public void showMenu() {
        buildMenuScreen();
        primaryStage.setScene(new Scene(theRoot, 800, 400));
        primaryStage.setTitle("User Interface");
        primaryStage.show();
    }

    // ---------------- MENU ----------------
    private void buildMenuScreen() {
        paneMenu.getChildren().clear();

        Label title = new Label("Welcome, " + currentUser.getName());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnQuestions = new Button("Manage Questions");
        btnQuestions.setOnAction(e -> buildQuestionsScreen());

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            new UserLoginPage(db, qMgr, aMgr).show(primaryStage);
        });

        VBox vbox = new VBox(20, title, btnQuestions, btnLogout);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-alignment: center;");

        paneMenu.getChildren().add(vbox);
        theRoot.getChildren().setAll(paneMenu);
    }

    // ---------------- QUESTIONS ----------------
    private void buildQuestionsScreen() {
        paneQuestions.getChildren().clear();

        Label qTitle = new Label("Questions");
        qTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Question Title");

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Question Description");

        Button btnAdd = new Button("Add Question");
        btnAdd.setOnAction(e -> {
            try {
                db.addQuestion(txtTitle.getText(), txtDesc.getText(), currentUser.getEmail());
                txtTitle.clear();
                txtDesc.clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        Button btnView = new Button("View Questions");
        btnView.setOnAction(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Question q : db.getAllQuestions()) {
                    sb.append(q.toString()).append("\n");
                }
                txtQuestionsList.setText(sb.toString());
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        //manage answers for a specific question ID entered
        TextField txtQuestionId = new TextField();
        txtQuestionId.setPromptText("Question ID for Answers");

        Button btnAnswers = new Button("Manage Answers");
        btnAnswers.setOnAction(e -> {
            try {
                int qId = Integer.parseInt(txtQuestionId.getText());
                buildAnswersScreen(qId);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        txtQuestionsList = new TextArea();
        txtQuestionsList.setEditable(false);
        txtQuestionsList.setPrefHeight(150);

        // Update/Delete
        TextField txtUpdateId = new TextField();
        txtUpdateId.setPromptText("ID to Update");

        TextField txtUpdateTitle = new TextField();
        txtUpdateTitle.setPromptText("New Title");

        TextField txtUpdateDesc = new TextField();
        txtUpdateDesc.setPromptText("New Description");

        Button btnUpdate = new Button("Update Question");
        btnUpdate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtUpdateId.getText());
                db.updateQuestion(id, txtUpdateTitle.getText(), txtUpdateDesc.getText());
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        TextField txtDeleteId = new TextField();
        txtDeleteId.setPromptText("ID to Delete");

        Button btnDelete = new Button("Delete Question");
        btnDelete.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtDeleteId.getText());
                db.deleteQuestion(id);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        Button btnBack = new Button("Back to Menu");
        btnBack.setOnAction(e -> buildMenuScreen());

        VBox vbox = new VBox(15,
                qTitle,
                txtTitle, txtDesc,
                new HBox(15, btnAdd, btnView, txtQuestionId, btnAnswers),
                txtQuestionsList,
                new HBox(10, txtUpdateId, txtUpdateTitle, txtUpdateDesc, btnUpdate),
                new HBox(10, txtDeleteId, btnDelete),
                btnBack
        );
        vbox.setPadding(new Insets(20));

        paneQuestions.getChildren().add(vbox);
        theRoot.getChildren().setAll(paneQuestions);
    }

    // ---------------- ANSWERS ----------------
    private void buildAnswersScreen(int questionId) {
        paneAnswers.getChildren().clear();

        Label aTitle = new Label("Answers for Question " + questionId);
        aTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtAnswer = new TextField();
        txtAnswer.setPromptText("Enter Answer");

        Button btnAdd = new Button("Add Answer");
        btnAdd.setOnAction(e -> {
            try {
                db.addAnswer(questionId, txtAnswer.getText(), currentUser.getEmail());
                txtAnswer.clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        Button btnView = new Button("View Answers");
        btnView.setOnAction(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Answer a : db.getAnswersByQuestionId(questionId)) {
                    sb.append(a.toString()).append("\n");
                }
                txtAnswersList.setText(sb.toString());
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        txtAnswersList = new TextArea();
        txtAnswersList.setEditable(false);
        txtAnswersList.setPrefHeight(150);

        // Update/Delete
        TextField txtUpdateAnswerId = new TextField();
        txtUpdateAnswerId.setPromptText("Answer ID to Update");

        TextField txtUpdateAnswerText = new TextField();
        txtUpdateAnswerText.setPromptText("New Answer Text");

        Button btnUpdateAnswer = new Button("Update Answer");
        btnUpdateAnswer.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtUpdateAnswerId.getText());
                db.updateAnswer(id, txtUpdateAnswerText.getText());
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        TextField txtDeleteAnswerId = new TextField();
        txtDeleteAnswerId.setPromptText("Answer ID to Delete");

        Button btnDeleteAnswer = new Button("Delete Answer");
        btnDeleteAnswer.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtDeleteAnswerId.getText());
                db.deleteAnswer(id);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        Button btnBack = new Button("Back to Questions");
        btnBack.setOnAction(e -> buildQuestionsScreen());

        VBox vbox = new VBox(15,
                aTitle,
                txtAnswer,
                new HBox(15, btnAdd, btnView),
                txtAnswersList,
                new HBox(10, txtUpdateAnswerId, txtUpdateAnswerText, btnUpdateAnswer),
                new HBox(10, txtDeleteAnswerId, btnDeleteAnswer),
                btnBack
        );
        vbox.setPadding(new Insets(20));

        paneAnswers.getChildren().add(vbox);
        theRoot.getChildren().setAll(paneAnswers);
    }
}
