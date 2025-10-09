package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as password, role, name, address, and email.
 */
public class User {
    private String password;
    private List<String> roles = new ArrayList<>();
    private String name;
    private String address;
    private String email;
    private int id;

 // Constructor for loading from DB (with id and roles)
    public User(int id, String name, String email, String password, List<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = new ArrayList<>(roles); 
    }

    // Full constructor for creating a new user with one role
    public User(String password, String role, String name, String address, String email) {
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.roles = new ArrayList<>();      
        this.roles.add(role);                
    }

    // constructor without role (DB layer assigns roles later)
    public User(String password, String name, String address, String email) {
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.roles = new ArrayList<>();      
    }
    
    // Convenience constructor (e.g., for quick login objects)
    public User(String email, String password) {
        this.password = password;
        this.email = email;
        this.roles = new ArrayList<>();
    }
    
    public void addRole(String role) {
        if (role != null && !role.isBlank()) {
            roles.add(role.toUpperCase());
        }
    }
    
    // Role Helpers
    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }
    
    public List<String> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getRolesAsString() {
        return roles == null || roles.isEmpty()
                ? ""
                : roles.stream().collect(Collectors.joining(", "));
    }

    // Getters
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public int getId() { return id; }

    // Setters
    public void setPassword(String password) { this.password = password; }
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
                ", roles=" + getRolesAsString() +
                '}';
    }
}
