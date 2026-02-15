package edu.connexion3a8.controllers;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.User;
import edu.connexion3a8.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {
    
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginMessage;
    @FXML private Button loginButton;
    @FXML private Button loginTabBtn;
    @FXML private VBox loginForm;
    
    @FXML private TextField registerName;
    @FXML private TextField registerEmail;
    @FXML private PasswordField registerPassword;
    @FXML private Label registerMessage;
    @FXML private Button registerButton;
    @FXML private Button registerTabBtn;
    @FXML private VBox registerForm;
    @FXML private ToggleButton innovatorBtn;
    @FXML private ToggleButton investorBtn;
    
    private UserService userService = new UserService();
    private ToggleGroup roleGroup;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Name validation pattern (letters, spaces, hyphens only)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÿ\\s'-]{2,50}$"
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create toggle group for role selection (only one can be selected)
        roleGroup = new ToggleGroup();
        innovatorBtn.setToggleGroup(roleGroup);
        investorBtn.setToggleGroup(roleGroup);
        
        // Select innovator by default
        innovatorBtn.setSelected(true);
        
        // Style toggle buttons when selected
        innovatorBtn.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                innovatorBtn.setStyle("-fx-background-color: #456990; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 20; -fx-cursor: hand; -fx-border-width: 0;");
            } else {
                innovatorBtn.setStyle("-fx-background-color: #F7F0F5; -fx-text-fill: #333; -fx-font-size: 13px; -fx-background-radius: 10; -fx-padding: 12 20; -fx-cursor: hand; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1;");
            }
        });
        
        investorBtn.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                investorBtn.setStyle("-fx-background-color: #9B7E46; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 20; -fx-cursor: hand; -fx-border-width: 0;");
            } else {
                investorBtn.setStyle("-fx-background-color: #F7F0F5; -fx-text-fill: #333; -fx-font-size: 13px; -fx-background-radius: 10; -fx-padding: 12 20; -fx-cursor: hand; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1;");
            }
        });
        
        // Add real-time validation for register fields
        registerName.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !NAME_PATTERN.matcher(newVal).matches()) {
                registerName.setStyle("-fx-background-color: #FFE5E5; -fx-background-radius: 10; -fx-padding: 14; -fx-font-size: 14px; -fx-border-color: #A62639; -fx-border-radius: 10; -fx-border-width: 2;");
            } else {
                registerName.setStyle("-fx-background-color: #F7F0F5; -fx-background-radius: 10; -fx-padding: 14; -fx-font-size: 14px; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1;");
            }
        });
        
        registerEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !EMAIL_PATTERN.matcher(newVal).matches()) {
                registerEmail.setStyle("-fx-background-color: #FFE5E5; -fx-background-radius: 10; -fx-padding: 14; -fx-font-size: 14px; -fx-border-color: #A62639; -fx-border-radius: 10; -fx-border-width: 2;");
            } else {
                registerEmail.setStyle("-fx-background-color: #F7F0F5; -fx-background-radius: 10; -fx-padding: 14; -fx-font-size: 14px; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1;");
            }
        });
        
        // Trigger initial styling
        innovatorBtn.setSelected(true);
    }
    
    @FXML
    private void switchToLogin() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        registerForm.setVisible(false);
        registerForm.setManaged(false);
        
        loginTabBtn.setStyle("-fx-background-color: #456990; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8 0 0 8; -fx-padding: 12 40; -fx-cursor: hand; -fx-border-width: 0;");
        registerTabBtn.setStyle("-fx-background-color: #E8E8E8; -fx-text-fill: #666; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 0 8 8 0; -fx-padding: 12 40; -fx-cursor: hand; -fx-border-width: 0;");
        
        clearMessages();
    }
    
    @FXML
    private void switchToRegister() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        registerForm.setVisible(true);
        registerForm.setManaged(true);
        
        loginTabBtn.setStyle("-fx-background-color: #E8E8E8; -fx-text-fill: #666; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8 0 0 8; -fx-padding: 12 40; -fx-cursor: hand; -fx-border-width: 0;");
        registerTabBtn.setStyle("-fx-background-color: #9B7E46; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 0 8 8 0; -fx-padding: 12 40; -fx-cursor: hand; -fx-border-width: 0;");
        
        clearMessages();
    }
    
    private void clearMessages() {
        loginMessage.setText("");
        registerMessage.setText("");
    }
    
    @FXML
    private void handleLogin() {
        String email = loginEmail.getText().trim();
        String password = loginPassword.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            loginMessage.setText("⚠ Please fill in all fields");
            return;
        }
        
        try {
            User user = userService.getUserByEmail(email);
            
            if (user == null) {
                loginMessage.setText("⚠ User not found");
                return;
            }
            
            if (!user.getPasswordHash().equals(password)) {
                loginMessage.setText("⚠ Incorrect password");
                return;
            }
            
            // Set current user in app
            InvestiApp.setCurrentUser(user);
            
            if ("admin".equals(user.getRole())) {
                InvestiApp.showAdminDashboard();
            } else {
                InvestiApp.showHomePage();
            }
            
        } catch (Exception e) {
            loginMessage.setText("⚠ Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRegister() {
        String name = registerName.getText().trim();
        String email = registerEmail.getText().trim();
        String password = registerPassword.getText();
        
        // Validate all fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            registerMessage.setText("⚠ Please fill in all fields");
            return;
        }
        
        // Validate name format
        if (!NAME_PATTERN.matcher(name).matches()) {
            registerMessage.setText("⚠ Name must contain only letters (2-50 characters)");
            return;
        }
        
        // Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            registerMessage.setText("⚠ Please enter a valid email address");
            return;
        }
        
        // Validate password length
        if (password.length() < 6) {
            registerMessage.setText("⚠ Password must be at least 6 characters");
            return;
        }
        
        // Check if a role is selected
        if (roleGroup.getSelectedToggle() == null) {
            registerMessage.setText("⚠ Please select your role (Innovator or Investor)");
            return;
        }
        
        // Determine selected role
        String role = innovatorBtn.isSelected() ? "innovator" : "investor";
        
        try {
            User existingUser = userService.getUserByEmail(email);
            if (existingUser != null) {
                registerMessage.setText("⚠ Email already registered");
                return;
            }
            
            User newUser = new User(email, password, name, role);
            newUser.setBio("New " + role + " on INVESTI");
            
            userService.addUser(newUser);
            
            registerMessage.setStyle("-fx-text-fill: #456990; -fx-font-size: 12px; -fx-font-weight: 600;");
            registerMessage.setText("✓ Account created as " + role + "! Redirecting to login...");
            
            registerName.clear();
            registerEmail.clear();
            registerPassword.clear();
            innovatorBtn.setSelected(true);
            
            // Auto-switch to login after 1.5 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::switchToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (SQLException e) {
            registerMessage.setText("⚠ Registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
