package application;

import databasePart1.DatabaseHelper;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class UserManager {
    private final Connection connection;

    public UserManager(DatabaseHelper db) {
        this.connection = db.getConnection();
    }

    //    Register a new user with default STUDENT role
    public void addUser(String email, String password, String name, String address) throws SQLException {
        String sql = "INSERT INTO USERS (email, password, name, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, address);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    int count = getUserCount();
                    if (count == 1) {
                        addRoleToUser(userId, "ADMIN");
                    } else {
                        addRoleToUser(userId, "STUDENT");
                    }
                }
            }
        }
    }
    
    public int getUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM USERS";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    // for Admin use
    public void updateUserAccount(int userId, String name, String email, String password, String address) throws SQLException {
        String sql = "UPDATE USERS SET name = ?, email = ?, password = ?, address = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password); 
            ps.setString(4, address);
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
    }
    
    // for all other user's
    public void updateUserProfile(String email, String name, String address, String newPassword) throws SQLException {
        String sql = "UPDATE USERS SET name = ?, address = ?, password = ? WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, newPassword);
            ps.setString(4, email);
            ps.executeUpdate();
        }
    }
    
    // Fetch all users with their roles.
    public List<User> getAllUsers() throws SQLException {
        String sql = """
            SELECT u.id, u.name, u.email, u.password, u.address, ur.role
            FROM USERS u
            LEFT JOIN USER_ROLES ur ON u.id = ur.user_id
            ORDER BY u.id
        """;

        Map<Integer, User> userMap = new LinkedHashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");

                // If we haven't seen this user yet, create a new User
                User user = userMap.get(id);
                if (user == null) {
                    user = new User(
                        id,
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        new ArrayList<>() // roles will be filled below
                    );
                    user.setAddress(rs.getString("address"));
                    userMap.put(id, user);
                }

                // Add role if present
                String role = rs.getString("role");
                if (role != null) {
                    user.addRole(role); 
                }
            }
        }

        return new ArrayList<>(userMap.values());
    }
    
    public User getUserByEmail(String email) throws SQLException {
        String sql = """
            SELECT u.id, u.name, u.email, u.password, u.address, ur.role
            FROM USERS u
            LEFT JOIN USER_ROLES ur ON u.id = ur.user_id
            WHERE u.email = ?
        """;

        User user = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (user == null) {
                        user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            new ArrayList<>()
                        );
                        user.setAddress(rs.getString("address"));
                    }
                    String role = rs.getString("role");
                    if (role != null) {
                        user.addRole(role.toUpperCase());
                    }
                }
            }
        }
        return user;
    }

    public void addRoleToUser(int userId, String role) throws SQLException {
    	String normalized = role.toUpperCase();
    	try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setString(2, normalized);
            ps.executeUpdate();
        }
    }
    
    public void removeRoleFromUser(int userId, String role, int currentAdminId) throws SQLException {
        String normalized = role.toUpperCase();

        if ("ADMIN".equals(normalized)) {
            if (userId == currentAdminId) {
                throw new IllegalStateException("You cannot remove your own admin role.");
            }
            if (!isLastAdmin(userId)) {
                throw new IllegalStateException("There must always be at least one admin.");
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM USER_ROLES WHERE user_id = ? AND role = ?")) {
            ps.setInt(1, userId);
            ps.setString(2, normalized);
            ps.executeUpdate();
        }
    }
    
    public User login(String email, String password) throws SQLException {
        String sql = """
            SELECT u.id, u.name, u.email, u.password, u.address, ur.role
            FROM USERS u
            LEFT JOIN USER_ROLES ur ON u.id = ur.user_id
            WHERE u.email = ?
        """;

        User user = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (user == null) {
                        String storedPassword = rs.getString("password");
                        if (!storedPassword.equals(password)) {
                            return null; // invalid password
                        }
                        user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            storedPassword,
                            new ArrayList<>()
                        );
                        user.setAddress(rs.getString("address"));
                    }
                    String role = rs.getString("role");
                    if (role != null) {
                        user.addRole(role.toUpperCase());
                    }
                }
            }
        }
        return user;
    }
    
    // Checks if the given user is the last admin in the system.
    public boolean isLastAdmin(int userId) throws SQLException {
        
        String sql = "SELECT COUNT(*) FROM USER_ROLES WHERE role = 'admin'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int totalAdmins = rs.getInt(1);
                if (totalAdmins == 0) {
                    return false; 
                }
                if (totalAdmins == 1) {
                    // Check if that one admin is the given user
                    String sql2 = "SELECT COUNT(*) FROM USER_ROLES WHERE role = 'admin' AND user_id = ?";
                    try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                        ps2.setInt(1, userId);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            return rs2.next() && rs2.getInt(1) == 1;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void deleteUser(int userId, int currentAdminId) throws SQLException {
        // Prevent self-deletion
        if (userId == currentAdminId) {
            throw new IllegalStateException("You cannot delete your own account.");
        }

        // Prevent deleting the last admin
        if (isLastAdmin(userId)) {
            throw new IllegalStateException("Cannot delete the last admin.");
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM USER_ROLES WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM USERS WHERE id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
    
    public boolean verifyPassword(String email, String password) throws SQLException {
        String sql = "SELECT password FROM USERS WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password").equals(password);
                }
            }
        }
        return false;
    }
}
