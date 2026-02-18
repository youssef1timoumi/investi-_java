package edu.connections3a8.services;

import edu.connections3a8.entities.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GamificationServiceTest {

    private static GamificationService gamificationService;
    private static int testUserId = 1; // Assuming user with ID 1 exists
    private static long testQuizId = -1;
    private static long testBadgeId = -1;

    @BeforeAll
    public static void setUp() {
        gamificationService = new GamificationService();
        System.out.println("[TEST_SETUP] GamificationService initialized");
    }

    // ===== QUIZ TESTS =====

    @Test
    @Order(1)
    @DisplayName("Test Create Quiz")
    public void testCreateQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Test Java Quiz");
        quiz.setDescription("Test your Java knowledge");
        quiz.setPointsReward(50);
        quiz.setQuestionCount(10);
        quiz.setDifficultyLevel("intermediate");
        quiz.setCategory("programming");
        quiz.setTimeLimit(600);
        quiz.setPassingScore(70);
        quiz.setStatus("active");

        try {
            // Get count before adding
            List<Quiz> quizzesBefore = gamificationService.getAllQuizzes();
            int countBefore = quizzesBefore.size();
            
            System.out.println("[DEBUG_LOG] Quizzes before adding: " + countBefore);
            
            gamificationService.addQuiz(quiz);
            
            System.out.println("[DEBUG_LOG] Quiz added to database");
            
            // Retrieve to get the ID - get the newest quiz
            List<Quiz> quizzesAfter = gamificationService.getAllQuizzes();
            
            System.out.println("[DEBUG_LOG] Quizzes after adding: " + quizzesAfter.size());
            
            // Find the newly created quiz (should be the one not in the before list)
            Quiz created = null;
            for (Quiz q : quizzesAfter) {
                boolean existedBefore = quizzesBefore.stream()
                        .anyMatch(qb -> qb.getId() == q.getId());
                if (!existedBefore && q.getTitle().equals("Test Java Quiz")) {
                    created = q;
                    System.out.println("[DEBUG_LOG] Found new quiz by difference: ID=" + q.getId());
                    break;
                }
            }
            
            // If not found by difference, try to find by title (in case it's the only one)
            if (created == null) {
                System.out.println("[DEBUG_LOG] Could not find by difference, searching by title...");
                created = quizzesAfter.stream()
                        .filter(q -> q.getTitle().equals("Test Java Quiz"))
                        .findFirst()
                        .orElse(null);
                
                if (created != null) {
                    System.out.println("[DEBUG_LOG] Found quiz by title: ID=" + created.getId());
                }
            }
            
            if (created == null) {
                System.out.println("[ERROR] Could not find created quiz. All quizzes after creation:");
                for (Quiz q : quizzesAfter) {
                    System.out.println("  - ID: " + q.getId() + ", Title: " + q.getTitle() + ", Status: " + q.getStatus());
                }
            }
            
            assertNotNull(created, "Created quiz should be found");
            testQuizId = created.getId();
            
            System.out.println("[DEBUG_LOG] Created Quiz with ID: " + testQuizId);
            assertTrue(testQuizId > 0, "Quiz ID should be greater than 0");
            assertEquals("Test Java Quiz", created.getTitle(), "Title should match");
            assertEquals(50, created.getPointsReward(), "Points reward should match");
            
        } catch (SQLException e) {
            System.err.println("[ERROR] SQLException: " + e.getMessage());
            e.printStackTrace();
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test Get All Quizzes")
    public void testGetAllQuizzes() {
        try {
            List<Quiz> quizzes = gamificationService.getAllQuizzes();
            
            System.out.println("[DEBUG_LOG] Retrieved " + quizzes.size() + " quizzes from database");
            System.out.println("[DEBUG_LOG] Looking for quiz with ID: " + testQuizId);
            
            assertNotNull(quizzes, "Quizzes list should not be null");
            assertFalse(quizzes.isEmpty(), "Quizzes list should not be empty");
            
            // If testQuizId is still -1, it means the previous test failed
            if (testQuizId == -1) {
                System.out.println("[WARNING] testQuizId is -1, trying to find test quiz by title");
                Quiz testQuiz = quizzes.stream()
                        .filter(q -> q.getTitle().equals("Test Java Quiz"))
                        .findFirst()
                        .orElse(null);
                
                if (testQuiz != null) {
                    testQuizId = testQuiz.getId();
                    System.out.println("[DEBUG_LOG] Found test quiz with ID: " + testQuizId);
                } else {
                    System.out.println("[ERROR] Could not find test quiz. Available quizzes:");
                    for (Quiz q : quizzes) {
                        System.out.println("  - ID: " + q.getId() + ", Title: " + q.getTitle());
                    }
                    fail("Test quiz 'Test Java Quiz' not found in database. Previous test may have failed.");
                }
            }
            
            boolean found = quizzes.stream()
                    .anyMatch(q -> q.getId() == testQuizId);
            
            if (!found) {
                System.out.println("[ERROR] Quiz with ID " + testQuizId + " not found. Available quiz IDs:");
                quizzes.forEach(q -> System.out.println("  - " + q.getId() + ": " + q.getTitle()));
            }
            
            assertTrue(found, "Test quiz with ID " + testQuizId + " should exist in the list");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Get Quiz By ID")
    public void testGetQuizById() {
        try {
            Quiz quiz = gamificationService.getQuizById(testQuizId);
            
            assertNotNull(quiz, "Quiz should be found");
            assertEquals(testQuizId, quiz.getId(), "ID should match");
            assertEquals("Test Java Quiz", quiz.getTitle(), "Title should match");
            
            System.out.println("[DEBUG_LOG] Successfully retrieved quiz by ID: " + testQuizId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test Get Quizzes By Category")
    public void testGetQuizzesByCategory() {
        try {
            List<Quiz> quizzes = gamificationService.getQuizzesByCategory("programming");
            
            assertNotNull(quizzes, "Quizzes list should not be null");
            assertFalse(quizzes.isEmpty(), "Should find quizzes in programming category");
            
            System.out.println("[DEBUG_LOG] Found " + quizzes.size() + " quizzes in programming category");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test Update Quiz")
    public void testUpdateQuiz() {
        try {
            Quiz updatedQuiz = gamificationService.getQuizById(testQuizId);
            updatedQuiz.setTitle("Updated Java Quiz");
            updatedQuiz.setPointsReward(75);
            
            gamificationService.updateQuiz(updatedQuiz, testQuizId);
            
            // Verify update
            Quiz retrieved = gamificationService.getQuizById(testQuizId);
            assertEquals("Updated Java Quiz", retrieved.getTitle(), "Title should be updated");
            assertEquals(75, retrieved.getPointsReward(), "Points reward should be updated");
            
            System.out.println("[DEBUG_LOG] Successfully updated quiz with ID: " + testQuizId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    // ===== BADGE TESTS =====

    @Test
    @Order(6)
    @DisplayName("Test Get All Badges")
    public void testGetAllBadges() {
        try {
            List<Badge> badges = gamificationService.getAllBadges();
            
            System.out.println("[DEBUG_LOG] Retrieved " + badges.size() + " badges from database");
            assertNotNull(badges, "Badges list should not be null");
            
            if (!badges.isEmpty()) {
                testBadgeId = badges.get(0).getId();
                System.out.println("[DEBUG_LOG] Using badge ID: " + testBadgeId + " for tests");
            }
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test Award Badge")
    public void testAwardBadge() {
        if (testBadgeId <= 0) {
            System.out.println("[SKIP] No badge available for testing");
            return;
        }

        try {
            gamificationService.awardBadge(testUserId, testBadgeId);
            
            boolean hasEarned = gamificationService.hasUserEarnedBadge(testUserId, testBadgeId);
            assertTrue(hasEarned, "User should have earned the badge");
            
            System.out.println("[DEBUG_LOG] Successfully awarded badge " + testBadgeId + " to user " + testUserId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test Get User Badges")
    public void testGetUserBadges() {
        try {
            List<Badge> userBadges = gamificationService.getUserBadges(testUserId);
            
            assertNotNull(userBadges, "User badges list should not be null");
            System.out.println("[DEBUG_LOG] User has " + userBadges.size() + " badges");
            
            if (testBadgeId > 0) {
                boolean found = userBadges.stream()
                        .anyMatch(b -> b.getId() == testBadgeId);
                assertTrue(found, "Test badge should be in user's badges");
            }
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    // ===== POINTS TESTS =====

    @Test
    @Order(9)
    @DisplayName("Test Get User Points")
    public void testGetUserPoints() {
        try {
            UserPoints userPoints = gamificationService.getUserPoints(testUserId);
            
            assertNotNull(userPoints, "User points should not be null");
            assertEquals(testUserId, userPoints.getUserId(), "User ID should match");
            assertTrue(userPoints.getPoints() >= 0, "Points should be non-negative");
            assertTrue(userPoints.getLevel() >= 1, "Level should be at least 1");
            
            System.out.println("[DEBUG_LOG] User " + testUserId + " has " + userPoints.getPoints() + " points, level " + userPoints.getLevel());
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test Add Points")
    public void testAddPoints() {
        try {
            UserPoints beforePoints = gamificationService.getUserPoints(testUserId);
            int pointsBefore = beforePoints.getPoints();
            
            gamificationService.addPoints(testUserId, 50, "TEST", "Test points addition");
            
            UserPoints afterPoints = gamificationService.getUserPoints(testUserId);
            int pointsAfter = afterPoints.getPoints();
            
            assertEquals(pointsBefore + 50, pointsAfter, "Points should increase by 50");
            
            System.out.println("[DEBUG_LOG] Successfully added 50 points. Before: " + pointsBefore + ", After: " + pointsAfter);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test Deduct Points")
    public void testDeductPoints() {
        try {
            UserPoints beforePoints = gamificationService.getUserPoints(testUserId);
            int pointsBefore = beforePoints.getPoints();
            
            if (pointsBefore >= 20) {
                gamificationService.deductPoints(testUserId, 20, "TEST", "Test points deduction");
                
                UserPoints afterPoints = gamificationService.getUserPoints(testUserId);
                int pointsAfter = afterPoints.getPoints();
                
                assertEquals(pointsBefore - 20, pointsAfter, "Points should decrease by 20");
                
                System.out.println("[DEBUG_LOG] Successfully deducted 20 points. Before: " + pointsBefore + ", After: " + pointsAfter);
            } else {
                System.out.println("[SKIP] Not enough points to test deduction");
            }
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test Get User Point History")
    public void testGetUserPointHistory() {
        try {
            List<PointTransaction> history = gamificationService.getUserPointHistory(testUserId);
            
            assertNotNull(history, "Point history should not be null");
            assertFalse(history.isEmpty(), "Point history should not be empty after previous tests");
            
            System.out.println("[DEBUG_LOG] User has " + history.size() + " point transactions");
            
            // Verify recent transactions
            boolean foundTestTransaction = history.stream()
                    .anyMatch(t -> "TEST".equals(t.getTransactionType()));
            assertTrue(foundTestTransaction, "Should find test transactions");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Test Complete Quiz")
    public void testCompleteQuiz() {
        try {
            UserPoints beforePoints = gamificationService.getUserPoints(testUserId);
            int pointsBefore = beforePoints.getPoints();
            
            // Complete quiz with passing score
            gamificationService.completeQuiz(testUserId, testQuizId, 85, 300);
            
            // Verify quiz completion
            boolean hasCompleted = gamificationService.hasUserCompletedQuiz(testUserId, testQuizId);
            assertTrue(hasCompleted, "User should have completed the quiz");
            
            // Verify points were awarded
            UserPoints afterPoints = gamificationService.getUserPoints(testUserId);
            int pointsAfter = afterPoints.getPoints();
            assertTrue(pointsAfter > pointsBefore, "Points should increase after passing quiz");
            
            System.out.println("[DEBUG_LOG] Successfully completed quiz. Points before: " + pointsBefore + ", after: " + pointsAfter);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @DisplayName("Test Get User Quiz History")
    public void testGetUserQuizHistory() {
        try {
            List<UserQuiz> history = gamificationService.getUserQuizHistory(testUserId);
            
            assertNotNull(history, "Quiz history should not be null");
            assertFalse(history.isEmpty(), "Quiz history should not be empty after previous test");
            
            System.out.println("[DEBUG_LOG] User has completed " + history.size() + " quizzes");
            
            boolean foundTestQuiz = history.stream()
                    .anyMatch(uq -> uq.getQuizId() == testQuizId);
            assertTrue(foundTestQuiz, "Should find test quiz in history");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    // ===== LEADERBOARD TESTS =====

    @Test
    @Order(15)
    @DisplayName("Test Get Top Users By Points")
    public void testGetTopUsersByPoints() {
        try {
            List<UserPoints> topUsers = gamificationService.getTopUsersByPoints(10);
            
            assertNotNull(topUsers, "Top users list should not be null");
            assertTrue(topUsers.size() <= 10, "Should return at most 10 users");
            
            // Verify ordering (descending by points)
            for (int i = 0; i < topUsers.size() - 1; i++) {
                assertTrue(topUsers.get(i).getPoints() >= topUsers.get(i + 1).getPoints(),
                        "Users should be ordered by points descending");
            }
            
            System.out.println("[DEBUG_LOG] Retrieved top " + topUsers.size() + " users by points");
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @Test
    @Order(16)
    @DisplayName("Test Get User Rank")
    public void testGetUserRank() {
        try {
            int rank = gamificationService.getUserRank(testUserId);
            
            assertTrue(rank > 0, "User rank should be positive");
            System.out.println("[DEBUG_LOG] User " + testUserId + " rank: " + rank);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    // ===== CLEANUP =====

    @Test
    @Order(17)
    @DisplayName("Test Delete Quiz")
    public void testDeleteQuiz() {
        try {
            gamificationService.deleteQuiz(testQuizId);
            
            Quiz deleted = gamificationService.getQuizById(testQuizId);
            assertNull(deleted, "Deleted quiz should not exist");
            
            System.out.println("[DEBUG_LOG] Successfully deleted quiz with ID: " + testQuizId);
            
        } catch (SQLException e) {
            fail("Exception in test: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("[TEST_CLEANUP] All GamificationService tests completed");
    }
}
