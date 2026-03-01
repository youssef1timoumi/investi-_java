# YouTube Data API Setup Guide

## Overview
This guide shows you how to integrate the YouTube Data API v3 into your project to automatically fetch video information.

## What the API Does

With the YouTube Data API, you can:
- ✅ Get video title automatically
- ✅ Get video description
- ✅ Get video thumbnail (high quality)
- ✅ Get video duration
- ✅ Check if video allows embedding
- ✅ Get view count and likes
- ✅ Get channel information

## Step 1: Get Your API Key

### 1.1 Go to Google Cloud Console
Visit: https://console.cloud.google.com/

### 1.2 Create a New Project
1. Click "Select a project" at the top
2. Click "NEW PROJECT"
3. Name it: "Connections3a8-LMS" (or any name)
4. Click "CREATE"

### 1.3 Enable YouTube Data API
1. In the left menu, go to "APIs & Services" → "Library"
2. Search for "YouTube Data API v3"
3. Click on it
4. Click "ENABLE"

### 1.4 Create API Key
1. Go to "APIs & Services" → "Credentials"
2. Click "CREATE CREDENTIALS" → "API key"
3. Your API key will be generated
4. **IMPORTANT**: Copy this key immediately!
5. (Optional) Click "RESTRICT KEY" to add security:
   - Application restrictions: None (for desktop app)
   - API restrictions: Select "YouTube Data API v3"
   - Click "SAVE"

### 1.5 API Key Example
Your API key will look like this:
```
AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## Step 2: Add API Key to Your Project

### 2.1 Open YouTubeApiService.java
File location: `src/main/java/edu/connections3a8/services/YouTubeApiService.java`

### 2.2 Replace the API Key
Find this line:
```java
private static final String API_KEY = "YOUR_API_KEY_HERE";
```

Replace with your actual key:
```java
private static final String API_KEY = "AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```

### 2.3 Save the File

## Step 3: Update Maven Dependencies

The dependencies are already added to `pom.xml`. Just reload Maven:

### In IntelliJ IDEA:
1. Right-click on `pom.xml`
2. Select "Maven" → "Reload project"
3. Wait for dependencies to download

### In Command Line:
```bash
mvn clean install
```

## Step 4: Test the API

### 4.1 Create a Test Class

Create `src/test/java/edu/connections3a8/services/YouTubeApiServiceTest.java`:

```java
package edu.connections3a8.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YouTubeApiServiceTest {
    
    @Test
    public void testGetVideoInfo() {
        YouTubeApiService service = new YouTubeApiService();
        
        // Test with a known video ID
        String videoId = "jNQXAC9IVRw"; // "Me at the zoo" - first YouTube video
        
        YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo(videoId);
        
        assertNotNull(info, "Video info should not be null");
        assertEquals(videoId, info.getVideoId());
        assertNotNull(info.getTitle());
        assertNotNull(info.getThumbnailUrl());
        
        System.out.println("Video Title: " + info.getTitle());
        System.out.println("Channel: " + info.getChannelTitle());
        System.out.println("Duration: " + info.getDurationInMinutes() + " minutes");
        System.out.println("Views: " + info.getViewCount());
        System.out.println("Embeddable: " + info.isEmbeddable());
        System.out.println("Thumbnail: " + info.getThumbnailUrl());
    }
}
```

### 4.2 Run the Test
```bash
mvn test -Dtest=YouTubeApiServiceTest
```

### 4.3 Expected Output
```
Video Title: Me at the zoo
Channel: jawed
Duration: 0 minutes
Views: 280000000+
Embeddable: true
Thumbnail: https://i.ytimg.com/vi/jNQXAC9IVRw/maxresdefault.jpg
```

## Step 5: Integrate with Course Form

Now let's update the Course Form to use the API when you paste a YouTube URL.

### 5.1 Update CourseController

I'll add a method to fetch video info when YouTube URL is detected:

```java
@FXML
private void handleYouTubeUrlPaste() {
    String url = contentUrlField.getText();
    
    if (isYouTubeUrl(url)) {
        String videoId = extractYouTubeVideoId(url);
        
        if (videoId != null) {
            // Show loading indicator
            statusLabel.setText("Fetching video info from YouTube...");
            
            // Fetch in background thread
            new Thread(() -> {
                YouTubeApiService service = new YouTubeApiService();
                YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo(videoId);
                
                if (info != null) {
                    // Update UI on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        // Auto-fill title if empty
                        if (titleField.getText().isEmpty()) {
                            titleField.setText(info.getTitle());
                        }
                        
                        // Auto-fill description if empty
                        if (descriptionArea.getText().isEmpty()) {
                            descriptionArea.setText(info.getDescription());
                        }
                        
                        // Auto-fill duration
                        durationField.setText(String.valueOf(info.getDurationInMinutes()));
                        
                        // Show embedding status
                        if (!info.isEmbeddable()) {
                            statusLabel.setText("⚠️ Warning: This video cannot be embedded");
                            statusLabel.setStyle("-fx-text-fill: #DC3545;");
                        } else {
                            statusLabel.setText("✅ Video info loaded successfully");
                            statusLabel.setStyle("-fx-text-fill: #28A745;");
                        }
                        
                        // Show video stats
                        System.out.println("Video: " + info.getTitle());
                        System.out.println("Views: " + info.getViewCount());
                        System.out.println("Duration: " + info.getDurationInMinutes() + " min");
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("❌ Could not fetch video info");
                        statusLabel.setStyle("-fx-text-fill: #DC3545;");
                    });
                }
            }).start();
        }
    }
}
```

