package edu.connexion3a8.controllers;

import edu.connexion3a8.InvestiApp;
import edu.connexion3a8.entities.User;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

/**
 * Shared application shell — header + collapsible left sidebar + content slot.
 *
 * <p>Every page is loaded into the {@code contentSlot} via
 * {@link #setContent(Node)}. The header and sidebar stay persistent across
 * navigation.
 *
 * <p>The sidebar is collapsible via the hamburger button on the header; we
 * animate its preferred width AND clip its contents with a {@link Rectangle}
 * tied to the holder width, so the nav items disappear together with the
 * sidebar rather than floating over the content.
 */
public class AppShellController {

    @FXML private BorderPane shellRoot;
    @FXML private HBox shellHeader;
    @FXML private Button menuToggleBtn;
    @FXML private Button themeToggleBtn;

    @FXML private StackPane avatarCircle;
    @FXML private Label avatarInitial;
    @FXML private Label profileName;
    @FXML private Label profileRole;
    @FXML private Button profileBtn;

    @FXML private StackPane sidebarClipHolder;
    @FXML private VBox sidebar;
    @FXML private Label adminSectionLabel;
    @FXML private Button navHome;
    @FXML private Button navCourses;
    @FXML private Button navEvents;
    @FXML private Button navForum;
    @FXML private Button navProducts;
    @FXML private Button navCollaboration;
    @FXML private Button navAdmin;
    @FXML private Button logoutBtn;

    @FXML private ScrollPane contentScroll;
    @FXML private StackPane contentSlot;

    private boolean sidebarOpen = true;
    private static final double SIDEBAR_OPEN_WIDTH = 240.0;
    private static final double SIDEBAR_CLOSED_WIDTH = 0.0;

    /** Clip that follows the holder width so nav items are cut off during collapse. */
    private Rectangle sidebarClip;

    @FXML
    public void initialize() {
        contentScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Attach a width-bound clip so collapsing actually hides the nav items.
        sidebarClip = new Rectangle();
        sidebarClip.setArcWidth(0);
        sidebarClip.setArcHeight(0);
        sidebarClip.widthProperty().bind(sidebarClipHolder.widthProperty());
        sidebarClip.heightProperty().bind(sidebarClipHolder.heightProperty());
        sidebarClipHolder.setClip(sidebarClip);

        // Make the holder itself clamp to the sidebar's preferred width so the
        // center content reflows when the sidebar collapses.
        sidebarClipHolder.prefWidthProperty().bind(sidebar.prefWidthProperty());
        sidebarClipHolder.minWidthProperty().bind(sidebar.prefWidthProperty());
        sidebarClipHolder.maxWidthProperty().bind(sidebar.prefWidthProperty());

        applyUser(InvestiApp.getCurrentUser());
    }

    // ------------------------------------------------------------------
    // User / role handling
    // ------------------------------------------------------------------

