# INVESTI Forum Module

Community forum for the INVESTI platform. JavaFX desktop app with MySQL backend.

## Quick Start

### Prerequisites
- Java 17+
- Maven
- MySQL (database name: `3a8`)

### Setup
1. Run `src/main/resources/forum_schema.sql` in your MySQL to create all tables (includes sample users)
2. Copy `.env.example` to `.env` and set your database credentials
3. Run: `mvn javafx:run`

Entry point: `edu.connexion3a8.ForumApp`

---

## Database

Database: `3a8` (shared with other modules)

### Tables

| Table | Purpose |
|---|---|
| `users` | Shared user table (managed by user management module — forum reads only, never writes) |
| `forum_posts` | Posts with title, content, category, votes, views |
| `forum_post_images` | Multiple images per post |
| `forum_comments` | Threaded comments with nested replies (via `parent_comment_id`) |
| `forum_post_votes` | One vote per user per post (upvote/downvote) |
| `forum_comment_votes` | One vote per user per comment |
| `forum_post_views` | Unique view tracking per user |
| `forum_bookmarks` | Saved/bookmarked posts per user (auto-created on startup if missing) |
| `forum_notifications` | @mention notifications (auto-created on startup if missing) |

All IDs are UUID strings (`CHAR(36)`).

### Users Table Dependency

The forum reads from the `users` table but **never writes to it**. Expected columns:

```sql
users (
  id CHAR(36) PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255),
  role VARCHAR(50),       -- 'admin', 'investor', 'innovator', etc.
  avatar_url VARCHAR(500),
  is_active BOOLEAN
  -- other columns are ignored by the forum
)
```

The forum joins `users.id` with `forum_posts.user_id` and `forum_comments.user_id` to get author names.

---

## Architecture

```
edu.connexion3a8/
├── ForumApp.java                    # JavaFX Application entry point
├── controllers/
│   └── ForumController.java         # Main UI controller (all UI logic, FXML bindings)
├── entities/
│   ├── ForumPost.java               # Post entity (id, userId, title, content, category, votes, views, images)
│   ├── ForumComment.java            # Comment entity (supports nested replies via parentCommentId)
│   ├── ForumPostVote.java           # Post vote entity
│   └── ForumCommentVote.java        # Comment vote entity
├── services/
│   └── ForumPostService.java        # All DB operations (CRUD, votes, bookmarks, notifications, search)
└── tools/
    ├── MyConnection.java            # MySQL connection (reads DB_URL, DB_USER, DB_PASSWORD from .env)
    ├── EnvConfig.java               # .env file loader (system env > .env file)
    ├── ThemeManager.java            # Dark/Light theme state + color getters for inline styles
    ├── BadWordsFilter.java          # Profanity filter (blocks posts/comments with bad words)
    ├── MentionParser.java           # @username mention parsing → styled TextFlow with blue highlights
    ├── TranslationService.java      # Translation via MyMemory API (EN/FR/AR, free, no key needed)
    └── SummarizationService.java    # AI summary via Groq API → AWS Bedrock → local TextRank fallback
```

Resources:
```
src/main/resources/
├── Forum.fxml              # Main UI layout (BorderPane: left sidebar + center feed)
├── styles.css              # Dark theme (default)
├── styles-light.css        # Light theme overlay (added/removed from scene stylesheets)
├── forum_schema.sql        # Full database schema + sample users
├── insert_users.sql        # Additional test users
└── INVESTI.png             # Logo
```

---

## Features

### Core Forum
- Create, edit, delete posts (with title, content, category, optional images)
- Threaded comments with nested replies (unlimited depth)
- Reddit-style upvote/downvote on posts and comments
- Unique view tracking per user (one view per user per post)
- Full-text search across post titles and content
- Category filtering: General, Tips & Advice, Success Stories, Investor Insights, Collaboration, Announcements
- Tabs: Home (all posts), My Posts, Activity (posts you voted/commented on/were mentioned in), Saved (bookmarks)
- Admin moderation: admin-role users can delete any post or comment

