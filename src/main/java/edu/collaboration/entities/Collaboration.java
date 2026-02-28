package edu.collaboration.entities;

import java.util.Date;

public class Collaboration {
    private int id;
    private int projectId;
    private int investorId;
    private Date startDate;
    private String status; // 'ACTIVE', 'AT_RISK', 'COMPLETED', 'DEFAULTED'
    private double healthScore;
    private double defaultProbability;

    // Fairness Engine Additions
    private double fairnessScore;
    private String fairnessStatus; // BALANCED, DRIFT_WARNING, UNBALANCED
    private double idealEquity;
    private double equityDeviation;

    public Collaboration() {
    }

    public Collaboration(int id, int projectId, int investorId, Date startDate, String status, double healthScore,
            double defaultProbability) {
        this.id = id;
        this.projectId = projectId;
        this.investorId = investorId;
        this.startDate = startDate;
        this.status = status;
        this.healthScore = healthScore;
        this.defaultProbability = defaultProbability;
        this.fairnessScore = 100.0;
        this.fairnessStatus = "BALANCED";
        this.idealEquity = 0.0;
        this.equityDeviation = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getInvestorId() {
        return investorId;
    }

    public void setInvestorId(int investorId) {
        this.investorId = investorId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(double healthScore) {
        this.healthScore = healthScore;
    }

    public double getDefaultProbability() {
        return defaultProbability;
    }

    public void setDefaultProbability(double defaultProbability) {
        this.defaultProbability = defaultProbability;
    }

    public double getFairnessScore() {
        return fairnessScore;
    }

    public void setFairnessScore(double fairnessScore) {
        this.fairnessScore = fairnessScore;
    }

    public String getFairnessStatus() {
        return fairnessStatus;
    }

    public void setFairnessStatus(String fairnessStatus) {
        this.fairnessStatus = fairnessStatus;
    }

    public double getIdealEquity() {
        return idealEquity;
    }

    public void setIdealEquity(double idealEquity) {
        this.idealEquity = idealEquity;
    }

    public double getEquityDeviation() {
        return equityDeviation;
    }

    public void setEquityDeviation(double equityDeviation) {
        this.equityDeviation = equityDeviation;
    }
}
