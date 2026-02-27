package services;

import models.Product;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements IService<Product> {

    private Connection connection;

    public ProductService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int create(Product product) throws SQLException {
        String sql = "INSERT INTO product (name, description, short_description, price, currency, is_digital, download_url, project_id, entrepreneur_id, category_id, status, image, gradient, stock, remise) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, product.getName());
        ps.setString(2, product.getDescription());
        ps.setString(3, product.getShortDescription());
        ps.setDouble(4, product.getPrice());
        ps.setString(5, product.getCurrency());
        ps.setBoolean(6, product.isDigital());
        ps.setString(7, product.getDownloadUrl());
        ps.setLong(8, product.getProjectId());
        ps.setLong(9, product.getEntrepreneurId());
        ps.setLong(10, product.getCategoryId());
        ps.setString(11, product.getStatus());
        ps.setString(12, product.getImage());
        ps.setString(13, product.getGradient());
        ps.setInt(14, product.getStock());
        ps.setInt(15, product.getRemise());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return (int) rs.getLong(1);
        }
        return -1;
    }

    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, description = ?, short_description = ?, price = ?, currency = ?, is_digital = ?, download_url = ?, project_id = ?, entrepreneur_id = ?, category_id = ?, status = ?, image = ?, gradient = ?, stock = ?, remise = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, product.getName());
        ps.setString(2, product.getDescription());
        ps.setString(3, product.getShortDescription());
        ps.setDouble(4, product.getPrice());
        ps.setString(5, product.getCurrency());
        ps.setBoolean(6, product.isDigital());
        ps.setString(7, product.getDownloadUrl());
        ps.setLong(8, product.getProjectId());
        ps.setLong(9, product.getEntrepreneurId());
        ps.setLong(10, product.getCategoryId());
        ps.setString(11, product.getStatus());
        ps.setString(12, product.getImage());
        ps.setString(13, product.getGradient());
        ps.setInt(14, product.getStock());
        ps.setInt(15, product.getRemise());
        ps.setLong(16, product.getId());

        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Product> read() throws SQLException {
        String sql = "SELECT * FROM product";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Product p = new Product();
            p.setId(rs.getLong("id"));
            p.setName(rs.getString("name"));
            p.setDescription(rs.getString("description"));
            p.setShortDescription(rs.getString("short_description"));
            p.setPrice(rs.getDouble("price"));
            p.setCurrency(rs.getString("currency"));
            p.setDigital(rs.getBoolean("is_digital"));
            p.setDownloadUrl(rs.getString("download_url"));
            p.setProjectId(rs.getLong("project_id"));
            p.setEntrepreneurId(rs.getLong("entrepreneur_id"));
            p.setCategoryId(rs.getLong("category_id"));
            p.setStatus(rs.getString("status"));
            p.setImage(rs.getString("image"));
            p.setGradient(rs.getString("gradient"));
            p.setViewsCount(rs.getInt("views_count"));
            p.setSalesCount(rs.getInt("sales_count"));
            // Handle possible nulls for stock and remise if columns didn't have defaults
            // initially
            try {
                p.setStock(rs.getInt("stock"));
            } catch (SQLException ignored) {
            }
            try {
                p.setRemise(rs.getInt("remise"));
            } catch (SQLException ignored) {
            }
            p.setCreatedAt(rs.getTimestamp("created_at"));
            p.setUpdatedAt(rs.getTimestamp("updated_at"));
            products.add(p);
        }
        return products;
    }
}
