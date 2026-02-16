package edu.connexion3a8.entities;

import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Forum Entities Unit Tests")
public class ForumEntitiesTest {

    // ========== ForumPost Tests ==========

    @Test
    @DisplayName("Test ForumPost Default Constructor")
    public void testForumPostDefaultConstructor() {
        ForumPost post = new ForumPost();
        
        assertNull(post.getId(), "ID should be null by default");
        assertNull(post.getUserId(), "User ID should be null by default");
        assertNull(post.getTitle(), "Title should be null by default");
        assertEquals(0, post.getUpvotes(), "Upvotes should be 0 by default");
        assertEquals(0, post.getDownvotes(), "Downvotes should be 0 by default");
        assertFalse(post.isPinned(), "isPinned should be false by default");
        assertFalse(post.isDeleted(), "isDeleted should be false by default");
        
        System.out.println("[DEBUG_LOG] ForumPost default constructor test passed");
    }

    @Test
    @DisplayName("Test ForumPost Parameterized Constructor")
    public void testForumPostParameterizedConstructor() {
        ForumPost post = new ForumPost("user-123", "Test Title", "Test Content", "General");
        
        assertEquals("user-123", post.getUserId(), "User ID should match");
        assertEquals("Test Title", post.getTitle(), "Title should match");
        assertEquals("Test Content", post.getContent(), "Content should match");
        assertEquals("General", post.getCategory(), "Category should match");
        assertEquals(0, post.getUpvotes(), "Upvotes should be 0");
        assertEquals(0, post.getDownvotes(), "Downvotes should be 0");
        assertEquals(0, post.getViews(), "Views should be 0");
        assertFalse(post.isPinned(), "isPinned should be false");
        assertFalse(post.isDeleted(), "isDeleted should be false");
        
        System.out.println("[DEBUG_LOG] ForumPost parameterized constructor test passed");
    }

    @Test
    @DisplayName("Test ForumPost Getters and Setters")
    public void testForumPostGettersSetters() {
        ForumPost post = new ForumPost();
        
        post.setId("post-123");
        post.setUserId("user-456");
        post.setTitle("My Post");
        post.setContent("Post content here");
        post.setCategory("Tips & Advice");
        post.setUpvotes(10);
        post.setDownvotes(2);
        post.setViews(100);
        post.setPinned(true);
        post.setDeleted(false);
        post.setAuthorName("John Doe");
        post.setAuthorAvatar("avatar.png");
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        
        assertEquals("post-123", post.getId());
        assertEquals("user-456", post.getUserId());
        assertEquals("My Post", post.getTitle());
        assertEquals("Post content here", post.getContent());
        assertEquals("Tips & Advice", post.getCategory());
        assertEquals(10, post.getUpvotes());
        assertEquals(2, post.getDownvotes());
        assertEquals(100, post.getViews());
        assertTrue(post.isPinned());
        assertFalse(post.isDeleted());
        assertEquals("John Doe", post.getAuthorName());
        assertEquals("avatar.png", post.getAuthorAvatar());
        assertEquals(now, post.getCreatedAt());
        assertEquals(now, post.getUpdatedAt());
        
        System.out.println("[DEBUG_LOG] ForumPost getters/setters test passed");
    }

    @Test
    @DisplayName("Test ForumPost Score Calculation")
    public void testForumPostScore() {
        ForumPost post = new ForumPost();
        
        post.setUpvotes(15);
        post.setDownvotes(5);
        
        assertEquals(10, post.getScore(), "Score should be upvotes - downvotes");
        
        post.setUpvotes(3);
        post.setDownvotes(10);
        
        assertEquals(-7, post.getScore(), "Score can be negative");
        
        System.out.println("[DEBUG_LOG] ForumPost score calculation test passed");
    }

    @Test
    @DisplayName("Test ForumPost toString")
    public void testForumPostToString() {
        ForumPost post = new ForumPost("user-1", "Test", "Content", "General");
        post.setId("post-1");
        post.setUpvotes(5);
        post.setDownvotes(1);
        post.setViews(50);
        
        String str = post.toString();
        
        assertTrue(str.contains("post-1"), "toString should contain ID");
        assertTrue(str.contains("Test"), "toString should contain title");
        assertTrue(str.contains("5"), "toString should contain upvotes");
        
        System.out.println("[DEBUG_LOG] ForumPost toString test passed");
    }

    // ========== ForumComment Tests ==========

    @Test
    @DisplayName("Test ForumComment Default Constructor")
    public void testForumCommentDefaultConstructor() {
        ForumComment comment = new ForumComment();
        
        assertNull(comment.getId(), "ID should be null by default");
        assertNull(comment.getPostId(), "Post ID should be null by default");
        assertNotNull(comment.getReplies(), "Replies list should be initialized");
        assertTrue(comment.getReplies().isEmpty(), "Replies should be empty by default");
        
        System.out.println("[DEBUG_LOG] ForumComment default constructor test passed");
    }

    @Test
    @DisplayName("Test ForumComment Parameterized Constructor")
    public void testForumCommentParameterizedConstructor() {
        ForumComment comment = new ForumComment("post-123", "user-456", "This is a comment");
        
        assertEquals("post-123", comment.getPostId(), "Post ID should match");
        assertEquals("user-456", comment.getUserId(), "User ID should match");
        assertEquals("This is a comment", comment.getContent(), "Content should match");
        assertEquals(0, comment.getUpvotes(), "Upvotes should be 0");
        assertEquals(0, comment.getDownvotes(), "Downvotes should be 0");
        assertFalse(comment.isDeleted(), "isDeleted should be false");
        assertNotNull(comment.getReplies(), "Replies should be initialized");
        
        System.out.println("[DEBUG_LOG] ForumComment parameterized constructor test passed");
    }

