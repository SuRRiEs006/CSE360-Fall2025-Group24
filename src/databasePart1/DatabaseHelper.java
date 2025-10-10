package databasePart1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/FoundationDatabase";
    private static final String USER = "sa";
    private static final String PASS = "";

    private Connection connection;

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            initSchema();
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // create USERS and USER_ROLES, leave questions/answers to managers
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "email VARCHAR(255) UNIQUE, " +
                         "password VARCHAR(255), " +
                         "name VARCHAR(255), " +
                         "address VARCHAR(255))");

            stmt.execute("CREATE TABLE IF NOT EXISTS USER_ROLES (" +
                         "user_id INT, " +
                         "role VARCHAR(50), " +
                         "PRIMARY KEY (user_id, role), " +
                         "FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE)");
        }
    }
	
		/* Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	}
  */

	
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
	    try {
	        if (connection != null && !connection.isClosed()) {
	            connection.close();
	        }
	    } catch (SQLException se) {
	        se.printStackTrace();
	    }
	}

}
