package edu.connections3a8.entities;

import java.sql.Timestamp;
import java.util.Objects;

public class PointTransaction {
    private long id;
    private int userId;
    private int points;
    private String transactionType;
    private Long referenceId;
    private String referenceType;
    private String description;
    private Timestamp createdAt;

    public PointTransaction() {
    }

    public PointTransaction(int userId, int points, String transactionType, String description) {
        this.userId = userId;
        this.points = points;
        this.transactionType = transactionType;
        this.description = description;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PointTransaction that = (PointTransaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PointTransaction{" +
                "userId=" + userId +
                ", points=" + points +
                ", transactionType='" + transactionType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
