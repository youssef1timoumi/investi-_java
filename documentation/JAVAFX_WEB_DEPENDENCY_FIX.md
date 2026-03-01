# 🔧 JavaFX Web Dependency Fix

## 🎯 Issue

The error `package javafx.scene.web does not exist` occurred because the `javafx-web` module was not included in the project dependencies.

## ✅ Solution Applied

Added the `javafx-web` dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-web</artifactId>
    <version>${javafx.version}</version>
</dependency>
```

## 📦 What is javafx-web?

The `javafx-web` module provides:
- **WebView** - Embedded web browser component
- **WebEngine** - HTML rendering engine
- **HTML/CSS/JavaScript** support
- Used for displaying web content and PDFs in JavaFX applications

## 🔄 Required Action: Reload Maven Dependencies

You need to reload Maven dependencies in IntelliJ IDEA to download the new module:

### Method 1: Maven Tool Window
1. Open **View** → **Tool Windows** → **Maven**
2. Click the **Reload All Maven Projects** icon (circular arrows) at the top of the Maven panel
3. Wait for dependencies to download

### Method 2: Right-Click pom.xml
1. Right-click on `pom.xml` in the Project Explorer
2. Select **Maven** → **Reload Project**
3. Wait for dependencies to download

### Method 3: Keyboard Shortcut
1. Press **Ctrl+Shift+O** (Windows/Linux) or **Cmd+Shift+I** (Mac)
2. This triggers Maven reimport
3. Wait for dependencies to download

## 📋 Current JavaFX Dependencies

After the fix, your project now has these JavaFX modules:

1. **javafx-controls** - UI controls (buttons, labels, etc.)
2. **javafx-fxml** - FXML support for UI layouts
3. **javafx-media** - Video and audio player
4. **javafx-web** - WebView for HTML/PDF display ✨ NEW!

## 🎯 Usage in Project

The `javafx-web` module is used in:

### CourseContentController.java

```java
import javafx.scene.web.WebView;

private void loadPDFViewer(File pdfFile) {
    WebView webView = new WebView();
    webView.setPrefHeight(600);
    webView.setPrefWidth(800);
    
    String pdfUrl = pdfFile.toURI().toString();
    webView.getEngine().load(pdfUrl);
    
    // Display PDF in embedded viewer
}
```

## 🔍 Verification

After reloading Maven dependencies, verify the fix:

1. **Check Maven Dependencies**
   - Open Maven tool window
   - Expand **Dependencies**
   - Look for `javafx-web-21.0.1-win.jar` (or your version)

2. **Check for Errors**
   - Open `CourseContentController.java`
   - The import `javafx.scene.web.WebView` should have no red underline
   - No compilation errors

3. **Build Project**
   - Run **Build** → **Rebuild Project**
   - Should complete without errors

## 📊 Dependency Details

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-web</artifactId>
    <version>21.0.1</version> <!-- or your javafx.version -->
</dependency>
```

**Maven Coordinates:**
- **Group ID**: org.openjfx
- **Artifact ID**: javafx-web
- **Version**: Matches your `${javafx.version}` property (likely 21.0.1)

**Download Size:**
- Approximately 15-20 MB
- Platform-specific (Windows, Mac, Linux)

## 🚀 What This Enables

With `javafx-web` dependency added, you can now:

### 1. Embed PDFs in Course Content Page
- Display PDFs directly in the application
- 600px height viewer
- Scrollable content
- No need to open external viewer

### 2. Display HTML Content
- Render HTML pages
- Support CSS styling
- Execute JavaScript (if needed)

### 3. Web-Based Content
- Embed web pages
- Display documentation
- Show interactive content

## ⚠️ Important Notes

### Browser Engine
- WebView uses **WebKit** rendering engine
- Same engine as Safari browser
- Good HTML5/CSS3 support
- Limited PDF support (depends on platform)

### PDF Display Limitations
- **Windows**: May not display PDFs natively in WebView
- **Mac**: Better PDF support in WebView
- **Linux**: Varies by distribution

### Fallback Strategy
The code includes a fallback:
```java
try {
    // Try to load PDF in WebView
    webView.getEngine().load(pdfUrl);
} catch (Exception e) {
    // Fallback: Show "Open in External Viewer" button
}
```

## 🔧 Troubleshooting

### Issue: Dependency Not Downloading

**Solution:**
1. Check internet connection
2. Clear Maven cache: Delete `~/.m2/repository/org/openjfx/javafx-web`
3. Reload Maven project again

### Issue: Wrong Platform Dependency

**Solution:**
Maven should auto-detect your platform. If not:
```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-web</artifactId>
    <version>21.0.1</version>
    <classifier>win</classifier> <!-- or mac, linux -->
</dependency>
```

### Issue: Version Mismatch

**Solution:**
Ensure all JavaFX dependencies use the same version:
```xml
<properties>
    <javafx.version>21.0.1</javafx.version>
</properties>
```

## 📚 Related Documentation

- [JavaFX WebView Documentation](https://openjfx.io/javadoc/21/javafx.web/javafx/scene/web/WebView.html)
- [OpenJFX Official Site](https://openjfx.io/)
- `COURSE_CONTENT_PAGE_FEATURE.md` - Feature using WebView
- `MAVEN_DEPENDENCY_FIX.md` - Previous dependency fix

## ✅ Checklist

After applying this fix:

- [x] Added `javafx-web` dependency to pom.xml
- [ ] Reload Maven dependencies in IntelliJ
- [ ] Verify no compilation errors
- [ ] Build project successfully
- [ ] Test PDF viewer in Course Content page

## 🎉 Ready!

Once you reload Maven dependencies, the `javafx.scene.web` package will be available and the PDF viewer will work!

---

**Status**: ✅ Dependency added to pom.xml  
**Action Required**: Reload Maven dependencies in IntelliJ  
**Expected Result**: No compilation errors, PDF viewer functional
