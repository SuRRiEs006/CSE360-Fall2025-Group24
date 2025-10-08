package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final User user;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    
    public WelcomeLoginPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user) {
        this(db, qMgr, aMgr, user, null, null);
    }
    
    public WelcomeLoginPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user, UserManager uMgr, CommentManager cMgr) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.user = user;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
    }
    
   
    public void show( Stage primaryStage) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome, " + user.getEmail());
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
     // Button to navigate to the user's respective page based on their role
        Button continueButton = new Button("Continue to your Page");
        continueButton.setOnAction(a -> {
            String role = user.getRolesAsString();
            System.out.println("Role: " + role);

            if (user.hasRole("ADMIN")) {
                new AdminDashboardPage(db, qMgr, aMgr, user, uMgr).show(primaryStage);
            } else if (user.hasRole("STUDENT")) {
                new StudentDashboardPage(db, qMgr, aMgr, cMgr, user).show(primaryStage);
            } else {
                new Alert(Alert.AlertType.ERROR, "No valid role assigned.").showAndWait();
            }
        });

        // Only students get the Update Account button
        if (user.hasRole("STUDENT")) {
            Button updateBtn = new Button("Update Account");
            updateBtn.setOnAction(a -> new UpdateAccountPage(db, qMgr, aMgr, user).show(primaryStage));
            layout.getChildren().add(updateBtn);
        }
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	db.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    /* "Invite" button for admin to generate invitation codes
	    if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
             inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton); */

	    layout.getChildren().addAll(welcomeLabel,continueButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}