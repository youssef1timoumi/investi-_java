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
        
        // Maximize the window
        stage.setMaximized(true);
        
        // Load login page
        showLoginPage();
        
        stage.setTitle("INVESTI - Where Innovation Meets Investment");
        stage.show();
    }
    
    public static void showLoginPage() throws Exception {
        currentUser = null;
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
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
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    public static void showHomePage(User user) throws Exception {
        currentUser = user;
        showHomePage();
    }

    public static void showEventsPage(User user) throws Exception {
        currentUser = user;
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/EventsPage.fxml"));
        Parent root = loader.load();
        
        if (loader.getController() != null) {
            edu.connexion3a8.controllers.EventsPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);
        }
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    public static void showEventManagement() throws Exception {
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/EventManagement.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
    
    public static void showAdminDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/AdminDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
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
