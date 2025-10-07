package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import application.User;
import application.Question;
import application.Answer;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	
	//	PreparedStatement pstmt
	public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            createTables();
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }
	
	try (Statement statement = connection.createStatement()) {
	    statement.execute("DROP TABLE IF EXISTS cse360users");

	    String userTable = "CREATE TABLE cse360users (" +
	            "id INT AUTO_INCREMENT PRIMARY KEY, " +
	            "password VARCHAR(255), " +
	            "role VARCHAR(20), " +
	            "name VARCHAR(255), " +
	            "address VARCHAR(255), " +
	            "email VARCHAR(255) UNIQUE)";
	    statement.execute(userTable);
		}
	}


	// Create table with new fields
	public void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" +
		        "id INT AUTO_INCREMENT PRIMARY KEY, " +
		        "password VARCHAR(255), " +
		        "role VARCHAR(20), " +
		        "name VARCHAR(255), " +
		        "address VARCHAR(255), " +
		        "email VARCHAR(255) UNIQUE)";
		statement.execute(userTable);
		
		String questionTable = "CREATE TABLE IF NOT EXISTS Questions (" +
		        "id INT AUTO_INCREMENT PRIMARY KEY, " +
		        "title VARCHAR(255), " +
		        "description VARCHAR(500))";
		statement.execute(questionTable);
		
		String answerTable = "CREATE TABLE IF NOT EXISTS Answers (" +
		        "id INT AUTO_INCREMENT PRIMARY KEY, " +
		        "questionId INT, " +
		        "text VARCHAR(500), " +
		        "FOREIGN KEY (questionId) REFERENCES Questions(id) ON DELETE CASCADE)";
		statement.execute(answerTable);
	}	
		/* Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	}
  */

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void registerFull(User user) throws SQLException {
	    // Check if this is the first account
	    String countQuery = "SELECT COUNT(*) FROM cse360users";
	    int count = 0;
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(countQuery)) {
	        if (rs.next()) {
	            count = rs.getInt(1);
	        }
	    }

	    // Assign role
	    if (count == 0) {
	        user.setRole("ADMIN");   // first user is admin
	    } else {
	        user.setRole("STUDENT"); // everyone else is student
	    }

	    // Now actually insert into the table
	    String sql = "INSERT INTO cse360users (name, address, email, password, role) VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, user.getName());
	        ps.setString(2, user.getAddress());
	        ps.setString(3, user.getEmail());
	        ps.setString(4, user.getPassword());
	        ps.setString(5, user.getRole());
	        ps.executeUpdate();
	    }
	}
	
	public int getUserCount() throws SQLException {
	    String sql = "SELECT COUNT(*) FROM CSE360USERS";
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    }
	    return 0;
	}

	// Validates a user has login credentials.
	public User login(String email, String password, String role) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, password);
	        pstmt.setString(3, role);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return new User(
	                    rs.getString("password"),
	                    rs.getString("role"),
	                    rs.getString("name"),
	                    rs.getString("address"),
	                    rs.getString("email")
	                );
	            }
	        }
	    }
	    return null; // login failed
	}
	
	public User login(User user) throws SQLException {
	    return login(user.getEmail(), user.getPassword(), user.getRole());
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String email) {
	    String query = "SELECT role FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	public void updateUserRole(String email, String newRole) throws SQLException {
	    String sql = "UPDATE cse360users SET role = ? WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newRole);
	        pstmt.setString(2, email);
	        pstmt.executeUpdate();
	    }
	}
	
	// Verify that the provided password matches the stored one
	public boolean verifyPassword(String email, String password) throws SQLException {
	    String sql = "SELECT password FROM CSE360USERS WHERE email = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, email);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                String stored = rs.getString("password");
	                return stored.equals(password); // 🔑 if you add hashing, replace with hash check
	            }
	        }
	    }
	    return false;
	}

	// Update name, address, and password for a user
	public void updateUser(String email, String newName, String newAddress, String newPassword) throws SQLException {
	    String sql = "UPDATE CSE360USERS SET name = ?, address = ?, password = ? WHERE email = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, newName);
	        ps.setString(2, newAddress);
	        ps.setString(3, newPassword);
	        ps.setString(4, email);
	        ps.executeUpdate();
	    }
	}
	
	// Fetch user by email
	public User getUserByEmail(String email) {
	    String sql = "SELECT * FROM CSE360USERS WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return new User(
	                rs.getString("password"),
	                rs.getString("role"),
	                rs.getString("name"),
	                rs.getString("address"),
	                rs.getString("email")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	// Update profile (email locked)
	public void updateUserProfile(String email, String name, String address, String newPassword) throws SQLException {
	    String sql = "UPDATE CSE360USERS SET name=?, address=?, password=? WHERE email=?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, name);
	        pstmt.setString(2, address);
	        pstmt.setString(3, newPassword);
	        pstmt.setString(4, email);
	        pstmt.executeUpdate();
	    }
	}
	
	public List<User> getPendingUsers() throws SQLException {
	    List<User> pending = new ArrayList<>();
	    String sql = "SELECT * FROM cse360users WHERE role IS NULL";
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	            User u = new User(
	                rs.getString("password"),
	                rs.getString("role"),
	                rs.getString("name"),
	                rs.getString("address"),
	                rs.getString("email")
	            );
	            pending.add(u);
	        }
	    }
	    return pending;
	}
	
	public void addAnswer(int questionId, String answerText, String createdBy) throws SQLException {
	    String sql = "INSERT INTO answers (question_id, answer_text, createdBy) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, answerText);
	        pstmt.setString(3, createdBy);
	        pstmt.executeUpdate();
	    }
	}
	
	public void addQuestion(String title, String desc, String createdBy) throws SQLException {
	    String sql = "INSERT INTO questions (title, description, createdBy) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, title);
	        pstmt.setString(2, desc);
	        pstmt.setString(3, createdBy);
	        pstmt.executeUpdate();
	    }
	}

	public List<Question> getAllQuestions() throws SQLException {
	    List<Question> list = new ArrayList<>();
	    String sql = "SELECT * FROM questions";
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	            list.add(new Question(
	                rs.getInt("id"),
	                rs.getString("title"),
	                rs.getString("description"),
	                rs.getString("createdBy")
	            ));
	        }
	    }
	    return list;
	}

	public void updateQuestion(int id, String newTitle, String newDesc) throws SQLException {
	    String sql = "UPDATE questions SET title = ?, description = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newTitle);
	        pstmt.setString(2, newDesc);
	        pstmt.setInt(3, id);
	        pstmt.executeUpdate();
	    }
	}

	public void deleteQuestion(int id) throws SQLException {
	    String sql = "DELETE FROM questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        pstmt.executeUpdate();
	    }
	}
	
	public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
	    List<Answer> list = new ArrayList<>();
	    String sql = "SELECT * FROM answers WHERE question_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            list.add(new Answer(
	                rs.getInt("id"),
	                rs.getInt("question_id"),
	                rs.getString("answer_text"),
	                rs.getString("createdBy")
	            ));
	        }
	    }
	    return list;
	}

	public void updateAnswer(int id, String newText) throws SQLException {
	    String sql = "UPDATE answers SET answer_text = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newText);
	        pstmt.setInt(2, id);
	        pstmt.executeUpdate();
	    }
	}

	public void deleteAnswer(int id) throws SQLException {
	    String sql = "DELETE FROM answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        pstmt.executeUpdate();
	    }
	}
	/* Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	} */
	
	public Connection getConnection() {
	    return connection;
	}
	
	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
