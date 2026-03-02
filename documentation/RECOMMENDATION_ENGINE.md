# 🎯 Recommendation Engine Feature

## Overview
Intelligent course recommendation system that suggests personalized courses to users based on their learning history, preferences, and behavior patterns.

## Features

### 1. Personalized Recommendations (Course Catalog)
- **Location**: Top of Course Catalog page
- **Section Title**: "✨ Recommended for You"
- **Display**: Horizontal scrollable row of 6 course cards
- **Algorithms Used**:
  - Content-based filtering (based on user's course history)
  - Collaborative filtering (based on similar users)
  - Popular courses in preferred categories
  - Trending courses (most viewed in last 30 days)

### 2. Similar Courses (Course Content Page)
- **Location**: Bottom of Course Content page
- **Section Title**: "🎓 Students Who Took This Also Took"
- **Display**: Horizontal scrollable row of 5 course cards
- **Algorithm**: Co-occurrence analysis (courses taken by same users)

## Recommendation Strategies

### Content-Based Filtering
Recommends courses similar to what the user has viewed:
- Analyzes user's most viewed categories
- Considers difficulty level preferences
- Suggests courses matching user's learning patterns

### Collaborative Filtering
Recommends courses based on similar users:
- Finds users with similar course viewing patterns
- Suggests courses that similar users have viewed
- Uses co-occurrence analysis for recommendations

### Popular in Preferred Categories
Recommends trending courses in user's favorite categories:
- Identifies user's top 2 categories
- Shows most popular courses in those categories
- Weighted by view count and engagement

### Trending Courses
Recommends currently popular courses:
- Courses with most views in last 30 days
- Global trending across all categories
- Helps users discover what's hot

### New User Recommendations
For users without history:
- Shows most popular courses overall
- Weighted by views and likes
- Helps new users get started

## Technical Implementation

### Service Layer
**File**: `src/main/java/edu/connections3a8/services/RecommendationService.java`

**Key Methods**:
```java
// Get personalized recommendations for user
List<Course> getRecommendationsForUser(int userId, int limit)

// Get similar courses based on co-occurrence
List<Course> getSimilarCourseRecommendations(long courseId, int limit)

// Get recommendations for new users
List<Course> getRecommendationsForNewUser(int limit)
```

### Controller Integration

#### CourseCatalogController
- Added `recommendationService` field
- Added `recommendationsSection` and `recommendationsContainer` FXML fields
- Added `loadRecommendations()` method
- Added `createRecommendationCard()` method
- Recommendations load automatically on page initialization

#### CourseContentController
- Added `recommendationService` field
- Added `similarCoursesSection` and `similarCoursesContainer` FXML fields
- Added `loadSimilarCourses()` method
- Added `createSimilarCourseCard()` method
- Similar courses load when course content is displayed

### UI Components

#### Recommendation Card (Catalog)
- **Size**: 220x280px
- **Style**: Golden border (#9B7E46)
- **Content**:
  - Thumbnail image (196x110px)
  - Course title (bold, 13px)
  - Points and difficulty badges
  - "View Course" button (golden gradient)

#### Similar Course Card (Content Page)
- **Size**: 220x280px
- **Style**: Blue border (#456990)
- **Content**:
  - Thumbnail image (196x110px)
  - Course title (bold, 13px)
  - Points and difficulty badges
  - "View Course" button (blue gradient)

## Database Requirements

### Required Tables
- `course` - Course information
- `course_history` - User course viewing history
- `course_interactions` - Likes/dislikes
- `personne` - User information

### Key Queries
```sql
-- Get user's preferred categories
SELECT c.category, COUNT(*) as view_count 
FROM course_history ch 
JOIN course c ON ch.course_id = c.id 
WHERE ch.user_id = ? 
GROUP BY c.category 
ORDER BY view_count DESC;

-- Find similar users
SELECT ch2.user_id, COUNT(DISTINCT ch2.course_id) as common_courses 
FROM course_history ch1 
JOIN course_history ch2 ON ch1.course_id = ch2.course_id 
WHERE ch1.user_id = ? AND ch2.user_id != ? 
GROUP BY ch2.user_id 
ORDER BY common_courses DESC;

-- Get trending courses
SELECT course_id, COUNT(*) as recent_views 
FROM course_history 
WHERE visited_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) 
GROUP BY course_id 
ORDER BY recent_views DESC;

-- Get similar courses (co-occurrence)
SELECT ch2.course_id, COUNT(*) as co_occurrence 
FROM course_history ch1 
JOIN course_history ch2 ON ch1.user_id = ch2.user_id 
WHERE ch1.course_id = ? AND ch2.course_id != ? 
GROUP BY ch2.course_id 
ORDER BY co_occurrence DESC;
```

## User Experience

### For New Users
- Shows most popular courses overall
- Helps users discover high-quality content
- No personalization until history is built

### For Existing Users
- Personalized recommendations based on history
- Discovers courses in preferred categories
- Finds courses similar users enjoyed
- Sees trending courses in their interests

### Visibility Rules
- Recommendations section hidden if no recommendations available
- Similar courses section hidden if no similar courses found
- Graceful degradation on errors

## Dark Mode Support
Both recommendation sections fully support dark mode:
- **Light Mode**: White cards with colored borders
- **Dark Mode**: Dark cards (#161630) with semi-transparent borders
- Automatic theme switching with rest of application

## Performance Considerations
- Recommendations limited to 6 courses (catalog) and 5 courses (content page)
- Queries optimized with proper indexing
- Graceful error handling prevents page crashes
- Sections hidden when no data available

## Future Enhancements
- Cache recommendations for better performance
- Add machine learning for better predictions
- Track recommendation click-through rates
- A/B test different recommendation algorithms
- Add "Why recommended?" explanations
- Implement real-time recommendation updates
- Add user feedback on recommendations

## Files Modified

### Java Files
- `src/main/java/edu/connections3a8/services/RecommendationService.java` (NEW)
- `src/main/java/edu/connections3a8/controllers/CourseCatalogController.java`
- `src/main/java/edu/connections3a8/controllers/CourseContentController.java`

### FXML Files
- `src/main/resources/CourseCatalogView.fxml`
- `src/main/resources/CourseContentView.fxml`

### Documentation
- `documentation/RECOMMENDATION_ENGINE.md` (this file)

## Testing Recommendations

### Test Scenarios
1. **New User**: Verify popular courses shown
2. **User with History**: Verify personalized recommendations
3. **Course with Similar Courses**: Verify similar courses shown
4. **Course without Similar Courses**: Verify section hidden
5. **Dark Mode**: Verify styling in both themes
6. **Navigation**: Click recommendations and verify navigation works

### Test Data Requirements
- Multiple users with course viewing history
- Courses in different categories
- Course interactions (likes/dislikes)
- Recent course views (last 30 days)

## Success Metrics
- Recommendation click-through rate
- User engagement with recommended courses
- Course discovery rate
- User satisfaction with recommendations
- Time spent on platform

---

**Status**: ✅ Implemented and Integrated
**Version**: 1.0
**Date**: March 2, 2026
