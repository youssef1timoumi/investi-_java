package edu.connexion3a8;

import edu.connexion3a8.entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class InvestiApp extends Application {
    
    private static Stage primaryStage;
    private static User currentUser;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Set app icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("/INVESTI.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Could not load app icon");
        }
        
        // Load login page
        showLoginPage();
        
        stage.setTitle("INVESTI - Where Innovation Meets Investment");
        stage.show();
    }
    
    public static void showLoginPage() throws Exception {
        currentUser = null;
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    public static void showHomePage() throws Exception {
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/Home.fxml"));
        Parent root = loader.load();
        
        // Pass current user to controller
        if (loader.getController() != null) {
            edu.connexion3a8.controllers.HomeController controller = loader.getController();
            controller.setCurrentUser(currentUser);
        }
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
    }
    
    public static void showAdminDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/AdminDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1500, 950);
        primaryStage.setScene(scene);
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
