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

    public UserLoginPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
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
            String role = roleBox.getValue();

            if (role == null) {
                errorLabel.setText("Please select a role.");
                return;
            }

            try {
                User loggedIn = db.login(email, password, role);

                if (loggedIn != null) {
                	
                	 UserManager uMgr = new UserManager(db);
                	
                    if ("admin".equalsIgnoreCase(role)) {
                        new AdminDashboardPage(db, qMgr, aMgr, loggedIn, uMgr).show(primaryStage);
                    } else {
                        new WelcomeLoginPage(db, qMgr, aMgr, loggedIn).show(primaryStage);
                    }
                } else {
                    errorLabel.setText("Invalid credentials or role.");
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
