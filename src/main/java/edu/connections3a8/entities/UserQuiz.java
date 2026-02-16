package edu.connections3a8.entities;

import java.sql.Timestamp;
import java.util.Objects;

public class UserQuiz {
    private long id;
    private int userId;
    private long quizId;
    private int score;
    private Timestamp completedAt;
    private int timeTaken;
    private boolean passed;

    public UserQuiz() {
    }

    public UserQuiz(int userId, long quizId, int score, int timeTaken, boolean passed) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.timeTaken = timeTaken;
        this.passed = passed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserQuiz userQuiz = (UserQuiz) o;
        return id == userQuiz.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserQuiz{" +
                "userId=" + userId +
                ", quizId=" + quizId +
                ", score=" + score +
                ", passed=" + passed +
                '}';
    }
}
