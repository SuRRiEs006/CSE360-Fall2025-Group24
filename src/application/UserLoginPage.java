package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
	private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    
    public UserLoginPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, UserManager uMgr, CommentManager cMgr) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
    }



    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("admin", "student");
        roleBox.setPromptText("Select role");
        roleBox.setMaxWidth(250);

        Button loginButton = new Button("Login");

        loginButton.setOnAction(a -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String selectedRole = roleBox.getValue();

            if (selectedRole == null) {
                errorLabel.setText("Please select a role.");
                return;
            }

            try {
                User loggedIn = db.login(email, password); // fetches user + roles from DB

                if (loggedIn != null) {
                    // Check if the selected role is actually assigned to this user
                    if (loggedIn.hasRole(selectedRole)) {
                        UserManager uMgr = new UserManager(db);
                        CommentManager cMgr = new CommentManager(db.getConnection());

                        // Use RouteManager to centralize routing
                        RouteManager router = new RouteManager(db, qMgr, aMgr, uMgr, cMgr, loggedIn);
                        router.showDashboardFor(loggedIn, selectedRole, primaryStage);

                    } else {
                        errorLabel.setText("You do not have the role: " + selectedRole);
                    }
                } else {
                    errorLabel.setText("Invalid email or password.");
                }
            } catch (SQLException e) {
                errorLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(emailField, passwordField, roleBox, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
