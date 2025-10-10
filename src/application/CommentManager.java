package application;

import databasePart1.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class CommentManager {
    private final Connection connection;

    public CommentManager(Connection connection) {
        this.connection = connection;
    }

    public CommentManager(DatabaseHelper db) {
        this.connection = db.getConnection();
    }

    //  Add root comment to a question
    public Comment addCommentToQuestion(int questionId, String text, String createdBy) throws SQLException {
        String sql = "INSERT INTO comments (questionId, text, createdBy, parentCommentId) VALUES (?, ?, ?, NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, questionId);
            ps.setString(2, text);
            ps.setString(3, createdBy);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Comment(rs.getInt(1), questionId, null, text, createdBy, null);
                }
            }
        }
        throw new SQLException("Failed to insert comment");
    }

    //  Add root comment to an answer 
    public Comment addCommentToAnswer(int answerId, String text, String createdBy) throws SQLException {
        String sql = "INSERT INTO comments (answerId, text, createdBy, parentCommentId) VALUES (?, ?, ?, NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, answerId);
            ps.setString(2, text);
            ps.setString(3, createdBy);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Comment(rs.getInt(1), null, answerId, text, createdBy, null);
                }
            }
        }
        throw new SQLException("Failed to insert comment");
    }

    //  Get root comments for a question 
    public List<Comment> getRootCommentsForQuestion(int questionId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE questionId = ? AND parentCommentId IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("questionId"),
                        (Integer) rs.getObject("answerId"),
                        rs.getString("text"),
                        rs.getString("createdBy"),
                        (Integer) rs.getObject("parentCommentId")
                    ));
                }
            }
        }
        return list;
    }

    // Get root comments for an answer 
    public List<Comment> getRootCommentsForAnswer(int answerId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE answerId = ? AND parentCommentId IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                        rs.getInt("id"),
                        (Integer) rs.getObject("questionId"),
                        rs.getInt("answerId"),
                        rs.getString("text"),
                        rs.getString("createdBy"),
                        (Integer) rs.getObject("parentCommentId")
                    ));
                }
            }
        }
        return list;
    }

    //  Add a reply to an existing comment 
    public Comment addReplyToComment(int parentCommentId, String text, String createdBy) throws SQLException {
        // Look up parent to inherit questionId/answerId
        String lookup = "SELECT questionId, answerId FROM comments WHERE id = ?";
        Integer qId = null, aId = null;
        try (PreparedStatement ps = connection.prepareStatement(lookup)) {
            ps.setInt(1, parentCommentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    qId = (Integer) rs.getObject("questionId");
                    aId = (Integer) rs.getObject("answerId");
                }
            }
        }

        String sql = "INSERT INTO comments (questionId, answerId, text, createdBy, parentCommentId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (qId != null) ps.setInt(1, qId); else ps.setNull(1, Types.INTEGER);
            if (aId != null) ps.setInt(2, aId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, text);
            ps.setString(4, createdBy);
            ps.setInt(5, parentCommentId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Comment(rs.getInt(1), qId, aId, text, createdBy, parentCommentId);
                }
            }
        }
        throw new SQLException("Failed to insert reply");
    }

    //  Get replies for a comment 
    public List<Comment> getReplies(int parentCommentId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE parentCommentId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parentCommentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                        rs.getInt("id"),
                        (Integer) rs.getObject("questionId"),
                        (Integer) rs.getObject("answerId"),
                        rs.getString("text"),
                        rs.getString("createdBy"),
                        (Integer) rs.getObject("parentCommentId")
                    ));
                }
            }
        }
        return list;
    }

    //Update comment text ---
    public void updateComment(int id, String newText) throws SQLException {
        String sql = "UPDATE comments SET text = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newText);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    //  Delete a comment 
    public void deleteComment(int id) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

