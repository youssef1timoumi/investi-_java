# YouTube API Quick Start

## 🚀 5-Minute Setup

### Step 1: Get API Key (2 minutes)
1. Go to: https://console.cloud.google.com/
2. Create new project: "Connections3a8-LMS"
3. Enable "YouTube Data API v3"
4. Create credentials → API key
5. Copy your API key

### Step 2: Add API Key (1 minute)
1. Open: `src/main/java/edu/connections3a8/services/YouTubeApiService.java`
2. Find line 18:
   ```java
   private static final String API_KEY = "YOUR_API_KEY_HERE";
   ```
3. Replace with your key:
   ```java
   private static final String API_KEY = "AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxx";
   ```
4. Save file

### Step 3: Reload Maven (2 minutes)
```bash
mvn clean install
```

Or in IntelliJ: Right-click `pom.xml` → Maven → Reload project

### Step 4: Test It!
```bash
mvn test -Dtest=YouTubeApiServiceTest
```

## ✅ What You Get

When you paste a YouTube URL in the course form, the API will:
- ✅ Auto-fill video title
- ✅ Auto-fill description
- ✅ Calculate duration automatically
- ✅ Check if video can be embedded
- ✅ Get high-quality thumbnail URL
- ✅ Show view count and stats

## 📝 Example Usage

```java
// Create service
YouTubeApiService service = new YouTubeApiService();

// Get video info
YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo("dQw4w9WgXcQ");

// Use the data
System.out.println("Title: " + info.getTitle());
System.out.println("Duration: " + info.getDurationInMinutes() + " minutes");
System.out.println("Views: " + info.getViewCount());
System.out.println("Can embed: " + info.isEmbeddable());
System.out.println("Thumbnail: " + info.getThumbnailUrl());
```

## 🎯 What's an API?

**API = Application Programming Interface**

Think of it like a waiter at a restaurant:
- You (your app) ask the waiter (API) for something
- The waiter goes to the kitchen (YouTube servers)
- The waiter brings back your food (video data)

### Before API (Manual)
```
1. Copy YouTube URL
2. Open YouTube in browser
3. Copy video title manually
4. Copy description manually
5. Check duration manually
6. Paste everything into form
```

### After API (Automatic)
```
1. Paste YouTube URL
2. API fetches everything automatically
3. Form fills itself!
```

## 📊 API Limits

- **Free**: 10,000 requests per day
- **Cost**: $0
- **Perfect for**: Your project!

Each video info request = 1 unit
So you can fetch 10,000 videos per day for free.

## 🔧 Troubleshooting

### "API key not configured"
→ Add your API key to YouTubeApiService.java

### "400 Bad Request"
→ Check your API key is correct

### "403 Quota Exceeded"
→ Wait until tomorrow (quota resets daily)

### Maven dependencies not downloading
```bash
mvn dependency:purge-local-repository
mvn clean install
```

## 📚 Full Documentation

For detailed setup instructions, see:
- [YOUTUBE_API_SETUP_GUIDE.md](YOUTUBE_API_SETUP_GUIDE.md) - Complete guide
- [YOUTUBE_INTEGRATION_FEATURE.md](YOUTUBE_INTEGRATION_FEATURE.md) - Embed feature

## 🎉 Next Steps

After setup:
1. Test the API with YouTubeApiServiceTest
2. I'll integrate it into CourseController
3. Auto-fill will work when you paste YouTube URLs
4. Enjoy automatic video info fetching!

## 💡 Real API vs Embed

| Feature | Embed (Current) | API (New) |
|---------|----------------|-----------|
| Play videos | ✅ Yes | ❌ No |
| Get video title | ❌ No | ✅ Yes |
| Get description | ❌ No | ✅ Yes |
| Get duration | ❌ No | ✅ Yes |
| Check embeddable | ❌ No | ✅ Yes |
| Get thumbnail | ❌ No | ✅ Yes |
| API key needed | ❌ No | ✅ Yes |
| Cost | Free | Free (10k/day) |

**Best approach**: Use BOTH!
- API: Fetch video info when creating course
- Embed: Play video when viewing course

## 🔐 Security Note

**Never commit your API key to Git!**

Add to `.gitignore`:
```
**/YouTubeApiService.java
```

Or use environment variables:
```java
private static final String API_KEY = System.getenv("YOUTUBE_API_KEY");
```

Then set environment variable:
```bash
# Windows
set YOUTUBE_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Linux/Mac
export YOUTUBE_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## ✨ Summary

You now have a REAL API integrated! This is what companies use in production apps. The YouTube Data API will make your course creation much faster and more professional.

Ready to set it up? Follow the 3 steps above and you'll be done in 5 minutes!
