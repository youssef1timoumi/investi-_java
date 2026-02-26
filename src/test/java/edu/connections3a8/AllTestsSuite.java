package edu.connections3a8;

import edu.connections3a8.entities.*;
import edu.connections3a8.services.CourseServiceTest;
import edu.connections3a8.services.GamificationServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite to run all tests in order
 * - Entity tests (unit tests for POJOs)
 * - Service tests (integration tests with database)
 */
@Suite
@SelectClasses({
    // Entity Tests (Unit Tests)
    CourseTest.class,
    QuizTest.class,
    BadgeTest.class,
    UserPointsTest.class,
    PointTransactionTest.class,
    UserBadgeTest.class,
    UserQuizTest.class,
    QuestionTest.class,
    
    // Service Tests (Integration Tests)
    CourseServiceTest.class,
    GamificationServiceTest.class
})
public class AllTestsSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
