package edu.connexion3a8.services;

import edu.connexion3a8.entities.ForumPost;
import edu.connexion3a8.entities.ForumComment;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForumPostServiceTest {

    private static ForumPostService forumService;
    private static String testPostId;
    private static String testCommentId;
    private static final String TEST_USER_ID = "test-user-123";

    @BeforeAll
    public static void setup() {
        forumService = new ForumPostService();
        System.out.println("[DEBUG_LOG] ForumPostService initialized for testing");
    }

    // ========== POST CRUD TESTS ==========

    @Test
    @Order(1)
    @DisplayName("Test Create Post")
    public void testCreatePost() {
        ForumPost post = new ForumPost(TEST_USER_ID, "Test Post Title", 
                "This is test content for the forum post.", "General");
        
        try {
            testPostId = forumService.addPost(post);
            System.out.println("[DEBUG_LOG] Created Post with ID: " + testPostId);
            
            assertNotNull(testPostId, "Post ID should not be null after creation");
            
            // Verify post was created
            ForumPost createdPost = forumService.getPostById(testPostId);
            assertNotNull(createdPost, "Created post should be retrievable");
            assertEquals("Test Post Title", createdPost.getTitle(), "Post title should match");
            assertEquals("General", createdPost.getCategory(), "Post category should match");
            
            System.out.println("[DEBUG_LOG] Verified: Post with ID " + testPostId + " exists");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test Read All Posts")
    public void testReadAllPosts() {
        try {
            List<ForumPost> posts = forumService.getAllPosts();
            System.out.println("[DEBUG_LOG] Retrieved " + posts.size() + " posts");
            
            assertNotNull(posts, "Posts list should not be null");
            assertFalse(posts.isEmpty(), "Posts list should not be empty after creating a post");
            
            // Verify our test post is in the list
            boolean found = posts.stream()
                    .anyMatch(p -> p.getId().equals(testPostId));
            assertTrue(found, "Test post should be in the list of all posts");
            
            System.out.println("[DEBUG_LOG] Verified: Test post found in all posts");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Read Post By ID")
    public void testReadPostById() {
        try {
            ForumPost post = forumService.getPostById(testPostId);
            System.out.println("[DEBUG_LOG] Retrieved post: " + post);
            
            assertNotNull(post, "Post should not be null");
            assertEquals(testPostId, post.getId(), "Post ID should match");
            assertEquals("Test Post Title", post.getTitle(), "Post title should match");
            assertEquals(TEST_USER_ID, post.getUserId(), "User ID should match");
            
            System.out.println("[DEBUG_LOG] Verified: Post details are correct");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test Update Post")
    public void testUpdatePost() {
        try {
            ForumPost post = forumService.getPostById(testPostId);
            assertNotNull(post, "Post should exist before update");
            
            post.setTitle("Updated Test Post Title");
            post.setContent("Updated content for the test post.");
            post.setCategory("Tips & Advice");
            
            forumService.updatePost(testPostId, post);
            System.out.println("[DEBUG_LOG] Updated post with ID: " + testPostId);
            
            // Verify update
            ForumPost updatedPost = forumService.getPostById(testPostId);
            assertEquals("Updated Test Post Title", updatedPost.getTitle(), "Title should be updated");
            assertEquals("Tips & Advice", updatedPost.getCategory(), "Category should be updated");
            
            System.out.println("[DEBUG_LOG] Verified: Post was updated successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test Get Posts By Category")
    public void testGetPostsByCategory() {
        try {
            List<ForumPost> posts = forumService.getPostsByCategory("Tips & Advice");
            System.out.println("[DEBUG_LOG] Retrieved " + posts.size() + " posts in 'Tips & Advice' category");
            
            assertNotNull(posts, "Posts list should not be null");
            
            // Our updated test post should be in this category
            boolean found = posts.stream()
                    .anyMatch(p -> p.getId().equals(testPostId));
            assertTrue(found, "Test post should be in 'Tips & Advice' category");
            
            System.out.println("[DEBUG_LOG] Verified: Post found in correct category");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test Get Posts By User")
    public void testGetPostsByUser() {
        try {
            List<ForumPost> posts = forumService.getPostsByUser(TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Retrieved " + posts.size() + " posts by user " + TEST_USER_ID);
            
            assertNotNull(posts, "Posts list should not be null");
            assertFalse(posts.isEmpty(), "User should have at least one post");
            
            // All posts should belong to the test user
            boolean allMatch = posts.stream()
                    .allMatch(p -> TEST_USER_ID.equals(p.getUserId()));
            assertTrue(allMatch, "All posts should belong to the test user");
            
            System.out.println("[DEBUG_LOG] Verified: All posts belong to test user");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }


    // ========== POST VOTE TESTS ==========

    @Test
    @Order(7)
    @DisplayName("Test Upvote Post")
    public void testUpvotePost() {
        try {
            ForumPost postBefore = forumService.getPostById(testPostId);
            int upvotesBefore = postBefore.getUpvotes();
            
            forumService.votePost(testPostId, TEST_USER_ID, "upvote");
            System.out.println("[DEBUG_LOG] Upvoted post: " + testPostId);
            
            ForumPost postAfter = forumService.getPostById(testPostId);
            assertEquals(upvotesBefore + 1, postAfter.getUpvotes(), "Upvotes should increase by 1");
            
            // Verify user vote is recorded
            String userVote = forumService.getUserVoteOnPost(testPostId, TEST_USER_ID);
            assertEquals("upvote", userVote, "User vote should be recorded as upvote");
            
            System.out.println("[DEBUG_LOG] Verified: Upvote recorded successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test Change Vote to Downvote")
    public void testChangeVoteToDownvote() {
        try {
            ForumPost postBefore = forumService.getPostById(testPostId);
            int upvotesBefore = postBefore.getUpvotes();
            int downvotesBefore = postBefore.getDownvotes();
            
            forumService.votePost(testPostId, TEST_USER_ID, "downvote");
            System.out.println("[DEBUG_LOG] Changed vote to downvote on post: " + testPostId);
            
            ForumPost postAfter = forumService.getPostById(testPostId);
            assertEquals(upvotesBefore - 1, postAfter.getUpvotes(), "Upvotes should decrease by 1");
            assertEquals(downvotesBefore + 1, postAfter.getDownvotes(), "Downvotes should increase by 1");
            
            String userVote = forumService.getUserVoteOnPost(testPostId, TEST_USER_ID);
            assertEquals("downvote", userVote, "User vote should be changed to downvote");
            
            System.out.println("[DEBUG_LOG] Verified: Vote changed successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test Remove Vote (Toggle Off)")
    public void testRemoveVote() {
        try {
            ForumPost postBefore = forumService.getPostById(testPostId);
            int downvotesBefore = postBefore.getDownvotes();
            
            // Voting same type again should remove the vote
            forumService.votePost(testPostId, TEST_USER_ID, "downvote");
            System.out.println("[DEBUG_LOG] Toggled off downvote on post: " + testPostId);
            
            ForumPost postAfter = forumService.getPostById(testPostId);
            assertEquals(downvotesBefore - 1, postAfter.getDownvotes(), "Downvotes should decrease by 1");
            
            String userVote = forumService.getUserVoteOnPost(testPostId, TEST_USER_ID);
            assertNull(userVote, "User vote should be removed");
            
            System.out.println("[DEBUG_LOG] Verified: Vote removed successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    // ========== COMMENT CRUD TESTS ==========

    @Test
    @Order(10)
    @DisplayName("Test Create Comment")
    public void testCreateComment() {
        ForumComment comment = new ForumComment(testPostId, TEST_USER_ID, 
                "This is a test comment on the post.");
        
        try {
            testCommentId = forumService.addComment(comment);
            System.out.println("[DEBUG_LOG] Created Comment with ID: " + testCommentId);
            
            assertNotNull(testCommentId, "Comment ID should not be null after creation");
            
            // Verify comment was created
            ForumComment createdComment = forumService.getCommentById(testCommentId);
            assertNotNull(createdComment, "Created comment should be retrievable");
            assertEquals("This is a test comment on the post.", createdComment.getContent(), 
                    "Comment content should match");
            
            System.out.println("[DEBUG_LOG] Verified: Comment with ID " + testCommentId + " exists");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test Read Comments By Post")
    public void testReadCommentsByPost() {
        try {
            List<ForumComment> comments = forumService.getCommentsByPost(testPostId);
            System.out.println("[DEBUG_LOG] Retrieved " + comments.size() + " comments for post " + testPostId);
            
            assertNotNull(comments, "Comments list should not be null");
            assertFalse(comments.isEmpty(), "Comments list should not be empty");
            
            System.out.println("[DEBUG_LOG] Verified: Comments retrieved successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test Create Reply Comment")
    public void testCreateReplyComment() {
        ForumComment reply = new ForumComment(testPostId, TEST_USER_ID, 
                "This is a reply to the first comment.");
        reply.setParentCommentId(testCommentId);
        
        try {
            String replyId = forumService.addComment(reply);
            System.out.println("[DEBUG_LOG] Created Reply Comment with ID: " + replyId);
            
            assertNotNull(replyId, "Reply ID should not be null");
            
            // Verify nested structure
            List<ForumComment> comments = forumService.getCommentsByPost(testPostId);
            ForumComment parentComment = comments.stream()
                    .filter(c -> c.getId().equals(testCommentId))
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(parentComment, "Parent comment should exist");
            assertFalse(parentComment.getReplies().isEmpty(), "Parent should have replies");
            
            System.out.println("[DEBUG_LOG] Verified: Reply nested under parent comment");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Test Update Comment")
    public void testUpdateComment() {
        try {
            forumService.updateComment(testCommentId, "Updated comment content.");
            System.out.println("[DEBUG_LOG] Updated comment: " + testCommentId);
            
            ForumComment updatedComment = forumService.getCommentById(testCommentId);
            assertEquals("Updated comment content.", updatedComment.getContent(), 
                    "Comment content should be updated");
            
            System.out.println("[DEBUG_LOG] Verified: Comment updated successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @DisplayName("Test Comment Count")
    public void testCommentCount() {
        try {
            int count = forumService.getCommentCountByPost(testPostId);
            System.out.println("[DEBUG_LOG] Comment count for post: " + count);
            
            assertTrue(count >= 2, "Should have at least 2 comments (original + reply)");
            
            System.out.println("[DEBUG_LOG] Verified: Comment count is correct");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }


    // ========== COMMENT VOTE TESTS ==========

    @Test
    @Order(15)
    @DisplayName("Test Upvote Comment")
    public void testUpvoteComment() {
        try {
            ForumComment commentBefore = forumService.getCommentById(testCommentId);
            int upvotesBefore = commentBefore.getUpvotes();
            
            forumService.voteComment(testCommentId, TEST_USER_ID, "upvote");
            System.out.println("[DEBUG_LOG] Upvoted comment: " + testCommentId);
            
            ForumComment commentAfter = forumService.getCommentById(testCommentId);
            assertEquals(upvotesBefore + 1, commentAfter.getUpvotes(), "Comment upvotes should increase by 1");
            
            String userVote = forumService.getUserVoteOnComment(testCommentId, TEST_USER_ID);
            assertEquals("upvote", userVote, "User vote on comment should be recorded");
            
            System.out.println("[DEBUG_LOG] Verified: Comment upvote recorded successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(16)
    @DisplayName("Test Downvote Comment")
    public void testDownvoteComment() {
        try {
            // Change vote to downvote
            forumService.voteComment(testCommentId, TEST_USER_ID, "downvote");
            System.out.println("[DEBUG_LOG] Changed comment vote to downvote: " + testCommentId);
            
            String userVote = forumService.getUserVoteOnComment(testCommentId, TEST_USER_ID);
            assertEquals("downvote", userVote, "User vote should be changed to downvote");
            
            System.out.println("[DEBUG_LOG] Verified: Comment vote changed successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    // ========== SEARCH TESTS ==========

    @Test
    @Order(17)
    @DisplayName("Test Search Posts")
    public void testSearchPosts() {
        try {
            List<ForumPost> results = forumService.searchPosts("Updated");
            System.out.println("[DEBUG_LOG] Search results: " + results.size() + " posts found");
            
            assertNotNull(results, "Search results should not be null");
            
            // Our test post should be in results (has "Updated" in title)
            boolean found = results.stream()
                    .anyMatch(p -> p.getId().equals(testPostId));
            assertTrue(found, "Test post should be found in search results");
            
            System.out.println("[DEBUG_LOG] Verified: Search works correctly");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    // ========== USER ACTIVITY TESTS ==========

    @Test
    @Order(18)
    @DisplayName("Test Get Posts Commented By User")
    public void testGetPostsCommentedByUser() {
        try {
            List<ForumPost> posts = forumService.getPostsCommentedByUser(TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Posts commented by user: " + posts.size());
            
            assertNotNull(posts, "Posts list should not be null");
            assertFalse(posts.isEmpty(), "User should have commented on at least one post");
            
            System.out.println("[DEBUG_LOG] Verified: User activity (comments) retrieved");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(19)
    @DisplayName("Test Get Comments By User")
    public void testGetCommentsByUser() {
        try {
            List<ForumComment> comments = forumService.getCommentsByUser(TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Comments by user: " + comments.size());
            
            assertNotNull(comments, "Comments list should not be null");
            assertFalse(comments.isEmpty(), "User should have at least one comment");
            
            // All comments should belong to the test user
            boolean allMatch = comments.stream()
                    .allMatch(c -> TEST_USER_ID.equals(c.getUserId()));
            assertTrue(allMatch, "All comments should belong to the test user");
            
            System.out.println("[DEBUG_LOG] Verified: User comments retrieved");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(20)
    @DisplayName("Test Increment Views")
    public void testIncrementViews() {
        try {
            ForumPost postBefore = forumService.getPostById(testPostId);
            int viewsBefore = postBefore.getViews();
            
            forumService.incrementViews(testPostId);
            System.out.println("[DEBUG_LOG] Incremented views for post: " + testPostId);
            
            ForumPost postAfter = forumService.getPostById(testPostId);
            assertEquals(viewsBefore + 1, postAfter.getViews(), "Views should increase by 1");
            
            System.out.println("[DEBUG_LOG] Verified: Views incremented successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    // ========== BOOKMARK TESTS ==========

    @Test
    @Order(21)
    @DisplayName("Test Add Bookmark")
    public void testAddBookmark() {
        try {
            // Should not be bookmarked initially
            assertFalse(forumService.isBookmarked(testPostId, TEST_USER_ID), "Post should not be bookmarked initially");

            forumService.addBookmark(testPostId, TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Added bookmark for post: " + testPostId);

            assertTrue(forumService.isBookmarked(testPostId, TEST_USER_ID), "Post should be bookmarked after adding");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(22)
    @DisplayName("Test Get Bookmarked Posts")
    public void testGetBookmarkedPosts() {
        try {
            List<ForumPost> bookmarked = forumService.getBookmarkedPosts(TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Bookmarked posts: " + bookmarked.size());

            assertNotNull(bookmarked, "Bookmarked list should not be null");
            boolean found = bookmarked.stream().anyMatch(p -> p.getId().equals(testPostId));
            assertTrue(found, "Test post should be in bookmarked list");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(23)
    @DisplayName("Test Toggle Bookmark Off")
    public void testToggleBookmarkOff() {
        try {
            // Post is currently bookmarked from previous test
            assertTrue(forumService.isBookmarked(testPostId, TEST_USER_ID));

            forumService.toggleBookmark(testPostId, TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Toggled bookmark off for post: " + testPostId);

            assertFalse(forumService.isBookmarked(testPostId, TEST_USER_ID), "Post should not be bookmarked after toggle off");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(24)
    @DisplayName("Test Toggle Bookmark On")
    public void testToggleBookmarkOn() {
        try {
            // Post is currently NOT bookmarked
            assertFalse(forumService.isBookmarked(testPostId, TEST_USER_ID));

            forumService.toggleBookmark(testPostId, TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Toggled bookmark on for post: " + testPostId);

            assertTrue(forumService.isBookmarked(testPostId, TEST_USER_ID), "Post should be bookmarked after toggle on");

            // Clean up bookmark for next tests
            forumService.removeBookmark(testPostId, TEST_USER_ID);
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(25)
    @DisplayName("Test Remove Bookmark")
    public void testRemoveBookmark() {
        try {
            forumService.addBookmark(testPostId, TEST_USER_ID);
            assertTrue(forumService.isBookmarked(testPostId, TEST_USER_ID));

            forumService.removeBookmark(testPostId, TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Removed bookmark for post: " + testPostId);

            assertFalse(forumService.isBookmarked(testPostId, TEST_USER_ID), "Post should not be bookmarked after removal");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    // ========== NOTIFICATION TESTS ==========

    @Test
    @Order(26)
    @DisplayName("Test Unread Notification Count Initially Zero")
    public void testUnreadNotificationCountZero() {
        try {
            // Mark all existing notifications as read first
            forumService.markAllNotificationsRead(TEST_USER_ID);

            int count = forumService.getUnreadNotificationCount(TEST_USER_ID);
            System.out.println("[DEBUG_LOG] Unread notification count: " + count);

            assertEquals(0, count, "Unread count should be 0 after marking all read");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(27)
    @DisplayName("Test Create Mention Notification")
    public void testCreateMentionNotification() {
        try {
            // Get a real user to use as sender (different from recipient)
            List<String[]> users = forumService.getAllUsers();
            if (users.size() < 2) {
                System.out.println("[DEBUG_LOG] Skipping: need at least 2 users for notification test");
                return;
            }
            String senderId = users.get(0)[0];
            String recipientId = users.get(1)[0];

            // Mark all read first
            forumService.markAllNotificationsRead(recipientId);

            forumService.createMentionNotification(recipientId, senderId, testPostId, null, "Test user tagged you in a post");
            System.out.println("[DEBUG_LOG] Created mention notification");

            int count = forumService.getUnreadNotificationCount(recipientId);
            assertTrue(count >= 1, "Should have at least 1 unread notification");

            System.out.println("[DEBUG_LOG] Verified: Notification created, unread count = " + count);
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(28)
    @DisplayName("Test Get Unread Notifications")
    public void testGetUnreadNotifications() {
        try {
            List<String[]> users = forumService.getAllUsers();
            if (users.size() < 2) return;
            String recipientId = users.get(1)[0];

            List<String[]> notifications = forumService.getUnreadNotifications(recipientId);
            System.out.println("[DEBUG_LOG] Unread notifications: " + notifications.size());

            assertNotNull(notifications, "Notifications list should not be null");
            assertFalse(notifications.isEmpty(), "Should have at least one unread notification");

            // Each notification should have 6 fields
            String[] notif = notifications.get(0);
            assertEquals(6, notif.length, "Notification should have 6 fields");
            assertNotNull(notif[0], "Notification ID should not be null");
            assertNotNull(notif[4], "Notification message should not be null");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(29)
    @DisplayName("Test Mark All Notifications Read")
    public void testMarkAllNotificationsRead() {
        try {
            List<String[]> users = forumService.getAllUsers();
            if (users.size() < 2) return;
            String recipientId = users.get(1)[0];

            forumService.markAllNotificationsRead(recipientId);
            System.out.println("[DEBUG_LOG] Marked all notifications as read");

            int count = forumService.getUnreadNotificationCount(recipientId);
            assertEquals(0, count, "Unread count should be 0 after marking all read");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(30)
    @DisplayName("Test Find User ID By Name")
    public void testFindUserIdByName() {
        try {
            List<String[]> users = forumService.getAllUsers();
            if (users.isEmpty()) return;

            String expectedId = users.get(0)[0];
            String name = users.get(0)[1];

            String foundId = forumService.findUserIdByName(name);
            System.out.println("[DEBUG_LOG] Found user ID for '" + name + "': " + foundId);

            assertEquals(expectedId, foundId, "Found user ID should match expected");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(31)
    @DisplayName("Test Find User ID By Name - Not Found")
    public void testFindUserIdByNameNotFound() {
        try {
            String foundId = forumService.findUserIdByName("NonExistentUser12345");
            System.out.println("[DEBUG_LOG] Found user ID for nonexistent user: " + foundId);

            assertNull(foundId, "Should return null for nonexistent user");
        } catch (SQLException e) {
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    // ========== DELETE TESTS (Run Last) ==========

    @Test
    @Order(40)
    @DisplayName("Test Delete Comment (Soft Delete)")
    public void testDeleteComment() {
        try {
            forumService.deleteComment(testCommentId);
            System.out.println("[DEBUG_LOG] Soft deleted comment: " + testCommentId);
            
            // Comment should not be retrievable after soft delete
            ForumComment deletedComment = forumService.getCommentById(testCommentId);
            assertNull(deletedComment, "Deleted comment should not be retrievable");
            
            System.out.println("[DEBUG_LOG] Verified: Comment soft deleted successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(41)
    @DisplayName("Test Delete Post (Soft Delete)")
    public void testDeletePost() {
        try {
            forumService.deletePost(testPostId);
            System.out.println("[DEBUG_LOG] Soft deleted post: " + testPostId);
            
            // Post should not be retrievable after soft delete
            ForumPost deletedPost = forumService.getPostById(testPostId);
            assertNull(deletedPost, "Deleted post should not be retrievable");
            
            System.out.println("[DEBUG_LOG] Verified: Post soft deleted successfully");
        } catch (SQLException e) {
            System.out.println("Exception in test: " + e.getMessage());
            fail("Should not throw SQLException: " + e.getMessage());
        }
    }

    @AfterAll
    public static void cleanup() {
        // Hard delete test data to clean up
        try {
            if (testPostId != null) {
                forumService.hardDeletePost(testPostId);
                System.out.println("[DEBUG_LOG] Cleaned up test post: " + testPostId);
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] Cleanup warning: " + e.getMessage());
        }
        System.out.println("[DEBUG_LOG] Test cleanup completed");
    }
}
