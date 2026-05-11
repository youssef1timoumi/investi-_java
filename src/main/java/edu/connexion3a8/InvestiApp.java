package edu.connexion3a8;

import edu.connexion3a8.controllers.AppShellController;
import edu.connexion3a8.entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Application entry point for the JavaFX Investi desktop app.
 *
 * <p><b>Navigation model:</b> one {@link Scene} hosts one {@code AppShell}
 * (header + collapsible left sidebar + content slot). Pages are loaded into
 * the shell's content slot via {@link AppShellController#setContent}. The
 * shell stays persistent across navigation, preserving window state and
 * giving a unified app experience.
 */
public class InvestiApp extends Application {

    private static Stage primaryStage;
    private static Scene appScene;
    private static User currentUser;

    /** Lazily-loaded app shell (header + sidebar). */
    private static Parent shellRoot;
    private static AppShellController shellController;

    private static final double DEFAULT_WIDTH = 1320;
    private static final double DEFAULT_HEIGHT = 820;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        try {
            Image icon = new Image(getClass().getResourceAsStream("/INVESTI.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Could not load app icon");
        }

        // Initialize the single Scene that will host all navigation. Start from
        // the Login FXML so the login page is visible on launch (no shell yet).
        FXMLLoader loginLoader = new FXMLLoader(InvestiApp.class.getResource("/Login.fxml"));
        Parent loginRoot = loginLoader.load();

        appScene = new Scene(loginRoot, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        attachGlobalStylesheet(appScene);
        primaryStage.setScene(appScene);

        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(640);

        double screenW = Screen.getPrimary().getVisualBounds().getWidth();
        double screenH = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setWidth(Math.min(DEFAULT_WIDTH, screenW));
        primaryStage.setHeight(Math.min(DEFAULT_HEIGHT, screenH));

        primaryStage.setMaximized(true);
        primaryStage.setTitle("INVESTI - Where Innovation Meets Investment");
        primaryStage.show();
    }

    // ------------------------------------------------------------------
    // Scene + shell plumbing
    // ------------------------------------------------------------------

    private static void setSceneRoot(Parent root) {
        if (appScene == null) {
            appScene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            attachGlobalStylesheet(appScene);
            primaryStage.setScene(appScene);
        } else {
            appScene.setRoot(root);
        }
    }

    private static void attachGlobalStylesheet(Scene scene) {
        try {
            java.net.URL css = InvestiApp.class.getResource("/css/investi-theme.css");
            if (css != null) {
                String href = css.toExternalForm();
                if (!scene.getStylesheets().contains(href)) {
                    scene.getStylesheets().add(href);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not attach investi-theme.css: " + e.getMessage());
        }
    }

    /** Build the shell once; subsequent calls return the cached instance. */
    private static void ensureShellLoaded() throws Exception {
        if (shellRoot == null) {
            FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/AppShell.fxml"));
            shellRoot = loader.load();
            shellController = loader.getController();
        }
    }

    /**
     * Load a page FXML and render it inside the shell.
     *
     * @param fxmlPath classpath path to the FXML (e.g. {@code /Home.fxml})
     * @param activeNav name of the nav item to highlight (or {@code null})
     * @param controllerConsumer optional callback to receive the page's controller
     * @return the loaded controller for further wiring if needed
     */
    private static Object showInShell(String fxmlPath,
                                      String activeNav,
                                      java.util.function.Consumer<Object> controllerConsumer) throws Exception {
        ensureShellLoaded();

        // Always re-apply the current user so the header reflects role changes.
        shellController.applyUser(currentUser);

        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource(fxmlPath));
        Parent pageRoot = loader.load();
        Object pageController = loader.getController();
        if (controllerConsumer != null) controllerConsumer.accept(pageController);

        shellController.setContent(pageRoot);
        if (activeNav != null) shellController.setActive(activeNav);

        // Ensure the scene's root is the shell (first-time navigation from Login).
        if (appScene.getRoot() != shellRoot) {
            setSceneRoot(shellRoot);
        }
        return pageController;
    }

    // ------------------------------------------------------------------
    // Navigation endpoints
    // ------------------------------------------------------------------

    public static void showLoginPage() throws Exception {
        currentUser = null;
        // Reset the shell reference so it's re-built on next login (picks up new user).
        shellRoot = null;
        shellController = null;
        FXMLLoader loader = new FXMLLoader(InvestiApp.class.getResource("/Login.fxml"));
        Parent root = loader.load();
        setSceneRoot(root);
    }

    public static void showHomePage() throws Exception {
        showInShell("/Home.fxml", "home", c -> {
            if (c instanceof edu.connexion3a8.controllers.HomeController) {
                ((edu.connexion3a8.controllers.HomeController) c).setCurrentUser(currentUser);
            }
        });
    }

    public static void showHomePage(User user) throws Exception {
        currentUser = user;
        showHomePage();
    }

    public static void showEventsPage(User user) throws Exception {
        currentUser = user;
        showInShell("/EventsPage.fxml", "events", c -> {
            if (c instanceof edu.connexion3a8.controllers.EventsPageController) {
                ((edu.connexion3a8.controllers.EventsPageController) c).setCurrentUser(currentUser);
            }
        });
    }

    public static void showEventManagement() throws Exception {
        showInShell("/EventManagement.fxml", "events", null);
    }

    public static void showMentorDashboard(User user) throws Exception {
        currentUser = user;
        showInShell("/MentorDashboard.fxml", "events", null);
    }

    public static void showAdminDashboard() throws Exception {
        showInShell("/AdminDashboard.fxml", "admin", null);
    }

    public static void showProfilePage() throws Exception {
        showInShell("/Profile.fxml", null, null);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (shellController != null) shellController.applyUser(user);
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Scene getAppScene() { return appScene; }
    public static Stage getPrimaryStage() { return primaryStage; }

    // ------------------------------------------------------------------
    // Collaboration module
    // ------------------------------------------------------------------

    public static void showCollaborationModule(User user) throws Exception {
        currentUser = user;
        if (user == null) { showLoginPage(); return; }
        switch (user.getRole().toLowerCase()) {
            case "admin":     showCollaborationAdmin(user); break;
            case "investor":  showInvestorDashboard(user); break;
            case "innovator": showEntrepreneurDashboard(user); break;
            default:
                showAlert("Access Denied", "You don't have access to the collaboration module.");
                showHomePage(user);
        }
    }

    public static void showCollaborationMain(User user) throws Exception {
        currentUser = user;
        showInShell("/collaboration/Main.fxml", "collaboration", c -> {
            if (c instanceof edu.connexion3a8.controllers.collaboration.MainController) {
                ((edu.connexion3a8.controllers.collaboration.MainController) c).setCurrentUser(currentUser);
            }
        });
    }

    public static void showEntrepreneurDashboard(User user) throws Exception {
        currentUser = user;
        if (!user.getRole().equalsIgnoreCase("innovator") && !user.getRole().equalsIgnoreCase("admin")) {
            showAlert("Access Denied", "Only innovators can access the entrepreneur dashboard.");
            showHomePage(user);
            return;
        }
        showInShell("/collaboration/EntrepreneurDashboard.fxml", "collaboration", c -> {
            if (c instanceof edu.connexion3a8.controllers.collaboration.EntrepreneurController) {
                ((edu.connexion3a8.controllers.collaboration.EntrepreneurController) c).setCurrentUser(currentUser);
            }
        });
    }

    public static void showInvestorDashboard(User user) throws Exception {
        currentUser = user;
        if (!user.getRole().equalsIgnoreCase("investor") && !user.getRole().equalsIgnoreCase("admin")) {
            showAlert("Access Denied", "Only investors can access the investor dashboard.");
            showHomePage(user);
            return;
        }
        showInShell("/collaboration/InvestorDashboard.fxml", "collaboration", c -> {
            if (c instanceof edu.connexion3a8.controllers.collaboration.InvestorController) {
                ((edu.connexion3a8.controllers.collaboration.InvestorController) c).setCurrentUser(currentUser);
            }
        });
    }

    public static void showCollaborationAdmin(User user) throws Exception {
        currentUser = user;
        if (!user.getRole().equalsIgnoreCase("admin")) {
            showAlert("Access Denied", "Only administrators can access the admin dashboard.");
            showHomePage(user);
            return;
        }
        showInShell("/collaboration/AdminDashboard.fxml", "collaboration", c -> {
            if (c instanceof edu.connexion3a8.controllers.collaboration.AdminController) {
                ((edu.connexion3a8.controllers.collaboration.AdminController) c).setCurrentUser(currentUser);
            }
        });
    }

    public static void showProductManagement() throws Exception {
        // Role-aware routing: admins get the management table with CRUD;
        // everyone else sees the marketplace catalog (matches the web
        // /products page for non-admins).
        User u = currentUser;
        boolean isAdmin = u != null && "admin".equalsIgnoreCase(u.getRole());
        if (isAdmin) {
            showInShell("/collaboration/ProductManagement.fxml", "products", null);
        } else {
            showInShell("/ProductsCatalog.fxml", "products", null);
        }
    }

    // ------------------------------------------------------------------
    // Gamification
    // ------------------------------------------------------------------

    public static void showGamificationMenu() throws Exception {
        showInShell("/gamification/MainMenu.fxml", "courses", c -> {
            if (c instanceof edu.connexion3a8.controllers.gamification.MainMenuController && currentUser != null) {
                ((edu.connexion3a8.controllers.gamification.MainMenuController) c).setUser(currentUser);
            }
        });
    }

    public static void showGamificationMenu(User user) throws Exception {
        currentUser = user;
        showGamificationMenu();
    }

    // ------------------------------------------------------------------
    // Forum
    // ------------------------------------------------------------------

    public static void showForum() throws Exception {
        showInShell("/Forum.fxml", "forum", c -> {
            if (c != null && currentUser != null) {
                try {
                    edu.connexion3a8.controllers.ForumController controller =
                            (edu.connexion3a8.controllers.ForumController) c;
                    controller.setCurrentUser(currentUser);
                } catch (Exception e) {
                    System.out.println("Forum controller doesn't have setCurrentUser method or doesn't exist");
                }
            }
        });
    }

    public static void showForum(User user) throws Exception {
        currentUser = user;
        showForum();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static void showAlert(String title, String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
