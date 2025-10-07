package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import application.AnswerManagementPage;

public class AdminDashboardPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final User user;

    public AdminDashboardPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.user = user;
    }

    public void show(Stage stage) {
        // Title
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button updateBtn = new Button("Update Account");
        updateBtn.setOnAction(e -> new UpdateAccountPage(db, qMgr, aMgr, user).show(stage));
        
        Button manageUsers = new Button("Manage Users");
     // Actions (replace TODOs with real pages)
        manageUsers.setOnAction(e -> {
            System.out.println("Manage Users clicked");
            // new ManageUsersPage(db, qMgr, aMgr).show(stage);
        });
        
        Button manageQuestions = new Button("Manage Questions");
        manageQuestions.setOnAction(e -> {
        	System.out.println("Manage Questions clicked");
        	new QuestionManagementPage(db, qMgr, aMgr, user).show(stage);
            
        });
        
        Button manageAnswers = new Button("Manage Answers");
        manageAnswers.setOnAction(e -> {
            System.out.println("Manage Answers clicked");
            new AnswerManagementPage(db, qMgr, aMgr, user).show(stage);
        });
        
        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            // Return to login/selection page
            new SetupLoginSelectionPage(db, qMgr, aMgr).show(stage);
        });

        // Layout
        VBox root = new VBox(15, title, updateBtn, manageUsers, manageQuestions, manageAnswers, logout);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Admin Page");
        stage.show();
    }
}