package application;

import databasePart1.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    private final Connection connection;

    public UserManager(DatabaseHelper db) {
        this.connection = db.getConnection();
    }

    //    Register a new user with default STUDENT role
    public void registerUser(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO CSE360USERS (name, email, password, role) VALUES (?, ?, ?, 'STUDENT')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
        }
    }

    //  fetch a user by email for login
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM CSE360USERS WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }
}