## API Usage Examples

### Example 1: Get Video Info
```java
YouTubeApiService service = new YouTubeApiService();
YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo("dQw4w9WgXcQ");

System.out.println("Title: " + info.getTitle());
System.out.println("Duration: " + info.getDurationInMinutes() + " minutes");
System.out.println("Views: " + info.getViewCount());
System.out.println("Can embed: " + info.isEmbeddable());
```

### Example 2: Check if Video is Embeddable
```java
YouTubeApiService service = new YouTubeApiService();
String videoId = extractYouTubeVideoId(url);
YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo(videoId);

if (info != null && !info.isEmbeddable()) {
    showWarning("This video cannot be embedded. It will open in browser.");
}
```

### Example 3: Auto-Download Thumbnail
```java
YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo(videoId);
String thumbnailUrl = info.getThumbnailUrl();

// Download thumbnail
URL url = new URL(thumbnailUrl);
InputStream in = url.openStream();
Files.copy(in, Paths.get("media/thumbnails/thumb_" + videoId + ".jpg"));
```

## API Quota and Limits

### Free Tier
- **Daily Quota**: 10,000 units per day
- **Cost per video info request**: 1 unit
- **You can make**: ~10,000 requests per day

### Quota Usage
- Getting video info: 1 unit
- Searching videos: 100 units
- Listing playlists: 1 unit

### Tips to Save Quota
1. Cache video info in database
2. Only fetch when URL changes
3. Don't fetch on every page load
4. Store thumbnail URLs instead of re-fetching

## Error Handling

### Common Errors

#### 1. API Key Not Set
```
Error: YouTube API key not configured
Solution: Set your API key in YouTubeApiService.java
```

#### 2. Invalid API Key
```
Error: 400 Bad Request
Solution: Check your API key is correct
```

#### 3. Quota Exceeded
```
Error: 403 Forbidden - quotaExceeded
Solution: Wait until tomorrow or upgrade to paid plan
```

#### 4. Video Not Found
```
Error: No video found with ID
Solution: Check the video ID is correct and video exists
```

#### 5. Video is Private
```
Error: Video is private
Solution: API cannot access private videos
```

## Security Best Practices

### 1. Don't Commit API Key to Git
Add to `.gitignore`:
```
# API Keys
**/YouTubeApiService.java
```

Or use environment variables:
```java
private static final String API_KEY = System.getenv("YOUTUBE_API_KEY");
```

### 2. Restrict API Key
In Google Cloud Console:
- Restrict to YouTube Data API v3 only
- Add IP restrictions if possible
- Regenerate key if exposed

### 3. Monitor Usage
- Check quota usage in Google Cloud Console
- Set up alerts for high usage
- Review API logs regularly

## Benefits of Using the API

### Before API
- Manual entry of video title
- Manual entry of description
- Manual entry of duration
- No way to check if embeddable
- No thumbnail auto-download

### After API
- ✅ Auto-fill video title
- ✅ Auto-fill description
- ✅ Auto-calculate duration
- ✅ Check embedding permissions
- ✅ Get high-quality thumbnails
- ✅ Show view count and stats
- ✅ Better user experience

## Next Steps

1. ✅ Get API key from Google Cloud Console
2. ✅ Add key to YouTubeApiService.java
3. ✅ Reload Maven dependencies
4. ✅ Test with YouTubeApiServiceTest
5. ⏳ Integrate with CourseController (I'll do this next)
6. ⏳ Add auto-fill functionality
7. ⏳ Add thumbnail download
8. ⏳ Cache video info in database

## Troubleshooting

### Maven Dependencies Not Downloading
```bash
# Clear Maven cache
mvn dependency:purge-local-repository

# Reload
mvn clean install
```

### API Not Working
1. Check API key is correct
2. Verify YouTube Data API v3 is enabled
3. Check internet connection
4. Review console for error messages
5. Test with curl:
```bash
curl "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=jNQXAC9IVRw&key=YOUR_API_KEY"
```

### Quota Issues
- Check usage: https://console.cloud.google.com/apis/api/youtube.googleapis.com/quotas
- Upgrade to paid plan if needed
- Implement caching to reduce requests

## Cost Information

### Free Tier
- **Cost**: $0
- **Quota**: 10,000 units/day
- **Requests**: ~10,000 video info requests/day
- **Perfect for**: Development and small apps

### Paid Tier
- **Cost**: $0 for first 10,000 units
- **Additional**: Contact Google for pricing
- **Unlimited**: Available for enterprise

## Support Resources

- **API Documentation**: https://developers.google.com/youtube/v3
- **API Explorer**: https://developers.google.com/youtube/v3/docs/videos/list
- **Stack Overflow**: Tag `youtube-api`
- **Google Cloud Support**: https://cloud.google.com/support

## Summary

You now have a real API integrated into your project! The YouTube Data API will:
- Automatically fetch video information
- Save time on manual data entry
- Provide better user experience
- Check embedding permissions
- Get high-quality thumbnails

Next, I'll integrate this into your CourseController to auto-fill form fields when you paste a YouTube URL.
