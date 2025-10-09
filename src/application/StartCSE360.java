package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import databasePart1.DatabaseHelper;


public class StartCSE360 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseHelper db = new DatabaseHelper();
            db.connectToDatabase();

            // Managers that exist before login
            QuestionManager qMgr = new QuestionManager(db.getConnection());
            AnswerManager aMgr = new AnswerManager(db.getConnection());
            UserManager uMgr = new UserManager(db);
            CommentManager cMgr = new CommentManager(db);

            // Show the login/role selection page first
            new SetupLoginSelectionPage(db, qMgr, aMgr, uMgr, cMgr).show(primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
