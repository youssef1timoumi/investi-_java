package edu.connexion3a8.controllers;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {
    
    @FXML private Button adminDashboardBtn;
    
    private User currentUser;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        
        // Show admin dashboard button only if user is admin
        if (user != null && "admin".equals(user.getRole())) {
            adminDashboardBtn.setVisible(true);
            adminDashboardBtn.setManaged(true);
        } else {
            adminDashboardBtn.setVisible(false);
            adminDashboardBtn.setManaged(false);
        }
    }
    
    @FXML
    private void handleAdminDashboard() {
        try {
            InvestiApp.showAdminDashboard();
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
