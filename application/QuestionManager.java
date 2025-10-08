package application;

import application.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionManager {
    private final Connection connection;

    public QuestionManager(Connection connection) {
        this.connection = connection;
    }

    public Question addQuestion(String title, String description, String createdBy) throws SQLException {
        String sql = "INSERT INTO questions (title, description, createdBy) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, createdBy);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Question(id, title, description, createdBy);
                }
            }
        }
        return null;
    }

    public List<Question> getAllQuestions() throws SQLException {
        List<Question> result = new ArrayList<>();
        String sql = "SELECT id, title, description, createdBy FROM questions ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Question(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("createdBy")
                ));
            }
        }
        return result;
    }
    

    public void updateQuestion(int id, String newTitle, String newDescription) throws SQLException {
        String sql = "UPDATE questions SET title = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newTitle);
            ps.setString(2, newDescription);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteQuestion(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public Question getQuestionById(int id) throws SQLException {
        String sql = "SELECT id, title, description, createdBy FROM questions WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Question(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("createdBy")
                    );
                }
            }
        }
        return null;
    }
}