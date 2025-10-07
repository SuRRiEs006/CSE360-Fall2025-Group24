package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as password, role, name, address, and email.
 */
public class User {
    private String password;
    private String role;
    private String name;
    private String address;
    private String email;
    private int id;

 // Constructor for loading from DB (with id)
    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    // Full constructor
    public User(String password, String role, String name, String address, String email) {
        this.password = password;
        this.role = role;
        this.name = name;
        this.address = address;
        this.email = email;
    }

    // Convenience constructor (e.g., for quick login objects)
    public User(String email, String password) {
        this.password = password;
        this.email = email;
    }
    
    public boolean hasRole(String role) {
        return this.role != null && this.role.equalsIgnoreCase(role);
    }

    // Getters
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public int getId() { return id; }

    // Setters
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
