package edu.collaboration.entities;

import java.util.Date;
import java.util.Objects;

public class Project {

    private int projectId;
    private int entrepreneurId;
    private String title;
    private String description;
    private double amountRequested;
    private double equityOffered;
    private String status;
    private Date projectDate;

    public Project() {}

    public Project(int entrepreneurId, String title, String description,
                   double amountRequested, double equityOffered, String status) {
        this.entrepreneurId = entrepreneurId;
        this.title = title;
        this.description = description;
        this.amountRequested = amountRequested;
        this.equityOffered = equityOffered;
        this.status = status;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getEntrepreneurId() {
        return entrepreneurId;
    }

    public void setEntrepreneurId(int entrepreneurId) {
        this.entrepreneurId = entrepreneurId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(double amountRequested) {
        this.amountRequested = amountRequested;
    }

    public double getEquityOffered() {
        return equityOffered;
    }

    public void setEquityOffered(double equityOffered) {
        this.equityOffered = equityOffered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(Date projectDate) {
        this.projectDate = projectDate;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", entrepreneurId=" + entrepreneurId +
                ", title='" + title + '\'' +
                ", amountRequested=" + amountRequested +
                ", equityOffered=" + equityOffered +
                ", status='" + status + '\'' +
                ", projectDate=" + projectDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return projectId == project.projectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