### Feature 1 — Dark/Light Mode Toggle
- `ThemeManager.java` holds the current theme state (DARK or LIGHT) and provides color getters
- Toggle button in the sidebar switches between modes
- CSS approach: `styles.css` (dark, always loaded) + `styles-light.css` (added/removed from scene)
- Inline styles use `ThemeManager.bg()`, `ThemeManager.text()`, etc. so they update on toggle
- All dialogs register their Scene via `ThemeManager.registerScene(scene)` for automatic theme application
- ComboBox button cells use `setButtonCell()` for reliable theme switching
- TextArea/TextField use `-fx-control-inner-background` to force internal node colors

### Feature 2 — Bookmarks / Saved Posts
- `forum_bookmarks` table (auto-created on startup via `ensureBookmarksTable()`)
- 🔖 Save/Unsave button on each post card
- "Saved" tab in sidebar shows bookmarked posts
- `ForumPostService` methods: `toggleBookmark()`, `addBookmark()`, `removeBookmark()`, `isBookmarked()`, `getBookmarkedPosts()`

### Feature 3 — @username Mention Tagging with Notifications
- `MentionParser.java` extracts `@username` patterns from text
- Mentions rendered as blue highlighted text in posts and comments
- Pattern: `@(\w+(\s\w+)?)` — supports multi-word names like "Mohamed Taha"
- When a user is @mentioned in a post or comment, a notification is created in `forum_notifications`
- The mentioned user sees a 🔔 notification bell badge in the header with unread count
- Clicking the bell opens a notification popup listing all unread mentions
- Clicking a notification opens the post where the user was tagged and marks it as read
- "Mark all read" button clears all unread notifications
- Posts where the user was @mentioned appear in the "Activity" tab
- `MentionParser.createStyledText()` returns a `TextFlow` with styled `Text` nodes
- Mention Text nodes have `-fx-cursor: hand` — ready for click-to-navigate integration
- `ForumPostService` methods: `findUserIdByName()`, `createMentionNotification()`, `getUnreadNotifications()`, `getUnreadNotificationCount()`, `markNotificationRead()`, `markAllNotificationsRead()`, `getPostsWhereMentioned()`

### Feature 4 — Share to Social Media
- Share `MenuButton` on each post with options: Facebook, X (Twitter), LinkedIn, Copy Text
- Shares the full text content of the post (not a URL link)
- X/Twitter: opens tweet composer with text (truncated to 280 chars)
- Facebook/LinkedIn: copies text to clipboard and opens the platform
- Copy Text: copies post content to clipboard

### Translation (MyMemory API)
- Translate any post to English, French, or Arabic
- Uses MyMemory API (free, no API key needed)
- Auto-detects source language (Arabic chars, French patterns, default English)
- Shows original + translated text side by side in a dialog

### AI Summarization (TL;DR)
- "✨ TL;DR" button appears on posts with 200+ characters
- Priority chain: Groq API (free tier) → AWS Bedrock → local TextRank
- Shows loading spinner, then original excerpt + AI summary in a styled dialog

### Bad Words Filter
- `BadWordsFilter.java` blocks posts and comments containing profanity
- Uses word-boundary regex matching (no false positives like "class" matching "ass")
- Configurable word list in `BLOCKED_WORDS` array

---

## Integration Guide

### 1. Integrate with User Management (Authentication)

The forum currently uses a **user selector dropdown** for testing. To connect your auth system:

In `ForumController.java`, find `loadUsers()` and `setCurrentUser()`. Replace with:

```java
// Set the authenticated user directly:
this.currentUserId = yourAuthService.getCurrentUserId();   // CHAR(36) UUID
this.currentUserName = yourAuthService.getCurrentUserName();
this.currentUserRole = yourAuthService.getCurrentUserRole(); // "admin", "investor", etc.
updateNotificationBadge(); // Refresh notification count for this user
```

