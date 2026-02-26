# 🔧 Fix: JavaFX Media Dependency

## ❌ Problem

```
package javafx.scene.media does not exist
```

The JavaFX Media API (for video playback) requires a separate dependency that wasn't included.

## ✅ Solution

I've added the `javafx-media` dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-media</artifactId>
    <version>${javafx.version}</version>
</dependency>
```

## 🔄 Reload Maven Dependencies

### Option 1: IntelliJ IDEA (Recommended)

1. **Open Maven Tool Window**
   - View → Tool Windows → Maven
   - Or click the "Maven" tab on the right side

2. **Reload Project**
   - Click the "Reload All Maven Projects" button (🔄 icon)
   - Or right-click on your project → Maven → Reload Project

3. **Wait for Download**
   - IntelliJ will download `javafx-media-21.0.1.jar`
   - This may take a few seconds

4. **Verify**
   - The import errors should disappear
   - Build → Rebuild Project

### Option 2: Command Line (If Maven is installed)

```bash
mvn clean install -DskipTests
```

### Option 3: IntelliJ Maven Lifecycle

1. Open Maven tool window
2. Expand "Lifecycle"
3. Double-click "clean"
4. Double-click "install"

## 🎯 What This Dependency Provides

The `javafx-media` module includes:
- `javafx.scene.media.Media` - Represents media content
- `javafx.scene.media.MediaPlayer` - Controls media playback
- `javafx.scene.media.MediaView` - Displays video content

## 🔍 Verify Installation

After reloading, check that these imports work:

```java
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
```

## 📦 Alternative: If Media Doesn't Work

If you still have issues or don't want to use JavaFX Media, I can provide an alternative implementation that:
- Opens videos in the system default player (like VLC)
- Uses external links instead of embedded player
- Still supports PDF viewing

Let me know if you need this alternative!

## 🎬 Supported Video Formats

Once `javafx-media` is installed, it supports:
- **MP4** (H.264 + AAC) - Best support
- **FLV** (Flash Video)
- **HLS** (HTTP Live Streaming)

Note: Some formats like AVI, MKV may not work with JavaFX MediaPlayer. For those, the system player fallback is better.

## 🚀 Next Steps

1. **Reload Maven dependencies** (Option 1 above)
2. **Wait for download to complete**
3. **Rebuild project**
4. **Run application**
5. **Test video upload and playback**

## ✅ Success Indicators

You'll know it worked when:
- ✅ No import errors in CourseCatalogController.java
- ✅ Project builds successfully
- ✅ Can upload video files
- ✅ Video player appears in Course Catalog

## 🐛 Still Having Issues?

If the dependency doesn't download:
1. Check internet connection
2. Clear Maven cache: Delete `~/.m2/repository/org/openjfx/javafx-media`
3. Reload Maven projects again
4. Or use the alternative implementation (without embedded video)

---

**Status**: Dependency added to pom.xml ✅  
**Action Required**: Reload Maven dependencies in IntelliJ
