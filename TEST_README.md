# Unit Tests Documentation

## Overview
This project includes comprehensive unit tests for entities (POJOs) and services (database operations) using JUnit 5.

## Test Structure

### Entity Tests (src/test/java/edu/connections3a8/entities)
Unit tests for Plain Old Java Objects (POJOs) - testing constructors, getters, setters, equals, hashCode, and toString methods.

### Service Tests (src/test/java/edu/connections3a8/services)
Integration tests for database operations - testing CRUD operations and business logic.

## Test Classes

### Entity Tests (Unit Tests)

#### 1. CourseTest
Tests for Course entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor (3 params)
- ✅ Parameterized Constructor (Full)
- ✅ All Getters and Setters
- ✅ Equals Method
- ✅ HashCode Method
- ✅ Timestamp Getters

#### 2. QuizTest
Tests for Quiz entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor (with defaults)
- ✅ All Getters and Setters
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method

#### 3. BadgeTest
Tests for Badge entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor
- ✅ All Getters and Setters
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method
- ✅ Badge with Zero Points Required

#### 4. UserPointsTest
Tests for UserPoints entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor (with defaults)
- ✅ All Getters and Setters
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method
- ✅ Points Progression Logic

#### 5. PointTransactionTest
Tests for PointTransaction entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor
- ✅ All Getters and Setters (including negative points)
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method
- ✅ Points Addition Transaction
- ✅ Points Deduction Transaction
- ✅ Transaction Without Reference
- ✅ Transaction With Reference

#### 6. UserBadgeTest
Tests for UserBadge entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor
- ✅ All Getters and Setters
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method
- ✅ Multiple Badges for Same User

#### 7. UserQuizTest
Tests for UserQuiz entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor
- ✅ All Getters and Setters
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method
- ✅ Passing Quiz Scenario
- ✅ Failing Quiz Scenario
- ✅ Multiple Quiz Attempts

#### 8. QuestionTest
Tests for Question entity:
- ✅ Default Constructor
- ✅ Parameterized Constructor
- ✅ All Getters and Setters (ID, Quiz ID, Question Text, Options, Correct Answer)
- ✅ Equals Method
- ✅ HashCode Method
- ✅ ToString Method
- ✅ Correct Answer Validation (1-4 range)
- ✅ Complete Question Setup

### Service Tests (Integration Tests)

#### 1. CourseServiceTest
Tests for course management operations:
- ✅ Create Course (addCourse)
- ✅ Get All Courses (getAllCourses)
- ✅ Get Course By ID (getCourseById)
- ✅ Get Course By Slug (getCourseBySlug)
- ✅ Get Courses By Category (getCoursesByCategory)
- ✅ Get Courses By Difficulty (getCoursesByDifficulty)
- ✅ Update Course (updateCourse)
- ✅ Publish Course (publishCourse)
- ✅ Archive Course (archiveCourse)
- ✅ Delete Course (deleteCourse)

### 2. GamificationServiceTest
Tests for gamification features:

**Quiz Management:**
- ✅ Create Quiz (addQuiz)
- ✅ Get All Quizzes (getAllQuizzes)
- ✅ Get Quiz By ID (getQuizById)
- ✅ Get Quizzes By Category (getQuizzesByCategory)
- ✅ Update Quiz (updateQuiz)
- ✅ Complete Quiz (completeQuiz)
- ✅ Get User Quiz History (getUserQuizHistory)
- ✅ Has User Completed Quiz (hasUserCompletedQuiz)
- ✅ Delete Quiz (deleteQuiz)

**Badge Management:**
- ✅ Get All Badges (getAllBadges)
- ✅ Award Badge (awardBadge)
- ✅ Get User Badges (getUserBadges)
- ✅ Has User Earned Badge (hasUserEarnedBadge)

**Points Management:**
- ✅ Get User Points (getUserPoints)
- ✅ Add Points (addPoints)
- ✅ Deduct Points (deductPoints)
- ✅ Get User Point History (getUserPointHistory)

