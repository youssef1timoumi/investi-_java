package edu.connexion3a8.controllers;

import edu.connexion3a8.entities.ForumPost;
import edu.connexion3a8.entities.ForumComment;
import edu.connexion3a8.services.ForumPostService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ForumController implements Initializable {

    @FXML private VBox postsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ToggleButton allPostsTab;
    @FXML private ToggleButton myPostsTab;
    @FXML private ToggleButton myActivityTab;
    @FXML private ComboBox<String> userSelector;
    @FXML private Label currentUserLabel;
    @FXML private Label userAvatarLabel;
    @FXML private Label composeAvatarLabel;
    @FXML private TextArea composeTextArea;
    @FXML private VBox activeUsersContainer;

    private ForumPostService forumService;
    private String currentUserId;
    private String currentUserName;
    private String currentUserRole;
    private String currentFilter = "all";
    private String currentCategory = "All";
    private List<String[]> allUsers;
    private List<String> pendingImages = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        forumService = new ForumPostService();
        loadUsers();
        setupCategoryFilter();
        setupTabGroup();
        setupDynamicSearch();
        loadPosts();
        loadActiveUsers();
    }

    private void setupDynamicSearch() {
        // Add listener for dynamic search as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                loadPosts();
            } else {
                performSearch(newValue.trim());
            }
        });
    }

    private void performSearch(String searchTerm) {
        try {
            List<ForumPost> posts = forumService.searchPosts(searchTerm);
            displayPosts(posts);
        } catch (SQLException e) {
            // Silently fail for dynamic search
        }
    }

    private void loadUsers() {
        try {
            allUsers = forumService.getAllUsers();
            if (allUsers.isEmpty()) {
                showError("No users found in database.");
                return;
            }
            
            userSelector.getItems().clear();
            for (String[] user : allUsers) {
                userSelector.getItems().add(user[1] + " (" + capitalizeRole(user[3]) + ")");
            }
            
            userSelector.getSelectionModel().selectFirst();
            setCurrentUser(0);
            
            userSelector.setOnAction(e -> {
                int idx = userSelector.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    setCurrentUser(idx);
                    loadPosts();
                }
            });
        } catch (SQLException e) {
            showError("Failed to load users: " + e.getMessage());
        }
    }

    private String capitalizeRole(String role) {
        if (role == null || role.isEmpty()) return "User";
        return role.substring(0, 1).toUpperCase() + role.substring(1);
    }

    private boolean isAdmin() {
        return "admin".equalsIgnoreCase(currentUserRole);
    }

    private void setCurrentUser(int index) {
        if (index >= 0 && index < allUsers.size()) {
            currentUserId = allUsers.get(index)[0];
            currentUserName = allUsers.get(index)[1];
            currentUserRole = allUsers.get(index)[3]; // role is at index 3
            currentUserLabel.setText(currentUserName);
            String initials = getInitials(currentUserName);
            userAvatarLabel.setText(initials);
            composeAvatarLabel.setText(initials);
        }
    }

    private void loadActiveUsers() {
        activeUsersContainer.getChildren().clear();
        try {
            List<String[]> users = forumService.getAllUsers();
            int count = 0;
            for (String[] user : users) {
                if (count >= 5) break;
                HBox userRow = createUserRow(user);
                activeUsersContainer.getChildren().add(userRow);
                count++;
            }
        } catch (SQLException e) {
            // Ignore
        }
    }

    private HBox createUserRow(String[] user) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 0;");
        
        // Avatar color based on role
        String avatarColor = getRoleColor(user[3]);
        Label avatar = new Label(getInitials(user[1]));
        avatar.setStyle("-fx-background-color: " + avatarColor + "; -fx-background-radius: 20; " +
                "-fx-min-width: 40; -fx-min-height: 40; -fx-max-width: 40; -fx-max-height: 40; " +
                "-fx-alignment: center; -fx-text-fill: white; -fx-font-weight: bold;");
        
        VBox info = new VBox(2);
        Label name = new Label(user[1]);
        name.setStyle("-fx-text-fill: #e7e9ea; -fx-font-weight: bold;");
        
        // Role badge with color
        Label role = new Label(capitalizeRole(user[3]));
        role.setStyle("-fx-background-color: " + avatarColor + "; -fx-text-fill: white; " +
                "-fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 10px;");
        info.getChildren().addAll(name, role);
        
        row.getChildren().addAll(avatar, info);
        return row;
    }

    private String getRoleColor(String role) {
        if (role == null) return "#456990";
        switch (role.toLowerCase()) {
            case "admin": return "#A62639";      // Brown Red for admins
            case "investor": return "#9B7E46";   // Faded Copper for investors
            case "innovator": return "#456990";  // Baltic Blue for innovators
            default: return "#456990";
        }
    }

    private void setupCategoryFilter() {
        categoryFilter.getItems().addAll(
            "All", "Tips & Advice", "Success Stories", 
            "Investor Insights", "Collaboration", "Announcements", "General"
        );
        categoryFilter.setValue("All");
        categoryFilter.setOnAction(e -> {
            currentCategory = categoryFilter.getValue();
            loadPosts();
        });
    }

    private void setupTabGroup() {
        ToggleGroup tabGroup = new ToggleGroup();
        allPostsTab.setToggleGroup(tabGroup);
        myPostsTab.setToggleGroup(tabGroup);
        myActivityTab.setToggleGroup(tabGroup);
        allPostsTab.setSelected(true);
    }

    @FXML
    private void handleAllPosts() {
        currentFilter = "all";
        loadPosts();
    }

    @FXML
    private void handleMyPosts() {
        currentFilter = "myPosts";
        loadPosts();
    }

    @FXML
    private void handleMyActivity() {
        currentFilter = "myActivity";
        loadPosts();
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPosts();
            return;
        }
        performSearch(searchTerm);
    }

    @FXML
    private void handleCreatePost() {
        showCreatePostDialog();
    }

    @FXML
    private void handleAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Images");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) composeTextArea.getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            pendingImages.clear();
            for (File file : files) {
                pendingImages.add(file.getAbsolutePath());
            }
            showSuccess("Added " + files.size() + " image(s)");
        }
    }

    @FXML
    private void handleQuickPost() {
        String content = composeTextArea.getText().trim();
        if (content.isEmpty() && pendingImages.isEmpty()) {
            return;
        }
        
        ForumPost post = new ForumPost(currentUserId, null, content.isEmpty() ? null : content, "General");
        for (String img : pendingImages) {
            post.addImagePath(img);
        }
        
        try {
            // Validate post before saving
            String validationError = forumService.validatePost(post);
            if (validationError != null) {
                showError(validationError);
                return;
            }
            
            forumService.addPost(post);
            composeTextArea.clear();
            pendingImages.clear();
            loadPosts();
            showSuccess("Post published!");
        } catch (SQLException e) {
            showError("Failed to post: " + e.getMessage());
        }
    }

    private void loadPosts() {
        try {
            List<ForumPost> posts;
            
            switch (currentFilter) {
                case "myPosts":
                    posts = forumService.getPostsByUser(currentUserId);
                    break;
                case "myActivity":
                    posts = forumService.getPostsCommentedByUser(currentUserId);
                    List<ForumPost> votedPosts = forumService.getPostsVotedByUser(currentUserId);
                    for (ForumPost p : votedPosts) {
                        if (posts.stream().noneMatch(post -> post.getId().equals(p.getId()))) {
                            posts.add(p);
                        }
                    }
                    break;
                default:
                    if (currentCategory.equals("All")) {
                        posts = forumService.getAllPosts();
                    } else {
                        posts = forumService.getPostsByCategory(currentCategory);
                    }
            }
            
            displayPosts(posts);
            
        } catch (SQLException e) {
            showError("Failed to load posts: " + e.getMessage());
        }
    }


    private void displayPosts(List<ForumPost> posts) {
        postsContainer.getChildren().clear();
        
        if (posts.isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-padding: 50;");
            Label emptyLabel = new Label("No posts yet");
            emptyLabel.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 20px; -fx-font-weight: bold;");
            Label subLabel = new Label("Be the first to share something!");
            subLabel.setStyle("-fx-text-fill: #71767b; -fx-font-size: 14px;");
            emptyState.getChildren().addAll(emptyLabel, subLabel);
            postsContainer.getChildren().add(emptyState);
            return;
        }
        
        for (ForumPost post : posts) {
            VBox postCard = createPostCard(post);
            postsContainer.getChildren().add(postCard);
        }
    }

    private VBox createPostCard(ForumPost post) {
        VBox card = new VBox(10);
        card.getStyleClass().add("post-card");
        
        // Main content row
        HBox mainRow = new HBox(12);
        
        // Avatar
        Label avatar = new Label(getInitials(post.getAuthorName()));
        avatar.getStyleClass().add("user-avatar");
        
        // Content column
        VBox contentCol = new VBox(4);
        HBox.setHgrow(contentCol, Priority.ALWAYS);
        
        // Header: name, username, time
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(post.getAuthorName() != null ? post.getAuthorName() : "Anonymous");
        nameLabel.setStyle("-fx-text-fill: #e7e9ea; -fx-font-weight: bold; -fx-font-size: 15px;");
        
        Label timeLabel = new Label("· " + getRelativeTime(post.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #71767b; -fx-font-size: 14px;");
        
        if (post.getCategory() != null) {
            Label categoryLabel = new Label(post.getCategory());
            categoryLabel.setStyle("-fx-background-color: #456990; -fx-text-fill: white; " +
                    "-fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11px;");
            header.getChildren().addAll(nameLabel, timeLabel, categoryLabel);
        } else {
            header.getChildren().addAll(nameLabel, timeLabel);
        }
        
        contentCol.getChildren().add(header);
        
        // Title (if exists)
        if (post.getTitle() != null && !post.getTitle().isEmpty()) {
            Label titleLabel = new Label(post.getTitle());
            titleLabel.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 16px; -fx-font-weight: bold;");
            titleLabel.setWrapText(true);
            contentCol.getChildren().add(titleLabel);
        }
        
        // Content
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            Label contentLabel = new Label(post.getContent());
            contentLabel.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 15px;");
            contentLabel.setWrapText(true);
            contentCol.getChildren().add(contentLabel);
        }
        
        // Images
        if (post.hasImages()) {
            HBox imagesBox = createImagesPreview(post.getImagePaths());
            contentCol.getChildren().add(imagesBox);
        }
        
        // Actions row
        HBox actions = createActionsRow(post);
        contentCol.getChildren().add(actions);
        
        mainRow.getChildren().addAll(avatar, contentCol);
        card.getChildren().add(mainRow);
        
        // Click handler
        card.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof Button) return;
            showPostDetails(post);
        });
        
        return card;
    }

    private HBox createImagesPreview(List<String> imagePaths) {
        HBox box = new HBox(8);
        box.setStyle("-fx-padding: 10 0;");
        
        // For multiple images, create a horizontal scrollable container
        if (imagePaths.size() > 1) {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(220);
            scrollPane.setMaxHeight(220);
            
            HBox imagesRow = new HBox(8);
            imagesRow.setStyle("-fx-background-color: transparent;");
            
            for (int i = 0; i < imagePaths.size(); i++) {
                try {
                    ImageView img = new ImageView();
                    File file = new File(imagePaths.get(i));
                    if (file.exists()) {
                        img.setImage(new Image(file.toURI().toString()));
                    }
                    img.setFitHeight(200);
                    img.setFitWidth(200);
                    img.setPreserveRatio(true);
                    img.setStyle("-fx-cursor: hand;");
                    
                    // Rounded corners effect
                    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(200, 200);
                    clip.setArcWidth(16);
                    clip.setArcHeight(16);
                    img.setClip(clip);
                    
                    final int idx = i;
                    img.setOnMouseClicked(e -> {
                        e.consume();
                        showFullImage(imagePaths.get(idx));
                    });
                    
                    imagesRow.getChildren().add(img);
                } catch (Exception e) {
                    // Skip
                }
            }
            
            scrollPane.setContent(imagesRow);
            box.getChildren().add(scrollPane);
            
            // Add image count indicator
            Label countLabel = new Label(imagePaths.size() + " images");
            countLabel.setStyle("-fx-text-fill: #71767b; -fx-font-size: 12px; -fx-padding: 5 0 0 0;");
            
            VBox wrapper = new VBox(5);
            wrapper.getChildren().addAll(scrollPane, countLabel);
            box.getChildren().clear();
            box.getChildren().add(wrapper);
        } else {
            // Single image - display normally
            for (int i = 0; i < imagePaths.size(); i++) {
                try {
                    ImageView img = new ImageView();
                    File file = new File(imagePaths.get(i));
                    if (file.exists()) {
                        img.setImage(new Image(file.toURI().toString()));
                    }
                    img.setFitHeight(300);
                    img.setFitWidth(400);
                    img.setPreserveRatio(true);
                    img.setStyle("-fx-cursor: hand;");
                    
                    // Rounded corners effect
                    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(400, 300);
                    clip.setArcWidth(16);
                    clip.setArcHeight(16);
                    img.setClip(clip);
                    
                    final int idx = i;
                    img.setOnMouseClicked(e -> {
                        e.consume();
                        showFullImage(imagePaths.get(idx));
                    });
                    
                    box.getChildren().add(img);
                } catch (Exception e) {
                    // Skip
                }
            }
        }
        
        return box;
    }

    private HBox createActionsRow(ForumPost post) {
        HBox actions = new HBox(0);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setStyle("-fx-padding: 10 0 0 0;");
        
        // Comments
        int commentCount = 0;
        try {
            commentCount = forumService.getCommentCountByPost(post.getId());
        } catch (SQLException e) {}
        
        Button commentBtn = new Button("💬 " + commentCount);
        commentBtn.getStyleClass().add("action-btn");
        commentBtn.setOnAction(e -> showPostDetails(post));
        
        // Upvote (Faded Copper when active)
        Button upvoteBtn = new Button("⬆ " + post.getUpvotes());
        upvoteBtn.getStyleClass().addAll("action-btn", "vote-btn-up");
        try {
            String vote = forumService.getUserVoteOnPost(post.getId(), currentUserId);
            if ("upvote".equals(vote)) {
                upvoteBtn.setStyle("-fx-text-fill: #9B7E46;");
            }
        } catch (SQLException e) {}
        upvoteBtn.setOnAction(e -> {
            e.consume();
            handleVote(post, "upvote");
        });
        
        // Downvote (Brown Red when active)
        Button downvoteBtn = new Button("⬇ " + post.getDownvotes());
        downvoteBtn.getStyleClass().addAll("action-btn", "vote-btn-down");
        try {
            String vote = forumService.getUserVoteOnPost(post.getId(), currentUserId);
            if ("downvote".equals(vote)) {
                downvoteBtn.setStyle("-fx-text-fill: #A62639;");
            }
        } catch (SQLException e) {}
        downvoteBtn.setOnAction(e -> {
            e.consume();
            handleVote(post, "downvote");
        });
        
        // Views
        Label viewsLabel = new Label("👁 " + post.getViews());
        viewsLabel.setStyle("-fx-text-fill: #71767b; -fx-font-size: 13px; -fx-padding: 8 12;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        actions.getChildren().addAll(commentBtn, upvoteBtn, downvoteBtn, viewsLabel, spacer);
        
        // Edit/Delete for own posts OR if user is admin
        boolean isOwner = post.getUserId() != null && post.getUserId().equals(currentUserId);
        boolean canModerate = isAdmin();
        
        if (isOwner) {
            Button editBtn = new Button("✏");
            editBtn.getStyleClass().add("action-btn");
            editBtn.setOnAction(e -> {
                e.consume();
                showEditPostDialog(post);
            });
            actions.getChildren().add(editBtn);
        }
        
        if (isOwner || canModerate) {
            Button deleteBtn = new Button("🗑");
            deleteBtn.getStyleClass().addAll("action-btn", "btn-danger");
            if (canModerate && !isOwner) {
                deleteBtn.setTooltip(new Tooltip("Supprimer (Admin)"));
            }
            deleteBtn.setOnAction(e -> {
                e.consume();
                handleDeletePost(post);
            });
            actions.getChildren().add(deleteBtn);
        }
        
        return actions;
    }

    private void handleVote(ForumPost post, String voteType) {
        try {
            // Record view when user interacts with post
            forumService.recordPostView(post.getId(), currentUserId);
            forumService.votePost(post.getId(), currentUserId, voteType);
            loadPosts();
        } catch (SQLException e) {
            showError("Vote failed: " + e.getMessage());
        }
    }

    private void showFullImage(String imagePath) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        
        try {
            ImageView imageView = new ImageView();
            File file = new File(imagePath);
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(900);
            imageView.setFitHeight(700);
            
            StackPane root = new StackPane(imageView);
            root.setStyle("-fx-background-color: rgba(0,0,0,0.9); -fx-cursor: hand;");
            root.setOnMouseClicked(e -> dialog.close());
            
            Scene scene = new Scene(root, 900, 700);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            showError("Failed to load image");
        }
    }


    private void showCreatePostDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Create Post");
        
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #000000; -fx-border-color: #2f3336; -fx-border-radius: 16; -fx-background-radius: 16;");
        root.setMaxWidth(600);
        
        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 10 15; -fx-border-color: #2f3336; -fx-border-width: 0 0 1 0;");
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e7e9ea; -fx-font-size: 18px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());
        
        header.getChildren().add(closeBtn);
        
        // Content
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        HBox composeRow = new HBox(12);
        Label avatar = new Label(getInitials(currentUserName));
        avatar.getStyleClass().add("user-avatar");
        
        VBox inputCol = new VBox(10);
        HBox.setHgrow(inputCol, Priority.ALWAYS);
        
        TextField titleField = new TextField();
        titleField.setPromptText("Title (optional)");
        titleField.getStyleClass().add("text-input-dark");
        
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("What's on your mind?");
        contentArea.setPrefRowCount(5);
        contentArea.getStyleClass().add("text-area-dark");
        contentArea.setWrapText(true);
        
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("General", "Tips & Advice", "Success Stories", 
                "Investor Insights", "Collaboration", "Announcements");
        categoryBox.setValue("General");
        categoryBox.setStyle("-fx-background-color: #16181c; -fx-text-fill: #e7e9ea;");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        
        // Images preview
        List<String> selectedImages = new ArrayList<>();
        HBox imagesPreview = new HBox(8);
        imagesPreview.setStyle("-fx-padding: 10 0;");
        
        Button addImgBtn = new Button("🖼 Add Photos");
        addImgBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1d9bf0; -fx-cursor: hand;");
        addImgBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            List<File> files = fc.showOpenMultipleDialog(dialog);
            if (files != null) {
                selectedImages.clear();
                imagesPreview.getChildren().clear();
                for (File f : files) {
                    selectedImages.add(f.getAbsolutePath());
                    try {
                        ImageView thumb = new ImageView(new Image(f.toURI().toString()));
                        thumb.setFitHeight(60);
                        thumb.setPreserveRatio(true);
                        imagesPreview.getChildren().add(thumb);
                    } catch (Exception ex) {}
                }
            }
        });
        
        inputCol.getChildren().addAll(titleField, contentArea, categoryBox, addImgBtn, imagesPreview);
        composeRow.getChildren().addAll(avatar, inputCol);
        
        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setStyle("-fx-padding: 15 20; -fx-border-color: #2f3336; -fx-border-width: 1 0 0 0;");
        
        Button postBtn = new Button("Post");
        postBtn.getStyleClass().add("btn-primary");
        postBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String postContent = contentArea.getText().trim();
            
            if (title.isEmpty() && postContent.isEmpty() && selectedImages.isEmpty()) {
                showError("Add some content to your post");
                return;
            }
            
            ForumPost newPost = new ForumPost(currentUserId,
                    title.isEmpty() ? null : title,
                    postContent.isEmpty() ? null : postContent,
                    categoryBox.getValue());
            
            for (String img : selectedImages) {
                newPost.addImagePath(img);
            }
            
            try {
                // Validate post before saving
                String validationError = forumService.validatePost(newPost);
                if (validationError != null) {
                    showError(validationError);
                    return;
                }
                
                forumService.addPost(newPost);
                dialog.close();
                loadPosts();
                showSuccess("Post published!");
            } catch (SQLException ex) {
                showError("Failed to create post: " + ex.getMessage());
            }
        });
        
        footer.getChildren().add(postBtn);
        
        content.getChildren().add(composeRow);
        root.getChildren().addAll(header, content, footer);
        
        Scene scene = new Scene(root, 600, 450);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showEditPostDialog(ForumPost post) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #000000; -fx-border-color: #2f3336; -fx-border-radius: 16; -fx-background-radius: 16;");
        root.setMaxWidth(600);
        
        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 10 15; -fx-border-color: #2f3336; -fx-border-width: 0 0 1 0;");
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e7e9ea; -fx-font-size: 18px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());
        
        Label titleLbl = new Label("Edit Post");
        titleLbl.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 0 15;");
        
        header.getChildren().addAll(closeBtn, titleLbl);
        
        // Content
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        TextField titleField = new TextField(post.getTitle() != null ? post.getTitle() : "");
        titleField.setPromptText("Title (optional)");
        titleField.getStyleClass().add("text-input-dark");
        
        TextArea contentArea = new TextArea(post.getContent() != null ? post.getContent() : "");
        contentArea.setPromptText("What's on your mind?");
        contentArea.setPrefRowCount(5);
        contentArea.getStyleClass().add("text-area-dark");
        contentArea.setWrapText(true);
        
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("General", "Tips & Advice", "Success Stories", 
                "Investor Insights", "Collaboration", "Announcements");
        categoryBox.setValue(post.getCategory() != null ? post.getCategory() : "General");
        categoryBox.setStyle("-fx-background-color: #16181c; -fx-text-fill: #e7e9ea;");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        
        content.getChildren().addAll(titleField, contentArea, categoryBox);
        
        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setStyle("-fx-padding: 15 20; -fx-border-color: #2f3336; -fx-border-width: 1 0 0 0;");
        
        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            post.setTitle(titleField.getText().trim().isEmpty() ? null : titleField.getText().trim());
            post.setContent(contentArea.getText().trim().isEmpty() ? null : contentArea.getText().trim());
            post.setCategory(categoryBox.getValue());
            
            try {
                forumService.updatePost(post.getId(), post);
                dialog.close();
                loadPosts();
            } catch (SQLException ex) {
                showError("Failed to update: " + ex.getMessage());
            }
        });
        
        footer.getChildren().add(saveBtn);
        root.getChildren().addAll(header, content, footer);
        
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void handleDeletePost(ForumPost post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Post");
        confirm.setHeaderText("Delete this post?");
        confirm.setContentText("This can't be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    forumService.deletePost(post.getId());
                    loadPosts();
                } catch (SQLException e) {
                    showError("Failed to delete: " + e.getMessage());
                }
            }
        });
    }


    private void showPostDetails(ForumPost post) {
        // Record view for current user (only counts once per user)
        try {
            forumService.recordPostView(post.getId(), currentUserId);
        } catch (SQLException e) {}
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Post");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");
        
        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 15 20; -fx-background-color: rgba(0,0,0,0.8); -fx-border-color: #2f3336; -fx-border-width: 0 0 1 0;");
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e7e9ea; -fx-font-size: 15px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> dialog.close());
        
        Label titleLbl = new Label("Post");
        titleLbl.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 0 20;");
        
        header.getChildren().addAll(backBtn, titleLbl);
        root.setTop(header);
        
        // Content
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("feed-scroll");
        
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: #000000;");
        
        // Post
        VBox postBox = new VBox(10);
        postBox.setStyle("-fx-padding: 20; -fx-border-color: #2f3336; -fx-border-width: 0 0 1 0;");
        
        HBox postHeader = new HBox(12);
        Label avatar = new Label(getInitials(post.getAuthorName()));
        avatar.getStyleClass().add("user-avatar");
        
        VBox authorInfo = new VBox(2);
        Label authorName = new Label(post.getAuthorName() != null ? post.getAuthorName() : "Anonymous");
        authorName.setStyle("-fx-text-fill: #e7e9ea; -fx-font-weight: bold; -fx-font-size: 15px;");
        Label postTime = new Label(formatDate(post.getCreatedAt()));
        postTime.setStyle("-fx-text-fill: #71767b; -fx-font-size: 13px;");
        authorInfo.getChildren().addAll(authorName, postTime);
        
        postHeader.getChildren().addAll(avatar, authorInfo);
        postBox.getChildren().add(postHeader);
        
        if (post.getTitle() != null && !post.getTitle().isEmpty()) {
            Label title = new Label(post.getTitle());
            title.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 22px; -fx-font-weight: bold;");
            title.setWrapText(true);
            postBox.getChildren().add(title);
        }
        
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            Label contentLbl = new Label(post.getContent());
            contentLbl.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 16px;");
            contentLbl.setWrapText(true);
            postBox.getChildren().add(contentLbl);
        }
        
        if (post.hasImages()) {
            HBox images = createImagesPreview(post.getImagePaths());
            postBox.getChildren().add(images);
        }
        
        // Actions
        HBox actions = createActionsRow(post);
        postBox.getChildren().add(actions);
        
        content.getChildren().add(postBox);
        
        // Add comment box
        VBox addCommentBox = new VBox(10);
        addCommentBox.setStyle("-fx-padding: 15 20; -fx-border-color: #2f3336; -fx-border-width: 0 0 1 0;");
        
        HBox commentRow = new HBox(12);
        Label commentAvatar = new Label(getInitials(currentUserName));
        commentAvatar.getStyleClass().add("user-avatar-small");
        
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Post your reply");
        commentArea.setPrefRowCount(2);
        commentArea.getStyleClass().add("text-area-dark");
        commentArea.setWrapText(true);
        HBox.setHgrow(commentArea, Priority.ALWAYS);
        
        Button replyBtn = new Button("Reply");
        replyBtn.getStyleClass().add("btn-primary");
        replyBtn.setOnAction(e -> {
            if (commentArea.getText().trim().isEmpty()) return;
            
            // Validate comment for bad words
            String validationError = forumService.validateComment(commentArea.getText().trim());
            if (validationError != null) {
                showError(validationError);
                return;
            }
            
            ForumComment comment = new ForumComment(post.getId(), currentUserId, commentArea.getText().trim());
            try {
                forumService.addComment(comment);
                dialog.close();
                showPostDetails(post);
            } catch (SQLException ex) {
                showError("Failed to reply: " + ex.getMessage());
            }
        });
        
        VBox replyCol = new VBox(8);
        replyCol.getChildren().addAll(commentArea, replyBtn);
        HBox.setHgrow(replyCol, Priority.ALWAYS);
        
        commentRow.getChildren().addAll(commentAvatar, replyCol);
        addCommentBox.getChildren().add(commentRow);
        content.getChildren().add(addCommentBox);
        
        // Comments
        try {
            List<ForumComment> comments = forumService.getCommentsByPost(post.getId());
            for (ForumComment comment : comments) {
                VBox commentCard = createCommentCard(comment, post, dialog, 0);
                content.getChildren().add(commentCard);
            }
            
            if (comments.isEmpty()) {
                Label noComments = new Label("No replies yet");
                noComments.setStyle("-fx-text-fill: #71767b; -fx-font-size: 14px; -fx-padding: 20;");
                content.getChildren().add(noComments);
            }
        } catch (SQLException e) {
            Label error = new Label("Failed to load comments");
            error.setStyle("-fx-text-fill: #f4212e;");
            content.getChildren().add(error);
        }
        
        scroll.setContent(content);
        root.setCenter(scroll);
        
        Scene scene = new Scene(root, 700, 700);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        loadPosts();
    }

    private VBox createCommentCard(ForumComment comment, ForumPost post, Stage dialog, int depth) {
        VBox card = new VBox(8);
        card.getStyleClass().add(depth > 0 ? "comment-reply" : "comment-card");
        card.setStyle(card.getStyle() + "-fx-padding: 15 20;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label avatar = new Label(getInitials(comment.getAuthorName()));
        avatar.setStyle("-fx-background-color: #456990; -fx-background-radius: 15; " +
                "-fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; " +
                "-fx-alignment: center; -fx-text-fill: white; -fx-font-size: 11px;");
        
        Label name = new Label(comment.getAuthorName() != null ? comment.getAuthorName() : "Anonymous");
        name.setStyle("-fx-text-fill: #e7e9ea; -fx-font-weight: bold;");
        
        Label time = new Label("· " + getRelativeTime(comment.getCreatedAt()));
        time.setStyle("-fx-text-fill: #71767b; -fx-font-size: 13px;");
        
        header.getChildren().addAll(avatar, name, time);
        
        Label contentLbl = new Label(comment.getContent());
        contentLbl.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 14px;");
        contentLbl.setWrapText(true);
        
        // Actions
        HBox actions = new HBox(15);
        actions.setStyle("-fx-padding: 8 0 0 0;");
        
        Button upBtn = new Button("⬆ " + comment.getUpvotes());
        upBtn.getStyleClass().addAll("action-btn", "vote-btn-up");
        upBtn.setOnAction(e -> {
            try {
                forumService.voteComment(comment.getId(), currentUserId, "upvote");
                // Update button text directly instead of reopening dialog
                ForumComment updated = forumService.getCommentById(comment.getId());
                if (updated != null) {
                    upBtn.setText("⬆ " + updated.getUpvotes());
                    ((Button)actions.getChildren().get(1)).setText("⬇ " + updated.getDownvotes());
                }
            } catch (SQLException ex) {}
        });
        
        Button downBtn = new Button("⬇ " + comment.getDownvotes());
        downBtn.getStyleClass().addAll("action-btn", "vote-btn-down");
        downBtn.setOnAction(e -> {
            try {
                forumService.voteComment(comment.getId(), currentUserId, "downvote");
                // Update button text directly instead of reopening dialog
                ForumComment updated = forumService.getCommentById(comment.getId());
                if (updated != null) {
                    ((Button)actions.getChildren().get(0)).setText("⬆ " + updated.getUpvotes());
                    downBtn.setText("⬇ " + updated.getDownvotes());
                }
            } catch (SQLException ex) {}
        });
        
        Button replyBtn = new Button("↩ Reply");
        replyBtn.getStyleClass().add("action-btn");
        replyBtn.setOnAction(e -> showReplyDialog(comment, post, dialog));
        
        actions.getChildren().addAll(upBtn, downBtn, replyBtn);
        
        // Delete button for own comments OR if user is admin
        boolean isCommentOwner = comment.getUserId() != null && comment.getUserId().equals(currentUserId);
        if (isCommentOwner || isAdmin()) {
            Button deleteBtn = new Button("🗑");
            deleteBtn.getStyleClass().addAll("action-btn", "btn-danger");
            if (isAdmin() && !isCommentOwner) {
                deleteBtn.setTooltip(new Tooltip("Supprimer (Admin)"));
            }
            deleteBtn.setOnAction(e -> {
                try {
                    forumService.deleteComment(comment.getId());
                    dialog.close();
                    showPostDetails(post);
                } catch (SQLException ex) {}
            });
            actions.getChildren().add(deleteBtn);
        }
        
        card.getChildren().addAll(header, contentLbl, actions);
        
        // Nested replies
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            for (ForumComment reply : comment.getReplies()) {
                VBox replyCard = createCommentCard(reply, post, dialog, depth + 1);
                card.getChildren().add(replyCard);
            }
        }
        
        return card;
    }

    private void showReplyDialog(ForumComment parent, ForumPost post, Stage parentDialog) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        
        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #000000; -fx-border-color: #2f3336; -fx-border-radius: 16; -fx-background-radius: 16; -fx-padding: 20;");
        
        Label title = new Label("Reply to " + (parent.getAuthorName() != null ? parent.getAuthorName() : "comment"));
        title.setStyle("-fx-text-fill: #e7e9ea; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Write your reply...");
        replyArea.setPrefRowCount(3);
        replyArea.getStyleClass().add("text-area-dark");
        
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> dialog.close());
        
        Button submitBtn = new Button("Reply");
        submitBtn.getStyleClass().add("btn-primary");
        submitBtn.setOnAction(e -> {
            if (replyArea.getText().trim().isEmpty()) return;
            
            // Validate reply for bad words
            String validationError = forumService.validateComment(replyArea.getText().trim());
            if (validationError != null) {
                showError(validationError);
                return;
            }
            
            ForumComment reply = new ForumComment(post.getId(), currentUserId, replyArea.getText().trim());
            reply.setParentCommentId(parent.getId());
            
            try {
                forumService.addComment(reply);
                dialog.close();
                parentDialog.close();
                showPostDetails(post);
            } catch (SQLException ex) {
                showError("Failed: " + ex.getMessage());
            }
        });
        
        buttons.getChildren().addAll(cancelBtn, submitBtn);
        root.getChildren().addAll(title, replyArea, buttons);
        
        Scene scene = new Scene(root, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(timestamp);
    }

    private String getRelativeTime(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "";
        long diff = System.currentTimeMillis() - timestamp.getTime();
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        if (minutes < 1) return "now";
        if (minutes < 60) return minutes + "m";
        
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (hours < 24) return hours + "h";
        
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        if (days < 7) return days + "d";
        
        return new SimpleDateFormat("MMM dd").format(timestamp);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
