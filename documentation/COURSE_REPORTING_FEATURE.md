# Course Reporting System

## Overview
Complete course reporting system allowing users to report problematic courses and administrators to manage reports.

## Database Setup

Run the SQL migration:
```sql
-- See COURSE_REPORT_MIGRATION.sql
CREATE TABLE course_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    report_reason VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
```

## Features

### 1. User Reporting (Course Catalog)
- **Location**: Course Catalog page, each course card
- **Button**: 🚩 Report button
- **Functionality**:
  - Opens dialog with dropdown for report reason
  - Predefined reasons:
    - Inappropriate Content
    - Misleading Information
    - Copyright Violation
    - Spam or Scam
    - Technical Issues
    - Other
  - Text area for detailed description
  - Saves report to database with pending status

### 2. Admin Report Management (Course Management)
- **Location**: Course Management page header
- **Button**: 📋 View Reports
- **Functionality**:
  - Shows all course reports in a dialog
  - Filter by status: All, Pending, Reviewed, Resolved
  - Each report card shows:
    - Course name
    - Report reason
    - Date submitted
    - Current status (color-coded)
    - View Details button

### 3. Report Details View
- **Accessed from**: Report card "View Details" button
- **Shows**:
  - Report ID
  - Course name and ID
  - User ID who submitted
  - Date and time
  - Report reason
  - Full description
  - Current status
- **Actions**:
  - Update Status (pending → reviewed → resolved → dismissed)
  - Delete Report
  - Close

## Report Statuses

- **pending** (Orange): New report, not yet reviewed
- **reviewed** (Blue): Admin has reviewed the report
- **resolved** (Green): Issue has been resolved
- **dismissed** (Red): Report was invalid or not actionable

## Files Created/Modified

### New Files:
- `src/main/java/edu/connections3a8/entities/CourseReport.java` - Entity class
- `COURSE_REPORT_MIGRATION.sql` - Database schema

### Modified Files:
- `src/main/java/edu/connections3a8/services/CouseService.java` - Added report methods
- `src/main/java/edu/connections3a8/controllers/CourseCatalogController.java` - Updated report dialog
- `src/main/java/edu/connections3a8/controllers/CourseController.java` - Added report management
- `src/main/resources/CourseForm.fxml` - Added View Reports button
- `src/main/resources/coursesForm.css` - Added warning button styles

## Usage

### For Users:
1. Browse courses in the catalog
2. Click 🚩 Report button on any course
3. Select reason from dropdown
4. Provide detailed description
5. Submit report

### For Administrators:
1. Go to Course Management page
2. Click "📋 View Reports" button
3. Filter reports by status if needed
4. Click "View Details" on any report
5. Update status or delete report as needed

## API Methods (CouseService)

```java
// Submit a new report
void submitCourseReport(CourseReport report)

// Get all reports
List<CourseReport> getAllReports()

// Get reports by status
List<CourseReport> getReportsByStatus(String status)

// Get specific report
CourseReport getReportById(long id)

// Update report status
void updateReportStatus(long reportId, String status)

// Delete report
void deleteReport(long reportId)

// Get report count for a course
int getReportCountByCourse(long courseId)
```

## Dark Mode Support
All report UI components support dark mode with appropriate color schemes.
