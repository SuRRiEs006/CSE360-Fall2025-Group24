package application;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import application.*;
import databasePart1.DatabaseHelper;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class StudentDashboardUITest extends ApplicationTest {

    private DatabaseHelper db;
    private QuestionManager qMgr;
    private AnswerManager aMgr;
    private UserManager uMgr;
    private CommentManager cMgr;
    private User testStudent;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize DB + managers
        db = new DatabaseHelper();
        db.connectToDatabase();

        qMgr = new QuestionManager(db.getConnection());
        aMgr = new AnswerManager(db.getConnection());
        uMgr = new UserManager(db);
        cMgr = new CommentManager(db.getConnection());

        // Create a test student
        testStudent = uMgr.registerUser("studentUI@test.com", "password123", "UI Student");
        uMgr.assignRole(testStudent.getId(), "STUDENT");

        // Seed a question for the student to comment on
        qMgr.addQuestion("What is encapsulation?", testStudent.getId());

        // Launch the StudentDashboardPage
        new StudentDashboardPage(db, qMgr, aMgr, uMgr, cMgr, testStudent).show(stage);
    }

    @BeforeEach
    void resetDB() throws Exception {
        // Optional: clear comments/questions between tests if needed
    }

    @Test
    void testAddCommentToQuestion() {
        // Select the first question in the table
        clickOn("#questionTable");
        type(javafx.scene.input.KeyCode.DOWN); // move selection to first row

        // Type a comment
        clickOn("#commentField");
        write("This is a UI test comment");

        // Click the add comment button
        clickOn("#addCBtn");

        // Verify the comment shows up in the comments list
        verifyThat("#commentsList", hasText("This is a UI test comment"));
    }
}