Then remove or hide the `userSelector` ComboBox from `Forum.fxml` (the VBox at the bottom of the sidebar).

The forum only needs these 3 fields: `currentUserId`, `currentUserName`, `currentUserRole`.

Admin detection: `currentUserRole.equals("admin")` — admins can delete any post/comment.

### 2. Integrate with Navigation

The forum is a single FXML view. To embed it:

**Option A — As a full scene:**
```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forum.fxml"));
Parent forumRoot = loader.load();
yourStage.setScene(new Scene(forumRoot, 1200, 800));
```

**Option B — Inside a container:**
```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forum.fxml"));
Parent forumRoot = loader.load();
yourBorderPane.setCenter(forumRoot);
```

### 3. Make @Mentions Interactive (Navigate to Profile)

In `MentionParser.createStyledText()`, the mention `Text` nodes already have `-fx-cursor: hand`. Add click handlers:

```java
mentionText.setOnMouseClicked(e -> {
    String username = mentionText.getText().substring(1); // remove @
    yourNavigationService.goToUserProfile(username);
});
```

### 4. @Mention Notifications — How They Work

When a user writes `@Mohamed Taha` in a post or comment:
1. `MentionParser.extractMentions()` extracts the name "Mohamed Taha"
2. `ForumPostService.findUserIdByName()` looks up the user ID by name (case-insensitive)
3. If the user exists and is not the author themselves, a notification row is inserted into `forum_notifications`
4. The mentioned user sees the notification badge update on their bell icon
5. The post appears in their "Activity" tab

The mention matching uses `users.name` (case-insensitive). Make sure your user names match what people type after `@`.

### 5. Theme Support for New Windows

If you open new windows/dialogs, register them for theme support:

```java
Scene scene = new Scene(root);
ThemeManager.registerScene(scene);
// On close:
ThemeManager.unregisterScene(scene);
```

### 6. Database Connection

`MyConnection.java` reads from `.env`:
```
DB_URL=jdbc:mysql://localhost:3306/3a8
DB_USER=root
DB_PASSWORD=
```

If your module uses a different connection approach, modify `MyConnection.java` or pass a `Connection` to `ForumPostService`.

---

## API Keys (Optional)

Set in `.env`:

| Key | Service | Purpose | Required? |
|---|---|---|---|
| `DB_URL` | MySQL | Database connection | Yes |
| `DB_USER` | MySQL | Database user | Yes |
| `DB_PASSWORD` | MySQL | Database password | Yes |
| `GROQ_API_KEY` | [Groq](https://console.groq.com) | AI post summarization (free tier) | No (falls back to local TextRank) |
| `BEDROCK_API_KEY` | AWS Bedrock | AI summarization backup | No |

Translation uses MyMemory API — free, no key needed.

---

## Key Classes Reference

| Class | What it does | Integration notes |
|---|---|---|
| `ForumController` | All UI logic, FXML bindings | Modify `loadUsers()`/`setCurrentUser()` for auth integration |
| `ForumPostService` | All DB operations (CRUD, votes, bookmarks, notifications, search) | Call its methods if building API endpoints |
| `MyConnection` | MySQL connection from `.env` | Adapt to your connection pooling if needed |
| `ThemeManager` | Static theme state + color getters | Call `registerScene(scene)` on new windows |
| `MentionParser` | @mention parsing → styled TextFlow | Add click handlers for user profile navigation |
| `BadWordsFilter` | Profanity detection | Add words to `BLOCKED_WORDS` array as needed |
| `TranslationService` | MyMemory API translation (EN/FR/AR) | No config needed, works out of the box |
| `SummarizationService` | AI summary (Groq → Bedrock → TextRank) | Set `GROQ_API_KEY` in `.env` for best results |
| `EnvConfig` | Loads `.env` file, system env takes priority | Used by MyConnection and SummarizationService |

---

## Build & Run

```bash
mvn compile          # Compile
mvn javafx:run       # Run the app
mvn test             # Run tests
mvn package          # Package
```
