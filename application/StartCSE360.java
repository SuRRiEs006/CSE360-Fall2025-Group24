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
            // 2. Create managers that share the same connection
            QuestionManager qMgr = new QuestionManager(db.getConnection());
            AnswerManager aMgr = new AnswerManager(db.getConnection());

            // 3. Decide which page to show first
            new SetupLoginSelectionPage(db, qMgr, aMgr).show(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
