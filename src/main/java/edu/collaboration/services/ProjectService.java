package edu.collaboration.services;

import edu.collaboration.entities.Project;
import edu.collaboration.interfaces.IService;
import edu.collaboration.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectService implements IService<Project> {

    @Override
    public void addEntity(Project p) throws SQLException {

        String sql = "INSERT INTO project " +
                "(entrepreneur_id, title, description, amountRequested, equityOffered, status, project_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";

        PreparedStatement pst = MyConnection.getInstance()
                .getCnx()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pst.setInt(1, p.getEntrepreneurId());
        pst.setString(2, p.getTitle());
        pst.setString(3, p.getDescription());
        pst.setDouble(4, p.getAmountRequested());
        pst.setDouble(5, p.getEquityOffered());
        pst.setString(6, p.getStatus());

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
            cnx.setAutoCommit(false); // Transaction to ensure both delete or neither

            // 1. Delete associated investments
            PreparedStatement pstInv = cnx.prepareStatement(sqlInvestments);
            pstInv.setInt(1, p.getProjectId());
            pstInv.executeUpdate();

            // 2. Delete the project
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
        String sql = "UPDATE project SET title = ?, description = ?, amountRequested = ?, equityOffered = ?, status = ? WHERE project_id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, p.getTitle());
            pst.setString(2, p.getDescription());
            pst.setDouble(3, p.getAmountRequested());
            pst.setDouble(4, p.getEquityOffered());
            pst.setString(5, p.getStatus());
            pst.setInt(6, id);
            System.out.println("DEBUG: Executing UPDATE for Project ID " + id + " | Title: " + p.getTitle()
                    + " | Status: " + p.getStatus());
            int rows = pst.executeUpdate();
            System.out.println("DEBUG: Update result rows: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Project> getData() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project";

        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Project p = new Project();
                p.setProjectId(rs.getInt("project_id"));
                p.setEntrepreneurId(rs.getInt("entrepreneur_id"));
                p.setTitle(rs.getString("title"));
                p.setDescription(rs.getString("description"));
                p.setAmountRequested(rs.getDouble("amountRequested"));
                p.setEquityOffered(rs.getDouble("equityOffered"));
                p.setStatus(rs.getString("status"));
                p.setProjectDate(rs.getDate("project_date"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Project> getProjectsByStatus(String status) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE status = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Project p = new Project();
                p.setProjectId(rs.getInt("project_id"));
                p.setEntrepreneurId(rs.getInt("entrepreneur_id"));
                p.setTitle(rs.getString("title"));
                p.setDescription(rs.getString("description"));
                p.setAmountRequested(rs.getDouble("amountRequested"));
                p.setEquityOffered(rs.getDouble("equityOffered"));
                p.setStatus(rs.getString("status"));
                p.setProjectDate(rs.getDate("project_date"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Project> getProjectsByEntrepreneur(int entrepreneurId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE entrepreneur_id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, entrepreneurId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Project p = new Project();
                p.setProjectId(rs.getInt("project_id"));
                p.setEntrepreneurId(rs.getInt("entrepreneur_id"));
                p.setTitle(rs.getString("title"));
                p.setDescription(rs.getString("description"));
                p.setAmountRequested(rs.getDouble("amountRequested"));
                p.setEquityOffered(rs.getDouble("equityOffered"));
                p.setStatus(rs.getString("status"));
                p.setProjectDate(rs.getDate("project_date"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
