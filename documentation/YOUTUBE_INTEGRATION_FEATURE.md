# YouTube Video Integration Feature

## Overview
Integrated YouTube video playback directly in the course content page using JavaFX WebView. No API key required for basic video playback.

## Features

### 1. **YouTube URL Detection**
- Automatically detects YouTube URLs in course content
- Supports multiple YouTube URL formats:
  - `https://www.youtube.com/watch?v=VIDEO_ID`
  - `https://youtu.be/VIDEO_ID`
  - `https://www.youtube.com/embed/VIDEO_ID`

### 2. **Embedded Video Player**
- Uses YouTube's embed player with youtube-nocookie.com domain for better privacy
- Full video controls (play, pause, volume, fullscreen)
- Responsive design with proper aspect ratio
- Clean, modern interface with dark mode support
- Improved HTML structure for better compatibility

### 3. **Video ID Extraction**
- Automatically extracts video ID from various URL formats
- Handles URL parameters and fragments
- Error handling for invalid URLs

### 4. **Error Handling & Fallback**
- Detects embedding restrictions (Error 153)
- Provides "Open in Browser" button as fallback
- User-friendly error messages
- Info label warning about potential restrictions
- Graceful degradation when embedding fails

### 5. **Seamless Integration**
- Works alongside local video files
- Works alongside PDF files
- Automatic content type detection
- No additional configuration needed

## How to Use

### For Course Creators:

1. **Add YouTube Video to Course**:
   - Go to Course Form
   - In "Content URL" field, paste any YouTube video URL
   - Examples:
     ```
     https://www.youtube.com/watch?v=dQw4w9WgXcQ
     https://youtu.be/dQw4w9WgXcQ
     ```

2. **Save Course**:
   - The system automatically detects it's a YouTube URL
   - No special configuration needed

3. **View Course**:
   - Open course content page
   - YouTube video will be embedded and ready to play

### For Students:

1. **Access Course**:
   - Navigate to course catalog
   - Click "Course Content" on any course

2. **Watch Video**:
   - If course has YouTube video, it will be embedded
   - Use standard YouTube controls:
     - Play/Pause
     - Volume control
     - Fullscreen mode
     - Quality settings
     - Playback speed

3. **If Video Doesn't Load**:
   - Some videos have embedding restrictions
   - Click "🌐 Open in Browser" button to watch on YouTube
   - This opens the video in your default web browser

## Technical Implementation

### Files Modified

#### Controller
- `src/main/java/edu/connections3a8/controllers/CourseContentController.java`
  - Added `isYouTubeUrl()` - Detects YouTube URLs
  - Added `extractYouTubeVideoId()` - Extracts video ID from URL
  - Added `loadYouTubeVideo()` - Embeds YouTube player
  - Updated `loadMedia()` - Checks for YouTube URLs first

### Key Methods

#### isYouTubeUrl(String url)
```java
private boolean isYouTubeUrl(String url) {
    return url.contains("youtube.com") || url.contains("youtu.be");
}
```
Checks if URL is from YouTube.

#### extractYouTubeVideoId(String url)
Extracts video ID from various YouTube URL formats:
- Standard: `youtube.com/watch?v=VIDEO_ID`
- Short: `youtu.be/VIDEO_ID`
- Embed: `youtube.com/embed/VIDEO_ID`

#### loadYouTubeVideo(String youtubeUrl)
- Creates JavaFX WebView with proper sizing (800x450)
- Generates HTML with YouTube embed iframe using youtube-nocookie.com
- Includes comprehensive error handling
- Provides fallback "Open in Browser" button
- Displays warning about potential embedding restrictions
- Loads content into WebView with proper styling
- Supports both light and dark modes
- Displays in course content page with clean UI

## Supported URL Formats

### Standard YouTube URL
```
https://www.youtube.com/watch?v=dQw4w9WgXcQ
https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=30s
```

### Short YouTube URL
```
https://youtu.be/dQw4w9WgXcQ
https://youtu.be/dQw4w9WgXcQ?t=30
```

### Embed URL
```
https://www.youtube.com/embed/dQw4w9WgXcQ
```

## Embed Features

### YouTube Player Parameters
- `autoplay=0` - Video doesn't auto-play
- `rel=0` - Minimizes related videos
- `modestbranding=1` - Minimal YouTube branding
- `enablejsapi=1` - Enables JavaScript API for better control

### Domain
- Uses `youtube-nocookie.com` for better privacy
- Reduces tracking and cookies
- Same functionality as regular YouTube embed

### Permissions
- Accelerometer
- Autoplay (user-initiated)
- Clipboard write
- Encrypted media
- Gyroscope
- Picture-in-picture
- Web share

### Styling
- Responsive container with proper aspect ratio
- Dark mode support
- Clean, modern design
- Rounded corners and proper spacing
- Video ID display for debugging

## Benefits

### No API Key Required
- Uses YouTube's public embed player
- No quota limits
- No API setup needed
- Free to use

### Full YouTube Features
- HD/4K quality (if available)
- Subtitles/Captions
- Playback speed control
- Theater/Fullscreen mode
- Mobile responsive

### Privacy
- No tracking beyond YouTube's standard embed
- No additional data collection
- Respects YouTube's privacy settings

## Content Type Priority

The system checks content in this order:
1. **YouTube URL** - If URL contains youtube.com or youtu.be
2. **Local Video File** - If file exists and is video format
3. **PDF File** - If file exists and is .pdf
4. **External URL** - Any other URL opens in browser