    @Test
    @DisplayName("Test ForumComment Getters and Setters")
    public void testForumCommentGettersSetters() {
        ForumComment comment = new ForumComment();
        
        comment.setId("comment-123");
        comment.setPostId("post-456");
        comment.setUserId("user-789");
        comment.setParentCommentId("parent-111");
        comment.setContent("Comment content");
        comment.setUpvotes(5);
        comment.setDownvotes(1);
        comment.setDeleted(false);
        comment.setAuthorName("Jane Doe");
        comment.setAuthorAvatar("jane.png");
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        
        assertEquals("comment-123", comment.getId());
        assertEquals("post-456", comment.getPostId());
        assertEquals("user-789", comment.getUserId());
        assertEquals("parent-111", comment.getParentCommentId());
        assertEquals("Comment content", comment.getContent());
        assertEquals(5, comment.getUpvotes());
        assertEquals(1, comment.getDownvotes());
        assertFalse(comment.isDeleted());
        assertEquals("Jane Doe", comment.getAuthorName());
        assertEquals("jane.png", comment.getAuthorAvatar());
        assertEquals(now, comment.getCreatedAt());
        assertEquals(now, comment.getUpdatedAt());
        
        System.out.println("[DEBUG_LOG] ForumComment getters/setters test passed");
    }

    @Test
    @DisplayName("Test ForumComment Score Calculation")
    public void testForumCommentScore() {
        ForumComment comment = new ForumComment();
        
        comment.setUpvotes(20);
        comment.setDownvotes(8);
        
        assertEquals(12, comment.getScore(), "Score should be upvotes - downvotes");
        
        System.out.println("[DEBUG_LOG] ForumComment score calculation test passed");
    }

    @Test
    @DisplayName("Test ForumComment Replies Management")
    public void testForumCommentReplies() {
        ForumComment parent = new ForumComment("post-1", "user-1", "Parent comment");
        ForumComment reply1 = new ForumComment("post-1", "user-2", "Reply 1");
        ForumComment reply2 = new ForumComment("post-1", "user-3", "Reply 2");
        
        parent.addReply(reply1);
        parent.addReply(reply2);
        
        assertEquals(2, parent.getReplies().size(), "Should have 2 replies");
        assertEquals("Reply 1", parent.getReplies().get(0).getContent());
        assertEquals("Reply 2", parent.getReplies().get(1).getContent());
        
        // Test setReplies
        List<ForumComment> newReplies = new ArrayList<>();
        newReplies.add(new ForumComment("post-1", "user-4", "New Reply"));
        parent.setReplies(newReplies);
        
        assertEquals(1, parent.getReplies().size(), "Should have 1 reply after setReplies");
        
        System.out.println("[DEBUG_LOG] ForumComment replies management test passed");
    }

    @Test
    @DisplayName("Test ForumComment toString")
    public void testForumCommentToString() {
        ForumComment comment = new ForumComment("post-1", "user-1", "Test comment");
        comment.setId("comment-1");
        comment.setUpvotes(3);
        comment.setDownvotes(0);
        
        String str = comment.toString();
        
        assertTrue(str.contains("comment-1"), "toString should contain ID");
        assertTrue(str.contains("post-1"), "toString should contain post ID");
        assertTrue(str.contains("Test comment"), "toString should contain content");
        
        System.out.println("[DEBUG_LOG] ForumComment toString test passed");
    }

    // ========== ForumPostVote Tests ==========

    @Test
    @DisplayName("Test ForumPostVote")
    public void testForumPostVote() {
        ForumPostVote vote = new ForumPostVote();
        
        assertNull(vote.getId(), "ID should be null by default");
        
        vote = new ForumPostVote("post-123", "user-456", "upvote");
        
        assertEquals("post-123", vote.getPostId());
        assertEquals("user-456", vote.getUserId());
        assertEquals("upvote", vote.getVoteType());
        
        vote.setId("vote-1");
        vote.setVoteType("downvote");
        
        assertEquals("vote-1", vote.getId());
        assertEquals("downvote", vote.getVoteType());
        
        String str = vote.toString();
        assertTrue(str.contains("vote-1"), "toString should contain ID");
        assertTrue(str.contains("downvote"), "toString should contain vote type");
        
        System.out.println("[DEBUG_LOG] ForumPostVote test passed");
    }

    // ========== ForumCommentVote Tests ==========

    @Test
    @DisplayName("Test ForumCommentVote")
    public void testForumCommentVote() {
        ForumCommentVote vote = new ForumCommentVote();
        
        assertNull(vote.getId(), "ID should be null by default");
        
        vote = new ForumCommentVote("comment-123", "user-456", "downvote");
        
        assertEquals("comment-123", vote.getCommentId());
        assertEquals("user-456", vote.getUserId());
        assertEquals("downvote", vote.getVoteType());
        
        vote.setId("vote-2");
        vote.setVoteType("upvote");
        
        assertEquals("vote-2", vote.getId());
        assertEquals("upvote", vote.getVoteType());
        
        String str = vote.toString();
        assertTrue(str.contains("vote-2"), "toString should contain ID");
        assertTrue(str.contains("upvote"), "toString should contain vote type");
        
        System.out.println("[DEBUG_LOG] ForumCommentVote test passed");
    }
}
