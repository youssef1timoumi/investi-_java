package edu.connexion3a8.controllers;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.User;
import edu.connexion3a8.services.UserAuthService;
import edu.connexion3a8.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Desktop profile page — matches the web version's "My Profile" layout:
 * identity card, stats (points / level / role), editable personal info,
 * and a security section for password changes. Uses the same services as
 * the rest of the app so the DB stays the source of truth.
 */
public class ProfileController implements Initializable {

    @FXML private Label avatarInitial;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleBadge;
    @FXML private Label statusBadge;

    @FXML private Label statPoints;
    @FXML private Label statLevel;
    @FXML private Label statRole;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextArea bioField;
    @FXML private Label savedLabel;

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordMessage;

    private final UserService userService = new UserService();
    private User user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        user = InvestiApp.getCurrentUser();
        if (user == null) return;
        populate(user);
    }

    private void populate(User u) {
        String name = u.getName() == null ? "User" : u.getName();
        nameLabel.setText(name);
        emailLabel.setText(u.getEmail());
        avatarInitial.setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase());

        String role = u.getRole() == null ? "user" : u.getRole();
        roleBadge.setText(capitalize(role));
        statRole.setText(capitalize(role));
        statusBadge.setText(u.isActive() ? "Active" : "Pending");
        if (!u.isActive()) {
            statusBadge.setStyle("-fx-background-color: #fff7ed; -fx-text-fill: #b45309; -fx-padding: 4 12; -fx-background-radius: 9999; -fx-font-size: 11px; -fx-font-weight: 700;");
        }

        nameField.setText(name);
        emailField.setText(u.getEmail());
        bioField.setText(u.getBio() == null ? "" : u.getBio());

        statPoints.setText(String.valueOf(u.getPoints()));
        statLevel.setText(String.valueOf(u.getLevel()));
    }

    @FXML
    private void handleSave() {
        if (user == null) return;
        String newName = nameField.getText() == null ? "" : nameField.getText().trim();
        String newBio = bioField.getText();
        if (newName.isEmpty()) {
            savedLabel.setStyle("-fx-text-fill: #b91c1c; -fx-font-weight: 600;");
            savedLabel.setText("Name cannot be empty.");
            return;
        }
        user.setName(newName);
        user.setBio(newBio);
        try {
            userService.updateUser(user.getId(), user);
            InvestiApp.setCurrentUser(user);
            savedLabel.setStyle("-fx-text-fill: #047857; -fx-font-weight: 600;");
            savedLabel.setText("Profile saved");
            nameLabel.setText(newName);
            avatarInitial.setText(newName.isEmpty() ? "?" : String.valueOf(newName.charAt(0)).toUpperCase());
        } catch (SQLException e) {
            savedLabel.setStyle("-fx-text-fill: #b91c1c; -fx-font-weight: 600;");
            savedLabel.setText("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        if (user != null) populate(user);
        savedLabel.setText("");
    }

    @FXML
    private void handlePasswordChange() {
        if (user == null) return;
        String pwd = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        if (pwd == null || pwd.length() < 6) {
            passwordMessage.setStyle("-fx-text-fill: #b91c1c;");
            passwordMessage.setText("Password must be at least 6 characters.");
            return;
        }
        if (!pwd.equals(confirm)) {
            passwordMessage.setStyle("-fx-text-fill: #b91c1c;");
            passwordMessage.setText("Passwords do not match.");
            return;
        }
        try {
            String hashed = UserAuthService.hashPassword(pwd);
            UserAuthService.updateStoredHash(user.getId(), hashed);
            user.setPasswordHash(hashed);
            passwordMessage.setStyle("-fx-text-fill: #047857;");
            passwordMessage.setText("Password updated");
            newPasswordField.clear();
            confirmPasswordField.clear();
        } catch (Exception e) {
            passwordMessage.setStyle("-fx-text-fill: #b91c1c;");
            passwordMessage.setText("Failed: " + e.getMessage());
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
