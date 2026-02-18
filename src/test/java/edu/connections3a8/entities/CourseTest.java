package edu.connections3a8.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    @Test
    @DisplayName("Test Default Constructor")
    public void testDefaultConstructor() {
        Course course = new Course();
        
        assertNotNull(course, "Course object should be created");
        assertEquals(0, course.getId(), "Default ID should be 0");
        assertNull(course.getTitle(), "Default title should be null");
        
        System.out.println("[TEST] Default constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor (3 params)")
    public void testParameterizedConstructor3Params() {
        Course course = new Course("Java Basics", "java-basics", "https://example.com/java");
        
        assertNotNull(course, "Course object should be created");
        assertEquals("Java Basics", course.getTitle(), "Title should match");
        assertEquals("java-basics", course.getSlug(), "Slug should match");
        assertEquals("https://example.com/java", course.getContentUrl(), "Content URL should match");
        
        System.out.println("[TEST] 3-parameter constructor works correctly");
    }

    @Test
    @DisplayName("Test Parameterized Constructor (Full)")
    public void testParameterizedConstructorFull() {
        Course course = new Course(
            1L, "Java Basics", "java-basics", "Learn Java programming",
            "https://example.com/java", "video", "beginner",
            "programming", "en", 120, 100,
            "published", "public", "https://example.com/thumb.jpg"
        );
        
        assertEquals(1L, course.getId(), "ID should match");
        assertEquals("Java Basics", course.getTitle(), "Title should match");
        assertEquals("java-basics", course.getSlug(), "Slug should match");
        assertEquals("Learn Java programming", course.getDescription(), "Description should match");
        assertEquals("https://example.com/java", course.getContentUrl(), "Content URL should match");
        assertEquals("video", course.getContentType(), "Content type should match");
        assertEquals("beginner", course.getDifficultyLevel(), "Difficulty should match");
        assertEquals("programming", course.getCategory(), "Category should match");
        assertEquals("en", course.getLanguage(), "Language should match");
        assertEquals(120, course.getEstimatedDuration(), "Duration should match");
        assertEquals(100, course.getRewardPoints(), "Reward points should match");
        assertEquals("published", course.getStatus(), "Status should match");
        assertEquals("public", course.getVisibility(), "Visibility should match");
        assertEquals("https://example.com/thumb.jpg", course.getThumbnailUrl(), "Thumbnail should match");
        
        System.out.println("[TEST] Full constructor works correctly");
    }

    @Test
    @DisplayName("Test All Getters and Setters")
    public void testGettersAndSetters() {
        Course course = new Course();
        
        // Test ID
        course.setId(42L);
        assertEquals(42L, course.getId(), "ID getter/setter should work");
        
        // Test Title
        course.setTitle("Advanced Java");
        assertEquals("Advanced Java", course.getTitle(), "Title getter/setter should work");
        
        // Test Slug
        course.setSlug("advanced-java");
        assertEquals("advanced-java", course.getSlug(), "Slug getter/setter should work");
        
        // Test Description
        course.setDescription("Advanced Java concepts");
        assertEquals("Advanced Java concepts", course.getDescription(), "Description getter/setter should work");
        
        // Test Content URL
        course.setContentUrl("https://example.com/advanced");
        assertEquals("https://example.com/advanced", course.getContentUrl(), "Content URL getter/setter should work");
        
        // Test Content Type
        course.setContentType("interactive");
        assertEquals("interactive", course.getContentType(), "Content type getter/setter should work");
        
        // Test Difficulty Level
        course.setDifficultyLevel("advanced");
        assertEquals("advanced", course.getDifficultyLevel(), "Difficulty getter/setter should work");
        
        // Test Category
        course.setCategory("web-development");
        assertEquals("web-development", course.getCategory(), "Category getter/setter should work");
        
        // Test Language
        course.setLanguage("fr");
        assertEquals("fr", course.getLanguage(), "Language getter/setter should work");
        
        // Test Estimated Duration
        course.setEstimatedDuration(180);
        assertEquals(180, course.getEstimatedDuration(), "Duration getter/setter should work");
        
        // Test Reward Points
        course.setRewardPoints(150);
        assertEquals(150, course.getRewardPoints(), "Reward points getter/setter should work");
        
        // Test Status
        course.setStatus("draft");
        assertEquals("draft", course.getStatus(), "Status getter/setter should work");
        
        // Test Visibility
        course.setVisibility("private");
        assertEquals("private", course.getVisibility(), "Visibility getter/setter should work");
        
        // Test Thumbnail URL
        course.setThumbnailUrl("https://example.com/new-thumb.jpg");
        assertEquals("https://example.com/new-thumb.jpg", course.getThumbnailUrl(), "Thumbnail getter/setter should work");
        
        System.out.println("[TEST] All getters and setters work correctly");
    }

    @Test
    @DisplayName("Test Equals Method")
    public void testEquals() {
        Course course1 = new Course("Java", "java", "url");
        course1.setId(1L);
        
        Course course2 = new Course("Python", "python", "url2");
        course2.setId(1L);
        
        Course course3 = new Course("Java", "java", "url");
        course3.setId(2L);
        
        // Same ID and slug
        assertEquals(course1, course2, "Courses with same ID should be equal");
        
        // Different ID
        assertNotEquals(course1, course3, "Courses with different ID should not be equal");
        
        // Null comparison
        assertNotEquals(course1, null, "Course should not equal null");
        
        System.out.println("[TEST] Equals method works correctly");
    }

    @Test
    @DisplayName("Test HashCode Method")
    public void testHashCode() {
        Course course1 = new Course("Java", "java-basics", "url");
        course1.setId(1L);
        
        Course course2 = new Course("Java", "java-basics", "url");
        course2.setId(1L);
        
        assertEquals(course1.hashCode(), course2.hashCode(), "Equal courses should have same hash code");
        
        System.out.println("[TEST] HashCode method works correctly");
    }

    @Test
    @DisplayName("Test Timestamp Getters")
    public void testTimestampGetters() {
        Course course = new Course();
        
        // Timestamps are read-only (no setters in entity)
        assertNull(course.getCreatedAt(), "CreatedAt should be null initially");
        assertNull(course.getUpdatedAt(), "UpdatedAt should be null initially");
        assertNull(course.getPublishedAt(), "PublishedAt should be null initially");
        
        System.out.println("[TEST] Timestamp getters work correctly");
    }
}
