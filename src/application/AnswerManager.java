package application;

import java.sql.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AnswerManager {
    private final Connection connection;

    public AnswerManager(Connection connection) {
        this.connection = connection;
    }

    // Add an answer
    public Answer addAnswer(int questionId, String answerText, String createdBy) throws SQLException {
        String sql = "INSERT INTO answers (questionId, answerText, createdBy, accepted) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, questionId);
            ps.setString(2, answerText);
            ps.setString(3, createdBy);
            ps.setBoolean(4,  false);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Answer(id, questionId, answerText, createdBy);
                }
            }
        }
        throw new SQLException("Failed to insert answer");
    }

    // Get all answers for a question
    public List<Answer> getAnswersByQuestionId(int qId) throws SQLException {
        List<Answer> result = new ArrayList<>();
        String sql = "SELECT id, questionId, answerText, createdBy, accepted FROM answers WHERE questionId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, qId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Answer(
                        rs.getInt("id"),
                        rs.getInt("questionId"),
                        rs.getString("answerText"),
                        rs.getString("createdBy"),
                        rs.getBoolean("accepted")
                    ));
                }
            }
        }
        return result;
    }

    // Get all answers (for admin view)
    public List<Answer> getAllAnswers() throws SQLException {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT id, questionId, answerText, createdBy, accepted FROM answers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Answer a = new Answer(
                    rs.getInt("id"),
                    rs.getInt("questionId"),
                    rs.getString("answerText"),
                    rs.getString("createdBy"),
                    rs.getBoolean("accepted")
                );
                answers.add(a);
            }
        }
        return answers;
    }

    // Update an answer
    public void updateAnswer(int id, String newText) throws SQLException {
        String sql = "UPDATE answers SET answerText = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newText);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
    
    public void markAcceptedAnswer(int answerId, int questionId) throws SQLException {
        //  reset all answers for this question to not accepted
        String resetSql = "UPDATE answers SET accepted = FALSE WHERE questionId = ?";
        try (PreparedStatement ps = connection.prepareStatement(resetSql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }

        // mark the chosen answer as accepted
        String acceptSql = "UPDATE answers SET accepted = TRUE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(acceptSql)) {
            ps.setInt(1, answerId);
            ps.executeUpdate();
        }
    }

    // Delete an answer
    public void deleteAnswer(int id) throws SQLException {
        String sql = "DELETE FROM answers WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
