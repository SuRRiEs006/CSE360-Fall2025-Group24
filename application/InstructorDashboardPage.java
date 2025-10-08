package application;

import databasePart1.DatabaseHelper;
import javafx.stage.Stage;

public class InstructorDashboardPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final User user;
    private final UserManager uMgr;

    public InstructorDashboardPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user, UserManager uMgr) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.user = user;
        this.uMgr = uMgr;
    }

    public void show(Stage stage) {
        // Reuse the existing QuestionManagementPage as the instructor dashboard
        new QuestionManagementPage(db, qMgr, aMgr, user, uMgr).show(stage);
    }
}
