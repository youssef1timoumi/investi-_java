package models;

import java.sql.Timestamp;

public class Product {
    private long id;
    private String name;
    private String description;
    private String shortDescription;
    private double price;
    private String currency;
    private boolean isDigital;
    private String downloadUrl;
    private long projectId;
    private long entrepreneurId;
    private long categoryId;
    private String status;
    private String image;
    private String gradient;
    private int viewsCount;
    private int salesCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product() {
    }

    public Product(long id, String name, String description, String shortDescription, double price, String currency,
            boolean isDigital, String downloadUrl, long projectId, long entrepreneurId, long categoryId, String status,
            String image, String gradient, int viewsCount, int salesCount, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.currency = currency;
        this.isDigital = isDigital;
        this.downloadUrl = downloadUrl;
        this.projectId = projectId;
        this.entrepreneurId = entrepreneurId;
        this.categoryId = categoryId;
        this.status = status;
        this.image = image;
        this.gradient = gradient;
        this.viewsCount = viewsCount;
        this.salesCount = salesCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for creation (without ID and timestamps)
    public Product(String name, String description, String shortDescription, double price, String currency,
            boolean isDigital, String downloadUrl, long projectId, long entrepreneurId, long categoryId,
            String status, String image, String gradient) {
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.currency = currency;
        this.isDigital = isDigital;
        this.downloadUrl = downloadUrl;
        this.projectId = projectId;
        this.entrepreneurId = entrepreneurId;
        this.categoryId = categoryId;
        this.status = status;
        this.image = image;
        this.gradient = gradient;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean digital) {
        isDigital = digital;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getEntrepreneurId() {
        return entrepreneurId;
    }

    public void setEntrepreneurId(long entrepreneurId) {
        this.entrepreneurId = entrepreneurId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGradient() {
        return gradient;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCategoryName() {
        return name; // Or whichever field represents the category name in your UI
    }

    public String getTitle() {
        return name;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}
