package edu.connexion3a8.tests;

import edu.connexion3a8.entities.ForumPost;
import edu.connexion3a8.entities.ForumComment;
import edu.connexion3a8.services.ForumPostService;

import java.sql.SQLException;
import java.util.List;

/**
 * Console-based test application for Forum Management
 * Use this to test CRUD operations before running the JavaFX UI
 */
public class ForumManagementApp {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   INVESTI Forum Management - Console Test");
        System.out.println("===========================================\n");

        ForumPostService forumService = new ForumPostService();
        String testUserId = "test-console-user";
        String testPostId = null;
        String testCommentId = null;

        try {
            // Test 1: Create a Post
            System.out.println("--- Test 1: Creating a Post ---");
            ForumPost newPost = new ForumPost(testUserId, "Console Test Post", 
                    "This is a test post created from the console application.", "General");
            testPostId = forumService.addPost(newPost);
            System.out.println("✓ Post created with ID: " + testPostId);

            // Test 2: Read All Posts
            System.out.println("\n--- Test 2: Reading All Posts ---");
            List<ForumPost> allPosts = forumService.getAllPosts();
            System.out.println("✓ Found " + allPosts.size() + " posts");
            for (ForumPost post : allPosts) {
                System.out.println("  - " + post.getTitle() + " (Score: " + post.getScore() + ")");
            }

            // Test 3: Read Post by ID
            System.out.println("\n--- Test 3: Reading Post by ID ---");
            ForumPost retrievedPost = forumService.getPostById(testPostId);
            if (retrievedPost != null) {
                System.out.println("✓ Retrieved post: " + retrievedPost.getTitle());
                System.out.println("  Content: " + retrievedPost.getContent());
                System.out.println("  Category: " + retrievedPost.getCategory());
            }

            // Test 4: Update Post
            System.out.println("\n--- Test 4: Updating Post ---");
            retrievedPost.setTitle("Updated Console Test Post");
            retrievedPost.setCategory("Tips & Advice");
            forumService.updatePost(testPostId, retrievedPost);
            ForumPost updatedPost = forumService.getPostById(testPostId);
            System.out.println("✓ Post updated: " + updatedPost.getTitle());
            System.out.println("  New category: " + updatedPost.getCategory());

            // Test 5: Upvote Post
            System.out.println("\n--- Test 5: Voting on Post ---");
            forumService.votePost(testPostId, testUserId, "upvote");
            ForumPost votedPost = forumService.getPostById(testPostId);
            System.out.println("✓ Upvoted post. New score: " + votedPost.getScore());

            // Test 6: Create Comment
            System.out.println("\n--- Test 6: Creating Comment ---");
            ForumComment newComment = new ForumComment(testPostId, testUserId, 
                    "This is a test comment on the post.");
            testCommentId = forumService.addComment(newComment);
            System.out.println("✓ Comment created with ID: " + testCommentId);

            // Test 7: Read Comments
            System.out.println("\n--- Test 7: Reading Comments ---");
            List<ForumComment> comments = forumService.getCommentsByPost(testPostId);
            System.out.println("✓ Found " + comments.size() + " comments");
            for (ForumComment comment : comments) {
                System.out.println("  - " + comment.getContent() + " (Score: " + comment.getScore() + ")");
            }

            // Test 8: Create Reply
            System.out.println("\n--- Test 8: Creating Reply ---");
            ForumComment reply = new ForumComment(testPostId, testUserId, "This is a reply to the comment.");
            reply.setParentCommentId(testCommentId);
            String replyId = forumService.addComment(reply);
            System.out.println("✓ Reply created with ID: " + replyId);

            // Test 9: Vote on Comment
            System.out.println("\n--- Test 9: Voting on Comment ---");
            forumService.voteComment(testCommentId, testUserId, "upvote");
            ForumComment votedComment = forumService.getCommentById(testCommentId);
            System.out.println("✓ Upvoted comment. New score: " + votedComment.getScore());

            // Test 10: Search Posts
            System.out.println("\n--- Test 10: Searching Posts ---");
            List<ForumPost> searchResults = forumService.searchPosts("Console");
            System.out.println("✓ Search for 'Console' found " + searchResults.size() + " results");

            // Test 11: Get Posts by Category
            System.out.println("\n--- Test 11: Getting Posts by Category ---");
            List<ForumPost> categoryPosts = forumService.getPostsByCategory("Tips & Advice");
            System.out.println("✓ Found " + categoryPosts.size() + " posts in 'Tips & Advice'");

            // Test 12: Get User Activity
            System.out.println("\n--- Test 12: Getting User Activity ---");
            List<ForumPost> userPosts = forumService.getPostsByUser(testUserId);
            List<ForumComment> userComments = forumService.getCommentsByUser(testUserId);
            System.out.println("✓ User has " + userPosts.size() + " posts and " + userComments.size() + " comments");

            // Cleanup: Delete test data
            System.out.println("\n--- Cleanup: Deleting Test Data ---");
            forumService.deletePost(testPostId);
            System.out.println("✓ Test post soft-deleted");

            // Hard delete for complete cleanup
            forumService.hardDeletePost(testPostId);
            System.out.println("✓ Test post hard-deleted (cleanup complete)");

            System.out.println("\n===========================================");
            System.out.println("   All tests completed successfully! ✓");
            System.out.println("===========================================");

        } catch (SQLException e) {
            System.err.println("\n✗ Error during testing: " + e.getMessage());
            e.printStackTrace();
            
            // Attempt cleanup on error
            if (testPostId != null) {
                try {
                    forumService.hardDeletePost(testPostId);
                    System.out.println("Cleanup: Deleted test post");
                } catch (SQLException ex) {
                    System.err.println("Cleanup failed: " + ex.getMessage());
                }
            }
        }
    }
}
