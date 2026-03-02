# Excel Export Feature

## Overview
Export courses data and analytics to Excel files (.xlsx) using Apache POI for easy data analysis and reporting.

## Features

### 1. Export Courses
Exports all course data to a single Excel file with columns:
- ID
- Title
- Category
- Difficulty
- Duration (minutes)
- Reward Points
- Status
- Visibility
- Created At

### 2. Export Analytics
Exports comprehensive analytics to a multi-sheet Excel file:

#### Sheet 1: Course Statistics
- Course Title
- Category
- Difficulty
- Views (from course_history)
- Likes
- Dislikes
- Reports
- Engagement Rate (calculated)

#### Sheet 2: Category Distribution
- Category name
- Course count
- Percentage of total

#### Sheet 3: Difficulty Distribution
- Difficulty level
- Course count
- Percentage of total

#### Sheet 4: Course Reports
- Report ID
- Course Name
- User ID
- Reason
- Description
- Status
- Date

## Usage

### From Course Management Page:

1. Click **"📊 Export Courses"** button
   - Exports all courses to Excel
   - File saved to `exports/courses_YYYYMMDD_HHMMSS.xlsx`
   - Opens file location automatically

2. Click **"📈 Export Analytics"** button
   - Exports comprehensive analytics
   - File saved to `exports/course_analytics_YYYYMMDD_HHMMSS.xlsx`
   - Opens file location automatically

## File Format

- **Format**: Excel 2007+ (.xlsx)
- **Location**: `exports/` folder in project root
- **Naming**: Timestamped files (e.g., `courses_20260301_143022.xlsx`)
- **Styling**: 
  - Blue header row with white bold text
  - Bordered cells
  - Auto-sized columns
  - Professional formatting

## Excel Features

- **Headers**: Bold, colored, centered
- **Data**: Clean, bordered cells
- **Auto-sizing**: Columns automatically sized to content
- **Multiple sheets**: Analytics file has 4 sheets
- **Formulas**: Engagement rate calculated automatically

## Dependencies

Added to `pom.xml`:
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

## API Usage

### Programmatic Export:

```java
ExcelExportService excelService = new ExcelExportService();

// Export courses
File coursesFile = excelService.exportCoursesToExcel();

// Export analytics
File analyticsFile = excelService.exportCourseAnalytics();
```

## Analytics Calculations

### Engagement Rate
```
Engagement Rate = ((Likes + Dislikes) / Views) × 100%
```

### Category/Difficulty Distribution
```
Percentage = (Count in Category / Total Courses) × 100%
```

## File Structure

```
exports/
├── courses_20260301_143022.xlsx
├── course_analytics_20260301_143530.xlsx
└── ...
```

## Use Cases

1. **Data Analysis**: Import into data analysis tools
2. **Reporting**: Share with stakeholders
3. **Backup**: Keep offline records
4. **Auditing**: Track course changes over time
5. **Planning**: Analyze trends and patterns

## Future Enhancements

- Export user data
- Export quiz statistics
- Export badge achievements
- Custom date range filters
- Scheduled automatic exports
- Email export files
- Export to CSV format
- Chart generation in Excel

## Troubleshooting

### "File not found" error
- Check that `exports/` folder exists
- Verify write permissions

### "Out of memory" error
- Large datasets may require more heap space
- Increase JVM memory: `-Xmx2g`

### Excel file won't open
- Ensure Apache POI dependencies are correct
- Check file isn't corrupted
- Try opening with different Excel version

## Security Notes

- Exported files contain sensitive data
- Store exports securely
- Don't commit exports to version control
- Add `exports/` to `.gitignore`