**Leaderboard:**
- ✅ Get Top Users By Points (getTopUsersByPoints)
- ✅ Get User Rank (getUserRank)

## Running Tests

### Prerequisites
1. Ensure your database is running and accessible
2. Update database connection settings in `MyConnection.java` if needed
3. Make sure you have at least one user in the database (user_id = 1 is used for testing)

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
# Run Entity Tests
mvn test -Dtest=CourseTest
mvn test -Dtest=QuizTest
mvn test -Dtest=BadgeTest

# Run Service Tests
mvn test -Dtest=CourseServiceTest
mvn test -Dtest=GamificationServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=CourseTest#testDefaultConstructor
mvn test -Dtest=CourseServiceTest#testCreateCourse
```

### Run Test Suite
```bash
mvn test -Dtest=AllTestsSuite
```

## Test Execution Order

### Entity Tests (Unit Tests)
- Run independently, no specific order required
- Fast execution (no database access)
- Test object creation, getters, setters, equals, hashCode

### Service Tests (Integration Tests)
Tests are executed in a specific order using `@Order` annotations to ensure:
1. Create operations run first
2. Read operations verify created data
3. Update operations modify existing data
4. Delete operations clean up at the end

## Test Output
Each test provides debug logs showing:
- Created entity IDs
- Retrieved data verification
- Operation success confirmations
- Transaction details

Example output:
```
[TEST_SETUP] CourseService initialized
[DEBUG_LOG] Created Course with ID: 42
[DEBUG_LOG] Verified: Course with ID 42 exists with correct data
[DEBUG_LOG] Retrieved 15 courses from database
[DEBUG_LOG] Successfully updated course with ID: 42
[DEBUG_LOG] Successfully deleted course with ID: 42
[TEST_CLEANUP] All CourseService tests completed
```

## Important Notes

### Database State
- Tests create and clean up their own test data
- Some tests require existing data (e.g., user_id = 1 for gamification tests)
- Tests are designed to be idempotent and can be run multiple times

### Test Isolation
- Each test class uses `@TestMethodOrder` to control execution order
- Tests within a class may depend on previous tests (e.g., create before update)
- Different test classes are independent of each other

### Assertions
All tests use JUnit 5 assertions:
- `assertTrue()` - Verify boolean conditions
- `assertFalse()` - Verify negative conditions
- `assertEquals()` - Verify exact matches
- `assertNotNull()` - Verify object existence
- `assertNull()` - Verify object deletion
- `fail()` - Explicitly fail with error message

## Troubleshooting

### Connection Issues
If tests fail with connection errors:
1. Check database is running
2. Verify connection settings in `MyConnection.java`
3. Ensure database schema is up to date

### Missing Data
If tests fail due to missing data:
1. Ensure at least one user exists (id = 1)
2. Run database initialization scripts
3. Check foreign key constraints

### Test Failures
If specific tests fail:
1. Check the debug logs for details
2. Verify database state before running tests
3. Run tests individually to isolate issues
4. Check for database constraint violations

## Best Practices

1. **Always run tests before committing code**
2. **Review test output for warnings**
3. **Keep test data separate from production data**
4. **Update tests when modifying service methods**
5. **Add new tests for new features**

## Coverage
Current test coverage includes:

### Entity Tests (Unit Tests)
- ✅ All constructors (default and parameterized)
- ✅ All getters and setters
- ✅ Equals and hashCode methods
- ✅ ToString methods
- ✅ Business logic scenarios

### Service Tests (Integration Tests)
- ✅ All CRUD operations
- ✅ All business logic methods
- ✅ Transaction handling
- ✅ Error conditions
- ✅ Data validation
- ✅ Complex queries (joins, aggregations)

## Future Enhancements
- Add integration tests for controllers
- Add performance tests for large datasets
- Add concurrent access tests
- Add validation error tests
- Mock database for faster tests
