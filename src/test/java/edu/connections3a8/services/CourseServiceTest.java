package edu.connections3a8.services;

import edu.connections3a8.entities.Course;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseServiceTest {

    private static CouseService courseService;
    private static long testCourseId = -1;
    private static String testCourseSlug = "";

    @BeforeAll
    public static void setUp() {
        courseService = new CouseService();
        System.out.println("[TEST_SETUP] CourseService initialized");
    }

    @Test
    @Order(1)
    @DisplayName("Test Create Course")
    public void testCreateCourse() {
        Course course = new Course();
        course.setTitle("Test Java Course");
        course.setSlug("test-java-course-" + System.currentTimeMillis()); // Make slug unique
        course.setDescription("A comprehensive Java programming course");
        course.setContentUrl("https://example.com/java-course");
        course.setContentType("video");
        course.setDifficultyLevel("beginner");
        course.setCategory("programming");
        course.setLanguage("en");
        course.setEstimatedDuration(120);
        course.setRewardPoints(100);
        course.setStatus("published");
        course.setVisibility("public");
        course.setThumbnailUrl("https://example.com/thumb.jpg");

        try {
            // Get count before adding
            List<Course> coursesBefore = courseService.getAllCourses();
            int countBefore = coursesBefore.size();
            
            courseService.addCourse(course);
            
            // Retrieve to get the ID
            List<Course> coursesAfter = courseService.getAllCourses();
            
            System.out.println("[DEBUG_LOG] Courses before: " + countBefore + ", after: " + coursesAfter.size());
            
            // Find the newly created course
            Course created = null;
            for (Course c : coursesAfter) {
                boolean existedBefore = coursesBefore.stream()
                        .anyMatch(cb -> cb.getId() == c.getId());
                if (!existedBefore && c.getSlug().equals(course.getSlug())) {
                    created = c;
                    break;
                }
            }
            
            // Fallback: find by slug
            if (created == null) {
                created = coursesAfter.stream()
                        .filter(c -> c.getSlug().equals(course.getSlug()))
                        .findFirst()
                        .orElse(null);
            }
            
            assertNotNull(created, "Created course should be found");
            testCourseId = created.getId();
            testCourseSlug = created.getSlug(); // Save the slug for later tests
            
            System.out.println("[DEBUG_LOG] Created Course with ID: " + testCourseId + ", Slug: " + testCourseSlug);
            assertTrue(testCourseId > 0, "Course ID should be greater than 0");
            assertEquals("Test Java Course", created.getTitle(), "Title should match");
            assertEquals(100, created.getRewardPoints(), "Reward points should match");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test Get All Courses")
    public void testGetAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            
            System.out.println("[DEBUG_LOG] Retrieved " + courses.size() + " courses from database");
            assertNotNull(courses, "Courses list should not be null");
            assertFalse(courses.isEmpty(), "Courses list should not be empty");
            
            boolean found = courses.stream()
                    .anyMatch(c -> c.getId() == testCourseId);
            assertTrue(found, "Test course should exist in the list");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Get Course By ID")
    public void testGetCourseById() {
        try {
            Course course = courseService.getCourseById(testCourseId);
            
            assertNotNull(course, "Course should be found");
            assertEquals(testCourseId, course.getId(), "ID should match");
            assertEquals("Test Java Course", course.getTitle(), "Title should match");
            assertEquals("test-java-course", course.getSlug(), "Slug should match");
            
            System.out.println("[DEBUG_LOG] Successfully retrieved course by ID: " + testCourseId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test Get Course By Slug")
    public void testGetCourseBySlug() {
        try {
            Course course = courseService.getCourseBySlug(testCourseSlug);
            
            assertNotNull(course, "Course should be found by slug");
            assertEquals("Test Java Course", course.getTitle(), "Title should match");
            
            System.out.println("[DEBUG_LOG] Successfully retrieved course by slug: " + testCourseSlug);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test Get Courses By Category")
    public void testGetCoursesByCategory() {
        try {
            List<Course> courses = courseService.getCoursesByCategory("programming");
            
            assertNotNull(courses, "Courses list should not be null");
            assertFalse(courses.isEmpty(), "Should find courses in programming category");
            
            boolean found = courses.stream()
                    .anyMatch(c -> c.getId() == testCourseId);
            assertTrue(found, "Test course should be in programming category");
            
            System.out.println("[DEBUG_LOG] Found " + courses.size() + " courses in programming category");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test Get Courses By Difficulty")
    public void testGetCoursesByDifficulty() {
        try {
            List<Course> courses = courseService.getCoursesByDifficulty("beginner");
            
            assertNotNull(courses, "Courses list should not be null");
            assertFalse(courses.isEmpty(), "Should find beginner courses");
            
            System.out.println("[DEBUG_LOG] Found " + courses.size() + " beginner courses");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test Update Course")
    public void testUpdateCourse() {
        try {
            Course updatedCourse = courseService.getCourseById(testCourseId);
            updatedCourse.setTitle("Updated Java Course");
            updatedCourse.setRewardPoints(150);
            
            courseService.updateCourse(updatedCourse, testCourseId);
            
            // Verify update
            Course retrieved = courseService.getCourseById(testCourseId);
            assertEquals("Updated Java Course", retrieved.getTitle(), "Title should be updated");
            assertEquals(150, retrieved.getRewardPoints(), "Reward points should be updated");
            
            System.out.println("[DEBUG_LOG] Successfully updated course with ID: " + testCourseId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test Publish Course")
    public void testPublishCourse() {
        try {
            courseService.publishCourse(testCourseId);
            
            Course course = courseService.getCourseById(testCourseId);
            assertEquals("published", course.getStatus(), "Status should be published");
            assertEquals("public", course.getVisibility(), "Visibility should be public");
            
            System.out.println("[DEBUG_LOG] Successfully published course");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test Archive Course")
    public void testArchiveCourse() {
        try {
            courseService.archiveCourse(testCourseId);
            
            Course course = courseService.getCourseById(testCourseId);
            assertEquals("archived", course.getStatus(), "Status should be archived");
            
            System.out.println("[DEBUG_LOG] Successfully archived course");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test Delete Course")
    public void testDeleteCourse() {
        try {
            courseService.deleteCourse(testCourseId);
            
            Course deleted = courseService.getCourseById(testCourseId);
            assertNull(deleted, "Deleted course should not exist");
            
            System.out.println("[DEBUG_LOG] Successfully deleted course with ID: " + testCourseId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("[TEST_CLEANUP] All CourseService tests completed");
    }
}
