package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboardPage {
	private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;

    public AdminDashboardPage(DatabaseHelper db,
                              QuestionManager qMgr,
                              AnswerManager aMgr,
                              UserManager uMgr,
                              CommentManager cMgr,
                              User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
        this.user = user;
    }

    public void show(Stage stage) {
        // Title
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button updateBtn = new Button("Update Account");
        updateBtn.setOnAction(e -> new UpdateAccountPage(db, qMgr, aMgr, uMgr, cMgr, user).show(stage));
        
        Button manageUsers = new Button("Manage Users");
        manageUsers.setOnAction(e -> {
            ManageUsersPage manageUsersPage =
                new ManageUsersPage(db, qMgr, aMgr, uMgr, cMgr, user);
            manageUsersPage.show(stage);
        });
        
        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            // Return to login/selection page
            new SetupLoginSelectionPage(db, qMgr, aMgr, uMgr, cMgr).show(stage);
        });

        // Layout
        VBox root = new VBox(15, title, updateBtn, manageUsers, logout);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Admin Page");
        stage.show();
    }
}