## Example Usage

### Create Course with YouTube Video

1. **Course Form**:
   ```
   Title: Introduction to Java Programming
   Content URL: https://www.youtube.com/watch?v=eIrMbAQSU34
   Content Type: video
   ```

2. **Result**:
   - Course content page shows embedded YouTube player
   - Students can watch directly in the app
   - Full YouTube controls available

### Mixed Content Course

You can have multiple courses with different content types:
- Course 1: YouTube video
- Course 2: Local MP4 file
- Course 3: PDF document
- Course 4: External website link

All work seamlessly!

## Error Handling

### Invalid YouTube URL
- Shows error message: "Invalid YouTube URL format"
- Provides clear feedback to user
- Displays video ID extraction failure

### Embedding Restrictions (Error 153)
- Some videos cannot be embedded due to creator settings
- System detects this and shows user-friendly error
- Provides "Watch on YouTube" button as alternative
- Includes warning message about embedding restrictions
- Graceful fallback ensures users can still access content

### Network Issues
- YouTube player handles network errors
- Shows standard YouTube error messages
- Retry functionality built into YouTube player

### WebView Errors
- Catches exceptions during WebView creation
- Shows fallback error container with browser link
- Ensures app doesn't crash on video load failure

### Unsupported Browsers
- JavaFX WebView supports modern web standards
- YouTube embed works on all platforms
- HTML5 video player used by default

## Future Enhancements

### Potential Additions
1. **Playlist Support**:
   - Embed entire YouTube playlists
   - Sequential video playback

2. **Video Analytics**:
   - Track watch time
   - Monitor completion rates
   - Student engagement metrics

3. **YouTube API Integration**:
   - Fetch video metadata (title, description, duration)
   - Get video thumbnails automatically
   - Search YouTube videos from within app

4. **Advanced Features**:
   - Start at specific timestamp
   - Loop videos
   - Custom player controls
   - Download transcripts

5. **Other Video Platforms**:
   - Vimeo support
   - Dailymotion support
   - Custom video hosting

## Testing

### Test Scenarios

1. **Standard YouTube URL**:
   ```
   https://www.youtube.com/watch?v=dQw4w9WgXcQ
   ```
   Expected: Video plays in embedded player

2. **Short YouTube URL**:
   ```
   https://youtu.be/dQw4w9WgXcQ
   ```
   Expected: Video plays in embedded player

3. **Embedding Restricted Video**:
   - Some videos show Error 153
   - Expected: Error message with "Open in Browser" button
   - Click button to watch on YouTube

4. **Invalid URL**:
   ```
   https://www.youtube.com/invalid
   ```
   Expected: Error message displayed

5. **Local Video File**:
   ```
   media/courses/course_123.mp4
   ```
   Expected: Local video player loads

6. **Mixed Content**:
   - Create courses with different content types
   - Verify each loads correctly

7. **Dark Mode**:
   - Toggle dark mode
   - Verify video container styling updates
   - Check button colors and text

## Recent Fixes

### Error 153 Resolution (March 2026)
**Problem**: Users reported "Erreur 153 - Erreur de configuration du lecteur vidéo"

**Root Cause**: Some YouTube videos have embedding restrictions set by their creators

**Solution Implemented**:
1. Improved HTML structure with better iframe configuration
2. Switched to `youtube-nocookie.com` domain for better compatibility
3. Added fallback "Open in Browser" button for restricted videos
4. Included warning message about potential embedding restrictions
5. Enhanced error handling with user-friendly messages
6. Styled container with proper dark mode support
7. Added video ID display for debugging

**Result**: Users can now either watch embedded videos OR open them in browser if embedding is restricted

## Notes

- **Internet Required**: YouTube videos require internet connection
- **YouTube Terms**: Comply with YouTube's Terms of Service
- **Content Rights**: Ensure you have rights to embed videos
- **Embedding Restrictions**: Some videos cannot be embedded due to creator settings (Error 153)
- **Fallback Available**: "Open in Browser" button provided for restricted videos
- **Privacy**: Uses youtube-nocookie.com domain for better privacy
- **Accessibility**: YouTube player includes accessibility features
- **Mobile**: Works on desktop; mobile support depends on JavaFX platform
- **Dark Mode**: Full dark mode support for video container and controls

## Troubleshooting

### Video Not Loading (Error 153)
**Problem**: "Erreur 153 - Erreur de configuration du lecteur vidéo"

**Cause**: The video has embedding restrictions set by the creator

**Solutions**:
1. Click the "🌐 Open in Browser" button to watch on YouTube
2. The video owner has disabled embedding - this is not an app issue
3. Try a different video that allows embedding
4. Contact the video owner to enable embedding

### Other Video Loading Issues
1. Check internet connection
2. Verify YouTube URL is correct
3. Check if video is available in your region
4. Ensure video is not age-restricted
5. Try refreshing the page

### WebView Issues
1. Ensure JavaFX WebView is properly configured
2. Check Java version compatibility (Java 11+ recommended)
3. Verify WebView dependencies are installed
4. Check console for JavaScript errors

### Performance
- WebView may use more memory than native player
- Consider local files for offline access
- YouTube quality adapts to connection speed
- Close other browser tabs if performance is slow

## Color Palette
- Video Container Background: #000 (Black)
- Label Text (Light): #000501
- Label Text (Dark): #F0F2FA
- Error Text: #DC3545 (Red)
