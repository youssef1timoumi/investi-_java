# INVESTI Forum Management Module

## Overview
This module implements a Reddit-style forum section for the INVESTI platform with:
- Posts with upvote/downvote system
- Nested comments with replies
- User activity tracking (My Posts, My Activity)
- Category filtering and search

## Project Structure

```
src/
├── main/
│   ├── java/edu/connexion3a8/
│   │   ├── controllers/
│   │   │   └── ForumController.java      # JavaFX controller for Forum UI
│   │   ├── entities/
│   │   │   ├── ForumPost.java            # Post entity
│   │   │   ├── ForumComment.java         # Comment entity with nested replies
│   │   │   ├── ForumPostVote.java        # Post vote tracking
│   │   │   └── ForumCommentVote.java     # Comment vote tracking
│   │   ├── services/
│   │   │   └── ForumPostService.java     # All CRUD operations for forum
│   │   ├── tools/
│   │   │   └── MyConnection.java         # Database connection
│   │   ├── tests/
│   │   │   └── ForumManagementApp.java   # Console test application
│   │   └── ForumApp.java                 # JavaFX main application
│   └── resources/
│       ├── Forum.fxml                    # Forum UI layout
│       ├── styles.css                    # Dark theme styling
│       ├── INVESTI.png                   # Logo
│       └── forum_schema.sql              # Database schema
└── test/java/edu/connexion3a8/
    ├── entities/
    │   └── ForumEntitiesTest.java        # Entity unit tests
    └── services/
        └── ForumPostServiceTest.java     # Service integration tests
```

## Setup Instructions

### 1. Database Setup
1. Start XAMPP (Apache + MySQL)
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Select database `3a8`
4. Run the SQL script from `src/main/resources/forum_schema.sql`

### 2. Running in IntelliJ IDEA
1. Open the project in IntelliJ
2. Right-click on `pom.xml` → Maven → Reload Project
3. Run `ForumApp.java` to launch the JavaFX application
4. Or run `ForumManagementApp.java` for console testing

### 3. Running Tests
1. In IntelliJ, right-click on `src/test/java`
2. Select "Run All Tests"
3. Or run individual test classes

## Features

### Posts
- Create, Read, Update, Delete posts
- Upvote/Downvote system (Reddit-style)
- Category filtering (Tips & Advice, Success Stories, etc.)
- Search functionality
- View count tracking
- Pinned posts support

### Comments
- Add comments to posts
- Reply to comments (nested structure)
- Upvote/Downvote comments
- Edit/Delete own comments

### User Activity
- "My Posts" - View your own posts
- "My Activity" - Posts you've voted on or commented on

## Database Tables

| Table | Description |
|-------|-------------|
| `forum_posts` | Main posts table |
| `forum_comments` | Comments with nested replies |
| `forum_post_votes` | Tracks user votes on posts |
| `forum_comment_votes` | Tracks user votes on comments |

## Design
- Dark theme matching TempDesk design
- Colors: Baltic Blue (#456990), Faded Copper (#9B7E46), Brown Red (#A62639)
- Responsive layout with sidebar and main content area

## Testing
- 22 unit tests for ForumPostService
- 10 unit tests for entities
- Tests cover CRUD, voting, search, and user activity
