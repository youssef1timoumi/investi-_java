package edu.connections3a8.interfaces;

import edu.connections3a8.entities.*;
import java.sql.SQLException;
import java.util.List;

public interface IGamification {

    /* ===== POINTS MANAGEMENT ===== */
    void addPoints(int userId, int points, String transactionType, String description) throws SQLException;
    void deductPoints(int userId, int points, String transactionType, String description) throws SQLException;
    UserPoints getUserPoints(int userId) throws SQLException;
    List<PointTransaction> getUserPointHistory(int userId) throws SQLException;
    
    /* ===== BADGE MANAGEMENT ===== */
    void awardBadge(int userId, long badgeId) throws SQLException;
    List<Badge> getUserBadges(int userId) throws SQLException;
    List<Badge> getAllBadges() throws SQLException;
    boolean hasUserEarnedBadge(int userId, long badgeId) throws SQLException;
    
    /* ===== QUIZ MANAGEMENT ===== */
    void addQuiz(Quiz quiz) throws SQLException;
    void updateQuiz(Quiz quiz, long id) throws SQLException;
    void deleteQuiz(long id) throws SQLException;
    Quiz getQuizById(long id) throws SQLException;
    void completeQuiz(int userId, long quizId, int score, int timeTaken) throws SQLException;
    List<Quiz> getAllQuizzes() throws SQLException;
    List<Quiz> getQuizzesByCategory(String category) throws SQLException;
    List<UserQuiz> getUserQuizHistory(int userId) throws SQLException;
    boolean hasUserCompletedQuiz(int userId, long quizId) throws SQLException;
    
    /* ===== LEADERBOARD ===== */
    List<UserPoints> getTopUsersByPoints(int limit) throws SQLException;
    List<UserPoints> getTopUsersByLevel(int limit) throws SQLException;
    int getUserRank(int userId) throws SQLException;
}
