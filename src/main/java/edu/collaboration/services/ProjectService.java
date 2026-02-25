package edu.collaboration.services;

import edu.collaboration.entities.Project;
import edu.collaboration.interfaces.IService;
import edu.collaboration.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectService implements IService<Project> {

    // ─── Helper to map a ResultSet row to a Project ──────────────────────────
    private Project mapRow(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setProjectId(rs.getInt("project_id"));
        p.setEntrepreneurId(rs.getInt("entrepreneur_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setAmountRequested(rs.getDouble("amountRequested"));
        p.setEquityOffered(rs.getDouble("equityOffered"));
        p.setStatus(rs.getString("status"));
        p.setProjectDate(rs.getDate("project_date"));
        // category column — falls back gracefully if column doesn't exist in DB yet
        try {
            p.setCategory(rs.getString("category"));
        } catch (SQLException ignored) {
            p.setCategory("Other");
        }
        return p;
    }

    @Override
    public void addEntity(Project p) throws SQLException {
        String sql = "INSERT INTO project " +
                "(entrepreneur_id, title, description, amountRequested, equityOffered, status, project_date, category) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)";

        PreparedStatement pst = MyConnection.getInstance()
                .getCnx()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pst.setInt(1, p.getEntrepreneurId());
        pst.setString(2, p.getTitle());
        pst.setString(3, p.getDescription());
        pst.setDouble(4, p.getAmountRequested());
        pst.setDouble(5, p.getEquityOffered());
        pst.setString(6, p.getStatus());
        pst.setString(7, p.getCategory());

        pst.executeUpdate();

        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            p.setProjectId(rs.getInt(1));
        }
    }

    @Override
    public void deleteEntity(Project p) {
        String sqlInvestments = "DELETE FROM investment WHERE project_id = ?";
        String sqlProject = "DELETE FROM project WHERE project_id = ?";

        Connection cnx = MyConnection.getInstance().getCnx();

        try {
            cnx.setAutoCommit(false);
            PreparedStatement pstInv = cnx.prepareStatement(sqlInvestments);
            pstInv.setInt(1, p.getProjectId());
            pstInv.executeUpdate();

            PreparedStatement pstProj = cnx.prepareStatement(sqlProject);
            pstProj.setInt(1, p.getProjectId());
            pstProj.executeUpdate();

            cnx.commit();
            cnx.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                cnx.rollback();
                cnx.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean update(int id, Project p) {
        String sql = "UPDATE project SET title = ?, description = ?, amountRequested = ?, " +
                "equityOffered = ?, status = ?, category = ? WHERE project_id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, p.getTitle());
            pst.setString(2, p.getDescription());
            pst.setDouble(3, p.getAmountRequested());
            pst.setDouble(4, p.getEquityOffered());
            pst.setString(5, p.getStatus());
            pst.setString(6, p.getCategory());
            pst.setInt(7, id);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Project> getData() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project ORDER BY project_date DESC";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Project> getProjectsByStatus(String status) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE status = ? ORDER BY project_date DESC";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Project> getProjectsByEntrepreneur(int entrepreneurId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE entrepreneur_id = ? ORDER BY project_date DESC";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, entrepreneurId);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // ─── NEW: Search by keyword (title or description) ───────────────────────
    public List<Project> searchProjects(String keyword) {
        List<Project> list = new ArrayList<>();
        if (keyword == null || keyword.isBlank())
            return getData();
        String sql = "SELECT * FROM project WHERE title LIKE ? OR description LIKE ? ORDER BY project_date DESC";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            String pattern = "%" + keyword.trim() + "%";
            pst.setString(1, pattern);
            pst.setString(2, pattern);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // ─── NEW: Filter by category ──────────────────────────────────────────────
    public List<Project> getProjectsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("All"))
            return getData();
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE category = ? ORDER BY project_date DESC";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, category);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // ─── NEW: Get stats ──────────────────────────────────────────────────────
    public int getTotalProjectCount() {
        try {
            ResultSet rs = MyConnection.getInstance().getCnx()
                    .createStatement().executeQuery("SELECT COUNT(*) FROM project");
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int getFundedProjectCount() {
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx()
                    .prepareStatement("SELECT COUNT(*) FROM project WHERE status = 'FUNDED'");
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int getOpenProjectCount() {
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx()
                    .prepareStatement("SELECT COUNT(*) FROM project WHERE status = 'OPEN'");
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
