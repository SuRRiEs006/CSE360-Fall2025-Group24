package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.DatabaseHelper;

/**
 * The AdminSetupPage handles the setup process for creating the very first administrator account.
 * This page should only be shown once, when no users exist in the system.
 */
public class AdminSetupPage {

	private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final User user;

    public AdminSetupPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        // Input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        nameField.setMaxWidth(250);

        TextField addressField = new TextField();
        addressField.setPromptText("Enter Address");
        addressField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button setupButton = new Button("Setup Admin");

        setupButton.setOnAction(a -> {
            String name = nameField.getText();
            String address = addressField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Name, Email, and Password are required.");
                return;
            }

            try {
                // Role will be assigned automatically in DatabaseHelper (first = admin)
                User user = new User(password, null, name, address, email);
                db.registerFull(user);

                System.out.println("Administrator setup completed.");

                // Navigate to Welcome/Login page
                new WelcomeLoginPage(db, qMgr, aMgr, user).show(primaryStage);

            } catch (SQLException e) {
                errorLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, nameField, addressField, emailField, passwordField, setupButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
