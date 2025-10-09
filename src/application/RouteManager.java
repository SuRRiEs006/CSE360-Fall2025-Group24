package application;

import javafx.stage.Stage;
import databasePart1.*;

public class RouteManager {

    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;

    public RouteManager(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, UserManager uMgr, CommentManager cMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
        this.user = user;
    }
    
    // Show the correct dashboard for the given user based on their role(s).
    public void showDashboardFor(User user, String selectedRole, Stage stage) {
        if (user == null || selectedRole == null) {
            throw new IllegalArgumentException("User and role must not be null");
        }

        // Verify the user actually has this role
        if (!user.hasRole(selectedRole)) {
            throw new IllegalStateException(
                "User " + user.getEmail() + " does not have role: " + selectedRole
            );
        }

        if ("ADMIN".equalsIgnoreCase(selectedRole)) {
            new AdminDashboardPage(db, qMgr, aMgr, uMgr, cMgr, user).show(stage);
        } else if ("STUDENT".equalsIgnoreCase(selectedRole)) {
            new StudentDashboardPage(db, qMgr, aMgr, uMgr, cMgr,user).show(stage);
        } else if ("TEACHER".equalsIgnoreCase(selectedRole)) {
            new InstructorDashboardPage(db, qMgr, aMgr, uMgr, cMgr, user).show(stage);
        } else {
            throw new IllegalStateException("Unsupported role: " + selectedRole);
        }
    }
}
