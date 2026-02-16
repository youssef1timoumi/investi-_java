package edu.connections3a8.services;

import edu.connections3a8.entities.Course;
import edu.connections3a8.interfaces.ICourse;
import edu.connections3a8.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouseService implements ICourse {

    private Connection cnx;

    public CouseService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void addCourse(Course course) throws SQLException {
        String query = "INSERT INTO course (title, slug, description, content_url, content_type, " +
                "difficulty_level, category, language, estimated_duration, reward_points, " +
                "status, visibility, thumbnail_url, published_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, course.getTitle());
        pst.setString(2, course.getSlug());
        pst.setString(3, course.getDescription());
        pst.setString(4, course.getContentUrl());
        pst.setString(5, course.getContentType() != null ? course.getContentType() : "video");
        pst.setString(6, course.getDifficultyLevel() != null ? course.getDifficultyLevel() : "beginner");
        pst.setString(7, course.getCategory());
        pst.setString(8, course.getLanguage());
        pst.setInt(9, course.getEstimatedDuration());
        pst.setInt(10, course.getRewardPoints());
        pst.setString(11, course.getStatus() != null ? course.getStatus() : "published");
        pst.setString(12, course.getVisibility() != null ? course.getVisibility() : "public");
        pst.setString(13, course.getThumbnailUrl());
        
        pst.executeUpdate();
    }

    @Override
    public void addDraftCourse(Course course) throws SQLException {
        String query = "INSERT INTO course (title, slug, description, content_url, content_type, " +
                "difficulty_level, category, language, estimated_duration, reward_points, " +
                "status, visibility, thumbnail_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'draft', 'private', ?)";
        
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, course.getTitle());
        pst.setString(2, course.getSlug());
        pst.setString(3, course.getDescription());
        pst.setString(4, course.getContentUrl());
        pst.setString(5, course.getContentType());
        pst.setString(6, course.getDifficultyLevel());
        pst.setString(7, course.getCategory());
        pst.setString(8, course.getLanguage());
        pst.setInt(9, course.getEstimatedDuration());
        pst.setInt(10, course.getRewardPoints());
        pst.setString(11, course.getThumbnailUrl());
        
        pst.executeUpdate();
    }

    @Override
    public Course getCourseById(long id) throws SQLException {
        String query = "SELECT * FROM course WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setLong(1, id);
        
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return mapResultSetToCourse(rs);
        }
        return null;
    }

    @Override
    public Course getCourseBySlug(String slug) throws SQLException {
        String query = "SELECT * FROM course WHERE slug = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, slug);
        
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return mapResultSetToCourse(rs);
        }
        return null;
    }

    @Override
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        
        while (rs.next()) {
            courses.add(mapResultSetToCourse(rs));
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByDifficulty(String difficulty) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course WHERE difficulty_level = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, difficulty);
        
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            courses.add(mapResultSetToCourse(rs));
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByCategory(String category) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course WHERE category = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, category);
        
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            courses.add(mapResultSetToCourse(rs));
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByStatus(String status) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course WHERE status = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, status);
        
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            courses.add(mapResultSetToCourse(rs));
        }
        return courses;
    }

    @Override
    public List<Course> getPublicCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course WHERE visibility = 'public' AND status = 'published'";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        
        while (rs.next()) {
            courses.add(mapResultSetToCourse(rs));
        }
        return courses;
    }

    @Override
    public void updateCourse(Course course, long id) throws SQLException {
        String query = "UPDATE course SET title = ?, slug = ?, description = ?, content_url = ?, " +
                "content_type = ?, difficulty_level = ?, category = ?, language = ?, " +
                "estimated_duration = ?, reward_points = ?, status = ?, visibility = ?, " +
                "thumbnail_url = ? WHERE id = ?";
        
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, course.getTitle());
        pst.setString(2, course.getSlug());
        pst.setString(3, course.getDescription());
        pst.setString(4, course.getContentUrl());
        pst.setString(5, course.getContentType());
        pst.setString(6, course.getDifficultyLevel());
        pst.setString(7, course.getCategory());
        pst.setString(8, course.getLanguage());
        pst.setInt(9, course.getEstimatedDuration());
        pst.setInt(10, course.getRewardPoints());
        pst.setString(11, course.getStatus());
        pst.setString(12, course.getVisibility());
        pst.setString(13, course.getThumbnailUrl());
        pst.setLong(14, id);
        
        pst.executeUpdate();
    }

    @Override
    public void updateCourseStatus(long id, String status) throws SQLException {
        String query = "UPDATE course SET status = ? WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, status);
        pst.setLong(2, id);
        pst.executeUpdate();
    }

    @Override
    public void publishCourse(long id) throws SQLException {
        String query = "UPDATE course SET status = 'published', visibility = 'public', " +
                "published_at = NOW() WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setLong(1, id);
        pst.executeUpdate();
    }

    @Override
    public void deleteCourse(long id) throws SQLException {
        String query = "DELETE FROM course WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setLong(1, id);
        pst.executeUpdate();
    }

    @Override
    public void archiveCourse(long id) throws SQLException {
        String query = "UPDATE course SET status = 'archived', visibility = 'private' WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setLong(1, id);
        pst.executeUpdate();
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setTitle(rs.getString("title"));
        course.setSlug(rs.getString("slug"));
        course.setDescription(rs.getString("description"));
        course.setContentUrl(rs.getString("content_url"));
        course.setContentType(rs.getString("content_type"));
        course.setDifficultyLevel(rs.getString("difficulty_level"));
        course.setCategory(rs.getString("category"));
        course.setLanguage(rs.getString("language"));
        course.setEstimatedDuration(rs.getInt("estimated_duration"));
        course.setRewardPoints(rs.getInt("reward_points"));
        course.setStatus(rs.getString("status"));
        course.setVisibility(rs.getString("visibility"));
        course.setThumbnailUrl(rs.getString("thumbnail_url"));
        return course;
    }
}
