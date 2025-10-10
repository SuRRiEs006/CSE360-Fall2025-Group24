package application;

import application.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


public class QuestionManager {
    private final Connection connection;

    public QuestionManager(Connection connection) {
        this.connection = connection;
    }

    public Question addQuestion(String title, String description, String createdBy) throws SQLException {
        String sql = "INSERT INTO questions (title, description, createdBy, resolved) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, createdBy);
            ps.setBoolean(4, false);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Question(id, title, description, createdBy, false, null);
                }
            }
        }
        return null;
    }

    public List<Question> getAllQuestions() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT id, title, description, createdBy, resolved, created_at FROM QUESTIONS";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Question q = new Question(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("createdBy"),
                    rs.getBoolean("resolved"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );

                list.add(q);
            }
        }
        return list;
    }
    
    public List<Question> getQuestionsByUser(String name) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM QUESTIONS WHERE createdBy = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	list.add(new Question(
                		    rs.getInt("id"),
                		    rs.getString("title"),
                		    rs.getString("description"),
                		    rs.getString("createdBy"),
                		    rs.getBoolean("resolved"),
                		    rs.getTimestamp("created_at").toLocalDateTime()
                	));
                }
            }
        }
        return list;
    }
    
    public int getUnreadAnswerCount(int questionId, String userName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ANSWERS WHERE question_id = ? AND is_read = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Question> getResolvedQuestionsByUser(String username) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE createdBy = ? AND resolved = TRUE ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }
    
    public List<Question> getUnresolvedQuestionsByUser(String username) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE createdBy = ? AND resolved = FALSE ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
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
        String sql = "SELECT id, title, description, createdBy, resolved, created_at FROM questions WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Question(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("createdBy"),
                        rs.getBoolean("resolved"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }
    
    //     Mark a question as resolved 
    public void markQuestionResolved(int questionId) throws SQLException {
        String sql = "UPDATE questions SET resolved = TRUE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }
    }

    //     Mark a question as unresolved 
    public void markQuestionUnresolved(int questionId) throws SQLException {
        String sql = "UPDATE questions SET resolved = FALSE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }
    }

    //     Helper to map DB row to Question object 
    private Question mapRow(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("createdBy"),
            rs.getBoolean("resolved"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}