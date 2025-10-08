package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
	private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;

    public SetupAccountPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
    }
    
    //email validation
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        TextField nameField = new TextField(); nameField.setPromptText("Full name");
        TextField addressField = new TextField(); addressField.setPromptText("Address");
        
        TextField emailField = new TextField(); emailField.setPromptText("Email");
        
        // requirement labels
        Label emailError = new Label();
        emailError.setTextFill(Color.RED);
        
        //Listener
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isValidEmail(newVal)) {
                emailError.setText("Invalid email format. Use example@email.com");
            } else {
                emailError.setText("");
            }
        });
        
        PasswordField passwordField = new PasswordField(); passwordField.setPromptText("Password");
        
        Label reqUpper = new Label("At least one uppercase letter - Not yet satisfied");
        Label reqLower = new Label("At least one lowercase letter - Not yet satisfied");
        Label reqDigit = new Label("At least one numeric digit - Not yet satisfied");
        Label reqSpecial = new Label("At least one special character - Not yet satisfied");
        Label reqLength = new Label("At least eight characters - Not yet satisfied");
        
        //Requirement labels
        reqUpper.setTextFill(Color.RED);
        reqLower.setTextFill(Color.RED);
        reqDigit.setTextFill(Color.RED);
        reqSpecial.setTextFill(Color.RED);
        reqLength.setTextFill(Color.RED);
        
        //Listener to password fields
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.matches(".*[A-Z].*")) { reqUpper.setText("At least one uppercase letter - Satisfied"); reqUpper.setTextFill(Color.GREEN); }
            else { reqUpper.setText("At least one uppercase letter - Not yet satisfied"); reqUpper.setTextFill(Color.RED); }

            if (newVal.matches(".*[a-z].*")) { reqLower.setText("At least one lowercase letter - Satisfied"); reqLower.setTextFill(Color.GREEN); }
            else { reqLower.setText("At least one lowercase letter - Not yet satisfied"); reqLower.setTextFill(Color.RED); }

            if (newVal.matches(".*\\d.*")) { reqDigit.setText("At least one numeric digit - Satisfied"); reqDigit.setTextFill(Color.GREEN); }
            else { reqDigit.setText("At least one numeric digit - Not yet satisfied"); reqDigit.setTextFill(Color.RED); }

            if (newVal.matches(".*[^a-zA-Z0-9].*")) { reqSpecial.setText("At least one special character - Satisfied"); reqSpecial.setTextFill(Color.GREEN); }
            else { reqSpecial.setText("At least one special character - Not yet satisfied"); reqSpecial.setTextFill(Color.RED); }

            if (newVal.length() >= 8) { reqLength.setText("At least eight characters - Satisfied"); reqLength.setTextFill(Color.GREEN); }
            else { reqLength.setText("At least eight characters - Not yet satisfied"); reqLength.setTextFill(Color.RED); }
        });

        Label status = new Label(); status.setStyle("-fx-text-fill: red;");
        Button setupBtn = new Button("Create account");

        setupBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (name.isEmpty() || address.isEmpty() || email.isEmpty()) {
                status.setText("All fields required."); return;
            }
            
            if (!isValidEmail(email)) {
                status.setText("Invalid email format. Please use example@email.com");
                return;
            }

            String err = PasswordEvaluator.evaluatePassword(password);
            if (!err.isEmpty()) {
                status.setText("Password invalid: " + err);
                return;
            }

            try {
                User user = new User(password, name, address, email); // use the 4‑arg constructor
                db.registerFull(user); // DB layer assigns role

                status.setStyle("-fx-text-fill: green;");
                status.setText("Account created successfully.");

                // Fetch back from DB if you want the fully populated user
                user = db.getUserByEmail(email);
                new WelcomeLoginPage(db, qMgr, aMgr, user).show(primaryStage);

            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });
        
        VBox layout = new VBox(10,
        	    nameField,
        	    addressField,
        	    emailField,
        	    emailError,
        	    passwordField,

        	    reqUpper,
        	    reqLower,
        	    reqDigit,
        	    reqSpecial,
        	    reqLength,

        	    setupBtn,
        	    status
        	);

        	layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        	primaryStage.setScene(new Scene(layout, 800, 400));
        	primaryStage.setTitle("Account setup");
        	primaryStage.show();
        }
    }