    public void applyUser(User user) {
        if (user == null) {
            profileName.setText("Guest");
            profileRole.setText("");
            avatarInitial.setText("?");
            hideAdminSection();
            return;
        }
        String name = user.getName() == null ? "User" : user.getName();
        profileName.setText(name);
        String role = user.getRole() == null ? "" : capitalize(user.getRole());
        profileRole.setText(role);
        avatarInitial.setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase());

        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());
        if (navAdmin != null) {
            navAdmin.setVisible(isAdmin);
            navAdmin.setManaged(isAdmin);
        }
        if (adminSectionLabel != null) {
            adminSectionLabel.setVisible(isAdmin);
            adminSectionLabel.setManaged(isAdmin);
        }

        // Courses is shown to everyone; Collaboration remains visible so role-
        // based gating inside InvestiApp.showCollaborationModule(...) still
        // shows the right dashboard (admin / investor / entrepreneur).
    }

    private void hideAdminSection() {
        if (navAdmin != null) {
            navAdmin.setVisible(false);
            navAdmin.setManaged(false);
        }
        if (adminSectionLabel != null) {
            adminSectionLabel.setVisible(false);
            adminSectionLabel.setManaged(false);
        }
    }

    // ------------------------------------------------------------------
    // Page content + active nav state
    // ------------------------------------------------------------------

    public void setContent(Node node) {
        contentSlot.getChildren().setAll(node);
        contentScroll.setVvalue(0);
        clearNavActiveState();
    }

    public void setActive(String page) {
        clearNavActiveState();
        Button target = null;
        switch (page == null ? "" : page.toLowerCase()) {
            case "home":          target = navHome; break;
            case "courses":       target = navCourses; break;
            case "events":        target = navEvents; break;
            case "forum":         target = navForum; break;
            case "products":      target = navProducts; break;
            case "collaboration": target = navCollaboration; break;
            case "admin":         target = navAdmin; break;
            default: break;
        }
        if (target != null && !target.getStyleClass().contains("is-active")) {
            target.getStyleClass().add("is-active");
        }
    }

    private void clearNavActiveState() {
        List<Button> all = Arrays.asList(navHome, navCourses, navEvents, navForum, navProducts, navCollaboration, navAdmin);
        for (Button b : all) {
            if (b != null) b.getStyleClass().remove("is-active");
        }
    }

    // ------------------------------------------------------------------
    // Sidebar toggle (width animation; clip hides children automatically)
    // ------------------------------------------------------------------

    @FXML
    private void handleToggleSidebar() {
        sidebarOpen = !sidebarOpen;
        double target = sidebarOpen ? SIDEBAR_OPEN_WIDTH : SIDEBAR_CLOSED_WIDTH;
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(220),
                new KeyValue(sidebar.prefWidthProperty(), target),
                new KeyValue(sidebar.minWidthProperty(), target),
                new KeyValue(sidebar.maxWidthProperty(), target)));
        tl.play();
    }

    // ------------------------------------------------------------------
    // Dark / light theme toggle
    // ------------------------------------------------------------------

    @FXML
    private void handleToggleTheme() {
        Scene scene = shellRoot.getScene();
        if (scene == null) return;
        boolean isDark = scene.getRoot().getStyleClass().contains("theme-dark");
        if (isDark) {
            scene.getRoot().getStyleClass().remove("theme-dark");
            themeToggleBtn.setText("🌙");
        } else {
            if (!scene.getRoot().getStyleClass().contains("theme-dark")) {
                scene.getRoot().getStyleClass().add("theme-dark");
            }
            themeToggleBtn.setText("☀");
        }
    }

    // ------------------------------------------------------------------
    // Navigation handlers (delegate to InvestiApp)
    // ------------------------------------------------------------------

    @FXML
    private void handleGoHome() {
        safeRun(() -> InvestiApp.showHomePage());
    }

    @FXML
    private void handleGoCourses() {
        safeRun(() -> InvestiApp.showGamificationMenu());
    }

    @FXML
    private void handleGoEvents() {
        User u = InvestiApp.getCurrentUser();
        if (u != null) safeRun(() -> InvestiApp.showEventsPage(u));
    }

    @FXML
    private void handleGoForum() {
        safeRun(() -> InvestiApp.showForum());
    }

    @FXML
    private void handleGoProducts() {
        safeRun(() -> InvestiApp.showProductManagement());
    }

    @FXML
    private void handleGoCollaboration() {
        User u = InvestiApp.getCurrentUser();
        if (u != null) safeRun(() -> InvestiApp.showCollaborationModule(u));
    }

    @FXML
    private void handleGoAdmin() {
        safeRun(() -> InvestiApp.showAdminDashboard());
    }

    @FXML
    private void handleOpenProfile() {
        safeRun(() -> InvestiApp.showProfilePage());
    }

    @FXML
    private void handleLogout() {
        safeRun(() -> InvestiApp.showLoginPage());
    }

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }

    private void safeRun(ThrowingRunnable r) {
        try {
            r.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
