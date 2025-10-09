package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ManageUsersPage {
	private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;

    private TableView<User> userTable;

    public ManageUsersPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, UserManager uMgr, CommentManager cMgr, User user) {
    	this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
    	this.uMgr = uMgr;
        this.cMgr = cMgr;
        this.user = user;
    }
    
    public void show(Stage stage) {
        //     Access control 
        if (!user.getRoles().contains("ADMIN")) {
            new Alert(Alert.AlertType.ERROR, "Access denied: only admins can manage users.").showAndWait();
            return;
        }

        Label title = new Label("Manage Users");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //    User Table 
        userTable = new TableView<>();
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<User, String> rolesCol = new TableColumn<>("Roles");
        rolesCol.setCellValueFactory(new PropertyValueFactory<>("rolesAsString")); 
        // rolesAsString is a helper in User that joins roles with commas
        userTable.getColumns().addAll(emailCol, nameCol, rolesCol);
        userTable.setPrefHeight(300);

        refreshUsers();

        //     Controls 
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField addressField = new TextField();
        addressField.setPromptText("address");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button addUserBtn = new Button("Add User");
        addUserBtn.setOnAction(e -> {
            try {
                uMgr.addUser(emailField.getText(), passwordField.getText(), nameField.getText(), addressField.getText());
                refreshUsers();
                emailField.clear();
                passwordField.clear();
                nameField.clear();
            } catch (SQLException ex) {
                showAlert(ex.getMessage());
            }
        });

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("admin", "student", "instructor");

        Button addRoleBtn = new Button("Add Role");
        addRoleBtn.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    uMgr.addRoleToUser(selected.getId(), roleBox.getValue());
                    refreshUsers();
                } catch (Exception ex) {
                    showAlert(ex.getMessage());
                }
            }
        });

        Button removeRoleBtn = new Button("Remove Role");
        removeRoleBtn.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    uMgr.removeRoleFromUser(selected.getId(), roleBox.getValue(), user.getId());
                    refreshUsers();
                } catch (Exception ex) {
                    showAlert(ex.getMessage());
                }
            }
        });

        Button deleteUserBtn = new Button("Delete User");
        deleteUserBtn.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Show confirmation dialog
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Deletion");
                confirm.setHeaderText("Delete User");
                confirm.setContentText("Are you sure you want to delete user: " 
                                       + selected.getEmail() + "?");

                // Wait for user response
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            uMgr.deleteUser(selected.getId(), user.getId());
                            refreshUsers();
                        } catch (Exception ex) {
                            showAlert(ex.getMessage());
                        }
                    }
                });
            } else {
                showAlert("Please select a user to delete.");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new AdminDashboardPage(db, qMgr, aMgr, uMgr, cMgr, user).show(stage));

        HBox controls = new HBox(10, emailField, passwordField, nameField, addUserBtn, roleBox, addRoleBtn, removeRoleBtn, deleteUserBtn, backBtn);

        VBox root = new VBox(15, title, userTable, controls);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 900, 500));
        stage.setTitle("Manage Users");
        stage.show();
    }

    private void refreshUsers() {
        try {
            ObservableList<User> data = FXCollections.observableArrayList(uMgr.getAllUsers());
            userTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}
