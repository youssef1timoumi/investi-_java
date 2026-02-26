# 🎬 Alternative Media Viewer (Without JavaFX Media)

## 📋 Overview

If you have issues with `javafx-media` dependency, here's an alternative implementation that opens videos in the system default player instead of embedding them.

## ✅ Benefits

- No additional dependencies needed
- Works with ALL video formats (not just MP4)
- Uses user's preferred video player (VLC, Windows Media Player, etc.)
- Simpler implementation
- No codec issues

## 🔧 Implementation

Replace the `createMediaViewer()` method in `CourseCatalogController.java` with this:

```java
private VBox createMediaViewer(Course course) {
    VBox mediaBox = new VBox(10);
    
    Label mediaLabel = new Label("📺 Course Content:");
    mediaLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000501;");
    
    String contentUrl = course.getContentUrl();
    
    if (contentUrl == null || contentUrl.trim().isEmpty()) {
        Label noMediaLabel = new Label("No media available for this course.");
        noMediaLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic;");
        mediaBox.getChildren().addAll(mediaLabel, noMediaLabel);
        return mediaBox;
    }
    
    // Check if it's a local file or URL
    File mediaFile = new File(contentUrl);
    
    if (mediaFile.exists()) {
        // Local file
        String fileExtension = contentUrl.substring(contentUrl.lastIndexOf(".")).toLowerCase();
        
        if (fileExtension.matches("\\.(mp4|avi|mkv|mov|wmv|flv)")) {
            // Video file - Open in system player
            Button openVideoBtn = new Button("▶ Open Video");
            openVideoBtn.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-padding: 12 24; -fx-cursor: hand; -fx-font-size: 14px;");
            openVideoBtn.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().open(mediaFile);
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Could not open video: " + ex.getMessage());
                    alert.showAndWait();
                }
            });
            
            Label videoInfo = new Label("Video File: " + mediaFile.getName());
            videoInfo.setStyle("-fx-text-fill: #000501; -fx-font-size: 13px; -fx-font-weight: 600;");
            
            Label videoHint = new Label("Click to open in your default video player");
            videoHint.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px; -fx-font-style: italic;");
            
            VBox videoBox = new VBox(10);
            videoBox.setAlignment(Pos.CENTER_LEFT);
            videoBox.setPadding(new Insets(20));
            videoBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #28A745; -fx-border-width: 2px; -fx-border-radius: 8px;");
            videoBox.getChildren().addAll(videoInfo, videoHint, openVideoBtn);
            
            mediaBox.getChildren().addAll(mediaLabel, videoBox);
            
        } else if (fileExtension.equals(".pdf")) {
            // PDF file
            Button openPdfBtn = new Button("📄 Open PDF");
            openPdfBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-padding: 12 24; -fx-cursor: hand; -fx-font-size: 14px;");
            openPdfBtn.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().open(mediaFile);
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Could not open PDF: " + ex.getMessage());
                    alert.showAndWait();
                }
            });
            
            Label pdfInfo = new Label("PDF Document: " + mediaFile.getName());
            pdfInfo.setStyle("-fx-text-fill: #000501; -fx-font-size: 13px; -fx-font-weight: 600;");
            
            Label pdfHint = new Label("Click to open in your default PDF viewer");
            pdfHint.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px; -fx-font-style: italic;");
            
            VBox pdfBox = new VBox(10);
            pdfBox.setAlignment(Pos.CENTER_LEFT);
            pdfBox.setPadding(new Insets(20));
            pdfBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #DC3545; -fx-border-width: 2px; -fx-border-radius: 8px;");
            pdfBox.getChildren().addAll(pdfInfo, pdfHint, openPdfBtn);
            
            mediaBox.getChildren().addAll(mediaLabel, pdfBox);
        }
        
    } else {
        // External URL
        Label urlLabel = new Label("External Content:");
        urlLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #456990;");
        
        Hyperlink link = new Hyperlink(contentUrl);
        link.setStyle("-fx-font-size: 13px;");
        link.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(contentUrl));
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Could not open URL: " + ex.getMessage());
                alert.showAndWait();
            }
        });
        
        VBox urlBox = new VBox(5);
        urlBox.setPadding(new Insets(15));
        urlBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #456990; -fx-border-width: 2px; -fx-border-radius: 8px;");
        urlBox.getChildren().addAll(urlLabel, link);
        
        mediaBox.getChildren().addAll(mediaLabel, urlBox);
    }
    
    return mediaBox;
}
```

## 🔄 How to Apply

### Option 1: Wait for Maven to Download javafx-media
This is the recommended approach - just reload Maven dependencies.

### Option 2: Use Alternative Implementation
If you can't get javafx-media working:

1. Open `CourseCatalogController.java`
2. Find the `createMediaViewer()` method
3. Replace it with the code above
4. Remove these imports (no longer needed):
   ```java
   import javafx.scene.media.Media;
   import javafx.scene.media.MediaPlayer;
   import javafx.scene.media.MediaView;
   ```

## 🎯 Differences

### With JavaFX Media (Original)
```
┌─────────────────────────────┐
│   [Embedded Video Player]   │
│                             │
│  ▶ Play  ⏸ Pause  ⏹ Stop  │
└─────────────────────────────┘
```

### Without JavaFX Media (Alternative)
```
┌─────────────────────────────┐
│ Video File: course_xxx.mp4  │
│ Click to open in player     │
│                             │
│      [▶ Open Video]         │
└─────────────────────────────┘
```

## ✅ Advantages of Alternative

1. **Universal Format Support** - Works with ANY video format
2. **No Dependencies** - No need for javafx-media
3. **User's Preferred Player** - Uses VLC, Windows Media Player, etc.
4. **Better Codec Support** - System player handles all codecs
5. **Simpler Code** - Less complexity

## ⚠️ Disadvantages

1. **Not Embedded** - Opens in separate window
2. **No In-App Controls** - Can't control from within app
3. **User Experience** - Slightly less seamless

## 🎬 User Experience

### Video Viewing Flow
```
User clicks "Open Video"
    ↓
System default player opens
    ↓
Video plays in VLC/Windows Media Player
    ↓
User watches video
    ↓
User closes player
    ↓
Returns to Course Catalog
```

## 📊 Comparison

| Feature | JavaFX Media | Alternative |
|---------|--------------|-------------|
| Embedded Player | ✅ Yes | ❌ No |
| All Formats | ❌ Limited | ✅ All |
| Dependencies | ⚠️ javafx-media | ✅ None |
| Codec Issues | ⚠️ Possible | ✅ None |
| User Control | ✅ In-app | ⚠️ External |
| Complexity | ⚠️ Higher | ✅ Lower |

## 🚀 Recommendation

1. **Try JavaFX Media first** (reload Maven dependencies)
2. **If it works** - Great! You get embedded player
3. **If it doesn't work** - Use alternative (still fully functional)

## 🎉 Both Work Great!

Either implementation provides a complete media viewing experience. Choose based on your needs and what works best in your environment.

---

**Current Status**: JavaFX Media dependency added to pom.xml  
**Next Step**: Reload Maven dependencies in IntelliJ  
**Fallback**: Use alternative implementation if needed
