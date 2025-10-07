package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class CommentManager {
	private final Connection connection;

    public CommentManager(Connection connection) {
        this.connection = connection;
    }

    public Comment addCommentToQuestion(int questionId, String text, String createdBy) throws SQLException {
        String sql = "INSERT INTO comments (questionId, text, createdBy) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, questionId);
            ps.setString(2, text);
            ps.setString(3, createdBy);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Comment(rs.getInt(1), questionId, null, text, createdBy);
                }
            }
        }
        throw new SQLException("Failed to insert comment");
    }

    public Comment addCommentToAnswer(int answerId, String text, String createdBy) throws SQLException {
        String sql = "INSERT INTO comments (answerId, text, createdBy) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, answerId);
            ps.setString(2, text);
            ps.setString(3, createdBy);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Comment(rs.getInt(1), null, answerId, text, createdBy);
                }
            }
        }
        throw new SQLException("Failed to insert comment");
    }

    public List<Comment> getCommentsForQuestion(int questionId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE questionId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("questionId"),
                        null,
                        rs.getString("text"),
                        rs.getString("createdBy")
                    ));
                }
            }
        }
        return list;
    }

    public List<Comment> getCommentsForAnswer(int answerId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE answerId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                        rs.getInt("id"),
                        null,
                        rs.getInt("answerId"),
                        rs.getString("text"),
                        rs.getString("createdBy")
                    ));
                }
            }
        }
        return list;
    }
    
    public void deleteComment(int id) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

