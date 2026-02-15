package edu.connexion3a8.controllers;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.User;
import edu.connexion3a8.services.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AdminDashboardController implements Initializable {
    
    private UserService userService = new UserService();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;
    private User selectedUser = null;
    
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Integer> colPoints;
    @FXML private TableColumn<User, Integer> colLevel;
    @FXML private TableColumn<User, String> colActions;
    
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField roleField;
    @FXML private TextArea bioField;
    @FXML private TextField searchField;
    @FXML private Label emptyStateLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    @FXML private Button btnAdd;
    @FXML private Button btnRefresh;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÿ\\s'-]{2,50}$"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupValidation();
        loadData();
        setupSearch();
    }
    
    private void setupValidation() {
        // Real-time email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !EMAIL_PATTERN.matcher(newVal).matches()) {
                emailField.getStyleClass().add("input-error");
            } else {
                emailField.getStyleClass().remove("input-error");
            }
        });
        
        // Real-time name validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !NAME_PATTERN.matcher(newVal).matches()) {
                nameField.getStyleClass().add("input-error");
            } else {
                nameField.getStyleClass().remove("input-error");
            }
        });
        
        // Password strength indicator
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 0 && newVal.length() < 6) {
                passwordField.getStyleClass().add("input-error");
            } else {
                passwordField.getStyleClass().remove("input-error");
            }
        });
    }
    
    private void setupSearch() {
        if (searchField != null) {
            filteredData = new FilteredList<>(userList, p -> true);
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(user -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    
                    String lowerCaseFilter = newValue.toLowerCase();
                    
                    if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (user.getRole().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            
            userTable.setItems(filteredData);
        }
    }
    
    private void loadData() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        
        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory = 
            (TableColumn<User, String> param) -> {
            final TableCell<User, String> cell = new TableCell<User, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Button deleteBtn = new Button("🗑 Delete");
                        Button editBtn = new Button("✏ Edit");
                        
                        deleteBtn.getStyleClass().addAll("modern-button", "button-danger");
                        deleteBtn.setStyle("-fx-padding: 8 16; -fx-font-size: 12px;");
                        
                        editBtn.getStyleClass().addAll("modern-button", "button-primary");
                        editBtn.setStyle("-fx-padding: 8 16; -fx-font-size: 12px;");
                        
                        deleteBtn.setOnMouseClicked((MouseEvent event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            
                            // Confirmation dialog
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                            confirm.setTitle("Confirm Delete");
                            confirm.setHeaderText("Delete User");
                            confirm.setContentText("Are you sure you want to delete " + user.getName() + "?\nThis action cannot be undone.");
                            
                            ButtonType btnYes = new ButtonType("Yes, Delete", ButtonBar.ButtonData.OK_DONE);
                            ButtonType btnNo = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            confirm.getButtonTypes().setAll(btnYes, btnNo);
                            
                            confirm.showAndWait().ifPresent(response -> {
                                if (response == btnYes) {
                                    try {
                                        userService.deleteUser(user.getId());
                                        refreshTable();
                                        clearFields();
                                        selectedUser = null;
                                        showSuccessAlert("User deleted successfully!");
                                    } catch (SQLException ex) {
                                        Logger.getLogger(AdminDashboardController.class.getName()).log(Level.SEVERE, null, ex);
                                        showErrorAlert("Error deleting user: " + ex.getMessage());
                                    }
                                }
                            });
                        });
                        
                        editBtn.setOnMouseClicked((MouseEvent event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            selectedUser = user;
                            setTextFields(user);
                            btnAdd.setText("💾 Update User");
                        });
                        
                        HBox manageBtn = new HBox(editBtn, deleteBtn);
                        manageBtn.setStyle("-fx-alignment:center; -fx-spacing: 8;");
                        
                        setGraphic(manageBtn);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        
        colActions.setCellFactory(cellFactory);
        refreshTable();
    }
    
    @FXML
    private void refreshTable() {
        showLoading(true);
        
        // Simulate async loading
        new Thread(() -> {
            try {
                Thread.sleep(300); // Small delay for loading effect
                userList.clear();
                userList.addAll(userService.getAllUsers());
                
                Platform.runLater(() -> {
                    if (searchField != null && filteredData != null) {
                        userTable.setItems(filteredData);
                    } else {
                        userTable.setItems(userList);
                    }
                    
                    // Show empty state if no users
                    if (emptyStateLabel != null) {
                        emptyStateLabel.setVisible(userList.isEmpty());
                        emptyStateLabel.setManaged(userList.isEmpty());
                    }
                    
                    showLoading(false);
                });
            } catch (SQLException ex) {
                Platform.runLater(() -> {
                    Logger.getLogger(AdminDashboardController.class.getName()).log(Level.SEVERE, null, ex);
                    showErrorAlert("Error loading users: " + ex.getMessage());
                    showLoading(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    @FXML
    private void addUser(ActionEvent event) {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String password = passwordField.getText();
        String role = roleField.getText().trim();
        String bio = bioField != null ? bioField.getText().trim() : "";
        
        // Validation
        if (email.isEmpty() || name.isEmpty() || role.isEmpty()) {
            showErrorAlert("Please fill in all required fields");
            return;
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showErrorAlert("Please enter a valid email address");
            return;
        }
        
        if (!NAME_PATTERN.matcher(name).matches()) {
            showErrorAlert("Name must contain only letters (2-50 characters)");
            return;
        }
        
        // Disable button and show loading
        btnAdd.setDisable(true);
        String originalText = btnAdd.getText();
        btnAdd.setText("⏳ Saving...");
        
        new Thread(() -> {
            try {
                if (selectedUser != null) {
                    // Update mode
                    selectedUser.setEmail(email);
                    selectedUser.setName(name);
                    selectedUser.setRole(role);
                    selectedUser.setBio(bio.isEmpty() ? null : bio);
                    
                    userService.updateUser(selectedUser.getId(), selectedUser);
                    
                    Platform.runLater(() -> {
                        showSuccessAlert("User updated successfully!");
                        selectedUser = null;
                        btnAdd.setText("💾 Save User");
                        clearFields();
                        refreshTable();
                        btnAdd.setDisable(false);
                    });
                } else {
                    // Add mode
                    if (password.isEmpty()) {
                        Platform.runLater(() -> {
                            showErrorAlert("Password is required for new users");
                            btnAdd.setText(originalText);
                            btnAdd.setDisable(false);
                        });
                        return;
                    }
                    
                    if (password.length() < 6) {
                        Platform.runLater(() -> {
                            showErrorAlert("Password must be at least 6 characters");
                            btnAdd.setText(originalText);
                            btnAdd.setDisable(false);
                        });
                        return;
                    }
                    
                    User user = new User(email, password, name, role);
                    if (!bio.isEmpty()) {
                        user.setBio(bio);
                    }
                    
                    userService.addUser(user);
                    
                    Platform.runLater(() -> {
                        showSuccessAlert("User added successfully!");
                        clearFields();
                        refreshTable();
                        btnAdd.setText(originalText);
                        btnAdd.setDisable(false);
                    });
                }
            } catch (SQLException ex) {
                Platform.runLater(() -> {
                    Logger.getLogger(AdminDashboardController.class.getName()).log(Level.SEVERE, null, ex);
                    showErrorAlert("Error: " + ex.getMessage());
                    btnAdd.setText(originalText);
                    btnAdd.setDisable(false);
                });
            }
        }).start();
    }
    
    private void setTextFields(User user) {
        emailField.setText(user.getEmail());
        nameField.setText(user.getName());
        roleField.setText(user.getRole());
        if (bioField != null) {
            bioField.setText(user.getBio() != null ? user.getBio() : "");
        }
        passwordField.clear();
    }
    
    private void clearFields() {
        emailField.clear();
        nameField.clear();
        passwordField.clear();
        roleField.clear();
        if (bioField != null) {
            bioField.clear();
        }
        selectedUser = null;
        btnAdd.setText("💾 Save User");
    }
    
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(show);
                loadingIndicator.setManaged(show);
            });
        }
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("✓ " + message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("⚠ " + message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleViewHome() {
        try {
            InvestiApp.showHomePage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            InvestiApp.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
