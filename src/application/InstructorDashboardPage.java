package application;

import databasePart1.DatabaseHelper;
import javafx.stage.Stage;

public class InstructorDashboardPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;

    public InstructorDashboardPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, UserManager uMgr, CommentManager cMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
        this.user = user;
    }

    public void show(Stage stage) {
        // Reuse the existing QuestionManagementPage as the instructor dashboard
        new QuestionManagementPage(db, qMgr, aMgr, user, uMgr, cMgr).show(stage);
    }
}
