package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UpdateAccountPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;

    public UpdateAccountPage(DatabaseHelper db,
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
        Label title = new Label("Update Account");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField nameField = new TextField(user.getName());
        TextField addressField = new TextField(user.getAddress());
        TextField emailField = new TextField(user.getEmail());
        emailField.setEditable(false);

        PasswordField currentPass = new PasswordField();
        currentPass.setPromptText("Current Password");

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password");

        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm New Password");

        Label status = new Label();

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            try {
                // verify current password
                if (!db.verifyPassword(user.getEmail(), currentPass.getText())) {
                    status.setText("Current password incorrect.");
                    return;
                }
                // check new password match
                if (!newPass.getText().equals(confirmPass.getText())) {
                    status.setText("New passwords do not match.");
                    return;
                }
                // update DB
                db.updateUser(user.getEmail(), nameField.getText(), addressField.getText(), newPass.getText());
                
                user.setName(nameField.getText());
                user.setAddress(addressField.getText());
                user.setPassword(newPass.getText());
                
                status.setText("Account updated successfully.");
            } catch (SQLException ex) {
                status.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            
            RouteManager router = new RouteManager(db, qMgr, aMgr, uMgr, cMgr, user);
            
            String role = user.getRoles().iterator().next();
            router.showDashboardFor(user, role, stage);
        });


        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 0, 0, 2, 1);
        grid.addRow(1, new Label("Name:"), nameField);
        grid.addRow(2, new Label("Address:"), addressField);
        grid.addRow(3, new Label("Email:"), emailField);
        grid.addRow(4, new Label("Current Password:"), currentPass);
        grid.addRow(5, new Label("New Password:"), newPass);
        grid.addRow(6, new Label("Confirm Password:"), confirmPass);
        grid.add(saveBtn, 0, 7);
        grid.add(backBtn, 1, 7);
        grid.add(status, 0, 8, 2, 1);

        stage.setScene(new Scene(grid, 500, 400));
        stage.setTitle("Update Account");
        stage.show();
    }
}