package edu.connections3a8.tests;

import edu.connections3a8.entities.*;
import edu.connections3a8.services.*;

import java.sql.SQLException;
import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        
        System.out.println("========== TESTING GAMIFICATION SYSTEM ==========\n");
        
        // Initialize services
        PersonneService personneService = new PersonneService();
        GamificationService gamificationService = new GamificationService();
        CouseService courseService = new CouseService();
        
        try {
            // ===== TEST 1: Add a new user =====
            System.out.println("--- TEST 1: Adding New User ---");
            Personne p1 = new Personne();
            p1.setNom("heshem");
            p1.setPrenom("kozgodli");
            personneService.addEntity2(p1);
            System.out.println("✓ User added successfully: " + p1.getNom() + " " + p1.getPrenom());
            
            // Get user ID (assuming user ID 1 for testing)
            int userId = 1;
            
            // ===== TEST 2: Check User Points =====
            System.out.println("\n--- TEST 2: Checking User Points ---");
            UserPoints userPoints = gamificationService.getUserPoints(userId);
            System.out.println("✓ User Points: " + userPoints.getPoints());
            System.out.println("✓ User Level: " + userPoints.getLevel());
            System.out.println("✓ Total Earned Points: " + userPoints.getTotalEarnedPoints());
            
            // ===== TEST 3: Add Points to User =====
            System.out.println("\n--- TEST 3: Adding Points to User ---");
            gamificationService.addPoints(userId, 150, "COURSE_COMPLETION", "Completed Introduction to Java");
            System.out.println("✓ Added 150 points for course completion");
            
            userPoints = gamificationService.getUserPoints(userId);
            System.out.println("✓ Updated Points: " + userPoints.getPoints());
            System.out.println("✓ Updated Level: " + userPoints.getLevel());
            
            // ===== TEST 4: Award Badge to User =====
            System.out.println("\n--- TEST 4: Awarding Badge to User ---");
            gamificationService.awardBadge(userId, 1); // Award "First Steps" badge
            System.out.println("✓ Badge awarded successfully");
            
            // ===== TEST 5: Get User Badges =====
            System.out.println("\n--- TEST 5: Getting User Badges ---");
            List<Badge> userBadges = gamificationService.getUserBadges(userId);
            System.out.println("✓ User has " + userBadges.size() + " badge(s):");
            for (Badge badge : userBadges) {
                System.out.println("  - " + badge.getName() + ": " + badge.getDescription());
            }
            
            // ===== TEST 6: Get All Badges =====
            System.out.println("\n--- TEST 6: Getting All Available Badges ---");
            List<Badge> allBadges = gamificationService.getAllBadges();
            System.out.println("✓ Total available badges: " + allBadges.size());
            for (Badge badge : allBadges) {
                System.out.println("  - " + badge.getName() + " (Requires: " + badge.getPointsRequired() + " points)");
            }
            
            // ===== TEST 7: Complete a Quiz =====
            System.out.println("\n--- TEST 7: Completing a Quiz ---");
            gamificationService.completeQuiz(userId, 1, 85, 450); // Quiz ID 1, score 85%, time 450 seconds
            System.out.println("✓ Quiz completed successfully");
            
            userPoints = gamificationService.getUserPoints(userId);
            System.out.println("✓ Points after quiz: " + userPoints.getPoints());
            System.out.println("✓ Level after quiz: " + userPoints.getLevel());
            
            // ===== TEST 8: Get All Quizzes =====
            System.out.println("\n--- TEST 8: Getting All Available Quizzes ---");
            List<Quiz> allQuizzes = gamificationService.getAllQuizzes();
            System.out.println("✓ Total available quizzes: " + allQuizzes.size());
            for (Quiz quiz : allQuizzes) {
                System.out.println("  - " + quiz.getTitle() + " (Reward: " + quiz.getPointsReward() + " points)");
            }
            
            // ===== TEST 9: Get User Quiz History =====
            System.out.println("\n--- TEST 9: Getting User Quiz History ---");
            List<UserQuiz> quizHistory = gamificationService.getUserQuizHistory(userId);
            System.out.println("✓ User completed " + quizHistory.size() + " quiz(zes):");
            for (UserQuiz uq : quizHistory) {
                System.out.println("  - Quiz ID: " + uq.getQuizId() + ", Score: " + uq.getScore() + 
                                   ", Passed: " + (uq.isPassed() ? "Yes" : "No"));
            }
            
            // ===== TEST 10: Get Point Transaction History =====
            System.out.println("\n--- TEST 10: Getting Point Transaction History ---");
            List<PointTransaction> transactions = gamificationService.getUserPointHistory(userId);
            System.out.println("✓ User has " + transactions.size() + " transaction(s):");
            for (PointTransaction trans : transactions) {
                System.out.println("  - " + trans.getTransactionType() + ": " + 
                                   (trans.getPoints() > 0 ? "+" : "") + trans.getPoints() + 
                                   " points - " + trans.getDescription());
            }
            
            // ===== TEST 11: Get Leaderboard =====
            System.out.println("\n--- TEST 11: Getting Top Users Leaderboard ---");
            List<UserPoints> topUsers = gamificationService.getTopUsersByPoints(5);
            System.out.println("✓ Top " + topUsers.size() + " user(s) by points:");
            for (int i = 0; i < topUsers.size(); i++) {
                UserPoints up = topUsers.get(i);
                System.out.println("  " + (i + 1) + ". User ID: " + up.getUserId() + 
                                   " - Points: " + up.getPoints() + 
                                   " - Level: " + up.getLevel());
            }
            
            // ===== TEST 12: Get User Rank =====
            System.out.println("\n--- TEST 12: Getting User Rank ---");
            int userRank = gamificationService.getUserRank(userId);
            System.out.println("✓ User rank: #" + userRank);
            
            // ===== TEST 13: Add More Points (Test Level Up) =====
            System.out.println("\n--- TEST 13: Adding More Points (Testing Level Up) ---");
            gamificationService.addPoints(userId, 100, "QUIZ_COMPLETION", "Completed Advanced SQL Quiz");
            userPoints = gamificationService.getUserPoints(userId);
            System.out.println("✓ Added 100 more points");
            System.out.println("✓ Current Points: " + userPoints.getPoints());
            System.out.println("✓ Current Level: " + userPoints.getLevel());
            
            // ===== TEST 14: Test Course Service =====
            System.out.println("\n--- TEST 14: Getting All Courses ---");
            List<Course> allCourses = courseService.getAllCourses();
            System.out.println("✓ Total available courses: " + allCourses.size());
            for (Course course : allCourses) {
                System.out.println("  - " + course.getTitle() + " (Reward: " + course.getRewardPoints() + " points)");
            }
            
            System.out.println("\n========== ALL TESTS COMPLETED SUCCESSFULLY ==========");
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
