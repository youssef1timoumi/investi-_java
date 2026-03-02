# 🔧 Recommendation Engine - Troubleshooting Guide

## Issue: Recommendations Section Not Showing

If you don't see the recommendations section in the Course Catalog, follow these steps:

### Step 1: Rebuild the Application

The code has been updated, but you need to rebuild:

**In IntelliJ IDEA:**
1. Click **Build** → **Rebuild Project**
2. Wait for the build to complete
3. Run the application again

**Or use Maven:**
```bash
mvn clean compile
```

### Step 2: Check Console Output

When you open the Course Catalog, look for these debug messages in the console:

```
=== Loading Recommendations ===
User history size: X
Loading recommendations for new user...
=== Getting recommendations for new user ===
Executing query for published courses...
Found course ID: X (popularity: X, likes: X)
  Added: [Course Title]
Total recommendations: X
Found X recommendations
Creating card for: [Course Title]
✅ Recommendations loaded successfully!
```

### Step 3: Common Issues and Solutions

#### Issue: "recommendationsContainer is null!"
**Cause**: FXML not loaded properly
**Solution**: 
- Make sure you rebuilt the project
- Check that `CourseCatalogView.fxml` has the `fx:id="recommendationsContainer"` attribute
- Restart the application

#### Issue: "No recommendations found - hiding section"
**Cause**: No courses in database or all courses have status != 'published'
**Solution**:
```sql
-- Check if you have courses
SELECT COUNT(*) FROM course;

-- Check course status
SELECT id, title, status FROM course LIMIT 10;

-- Update courses to published if needed
UPDATE course SET status = 'published' WHERE status IS NULL OR status = '';
```

#### Issue: "Error loading recommendations: SQLException"
**Cause**: Database connection or query issue
**Solution**:
- Check database connection is working
- Verify tables exist: `course`, `course_history`, `course_interactions`
- Check console for specific SQL error message

### Step 4: Verify Database Tables

Run these queries to ensure data exists:

```sql
-- Check courses
SELECT COUNT(*) as total_courses FROM course;

-- Check published courses
SELECT COUNT(*) as published_courses FROM course WHERE status = 'published';

-- Check course history
SELECT COUNT(*) as history_records FROM course_history;

-- Check if user has history
SELECT COUNT(*) as user_history FROM course_history WHERE user_id = 1;
```

### Step 5: Force Show Recommendations

If you have courses but they're not showing, try this temporary fix:

**Option A: Set all courses to published**
```sql
UPDATE course SET status = 'published';
```

**Option B: Check the fallback is working**
The code now has a fallback that shows ANY courses if no published courses exist. Check console for:
```
No published courses found, trying all courses...
Fallback added: [Course Title]
```

### Step 6: Manual Test

Create a simple test to verify the service works:

```java
// In your main method or a test
RecommendationService recService = new RecommendationService();
try {
    List<Course> recs = recService.getRecommendationsForNewUser(6);
    System.out.println("Got " + recs.size() + " recommendations");
    for (Course c : recs) {
        System.out.println("  - " + c.getTitle());
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### Step 7: Check FXML Binding

Verify the FXML file has these elements:

```xml
<!-- In CourseCatalogView.fxml -->
<VBox fx:id="recommendationsSection" spacing="15" visible="true" managed="true">
    <HBox fx:id="recommendationsContainer" spacing="15" alignment="CENTER_LEFT">
        <!-- Recommendations loaded dynamically -->
    </HBox>
</VBox>
```

### Step 8: Verify Controller Fields

Check that `CourseCatalogController.java` has:

```java
@FXML private VBox recommendationsSection;
@FXML private HBox recommendationsContainer;
private RecommendationService recommendationService;

public void initialize() {
    // ...
    recommendationService = new RecommendationService();
    loadRecommendations();  // This should be called!
    // ...
}
```

## Still Not Working?

### Check These Files Were Modified:

1. ✅ `src/main/java/edu/connections3a8/services/RecommendationService.java` - Created
2. ✅ `src/main/java/edu/connections3a8/controllers/CourseCatalogController.java` - Modified
3. ✅ `src/main/resources/CourseCatalogView.fxml` - Modified

### Verify Changes:

**In CourseCatalogController.java**, search for:
- `private RecommendationService recommendationService;`
- `recommendationService = new RecommendationService();`
- `loadRecommendations();`
- `private void loadRecommendations()`

**In CourseCatalogView.fxml**, search for:
- `fx:id="recommendationsSection"`
- `fx:id="recommendationsContainer"`
- `✨ Recommended for You`

## Quick Fix: Restart Everything

1. **Close** the application completely
2. **Rebuild** the project in your IDE
3. **Clean** build artifacts if needed
4. **Run** the application again
5. **Navigate** to Course Catalog
6. **Check** console output for debug messages

## Expected Behavior

When working correctly, you should see:

1. **In Course Catalog**: A section titled "✨ Recommended for You" with horizontal scrolling course cards
2. **In Console**: Debug messages showing recommendations being loaded
3. **Cards**: 6 course cards with golden borders, thumbnails, and "View Course" buttons

## Need More Help?

If still not working, provide:
1. Console output when opening Course Catalog
2. Result of `SELECT COUNT(*) FROM course;`
3. Result of `SELECT status, COUNT(*) FROM course GROUP BY status;`
4. Screenshot of the Course Catalog page

---

**Remember**: Always rebuild after code changes!
