package edu.collaboration.services;

import edu.collaboration.entities.Investment;
import edu.collaboration.interfaces.IService;
import edu.collaboration.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvestmentService implements IService<Investment> {

    @Override
    public void addEntity(Investment i) throws SQLException {

        String sql = "INSERT INTO investment " +
                "(project_id, investor_id, totalAmount, durationMonths, amountPerPeriod, equityRequested, status, investment_date) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";

        PreparedStatement pst = MyConnection.getInstance()
                .getCnx()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pst.setInt(1, i.getProjectId());
        pst.setInt(2, i.getInvestorId());
        pst.setDouble(3, i.getTotalAmount());
        pst.setInt(4, i.getDurationMonths());
        pst.setDouble(5, i.getAmountPerPeriod());
        pst.setDouble(6, i.getEquityRequested());
        pst.setString(7, i.getStatus());

        pst.executeUpdate();

        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            i.setInvestmentId(rs.getInt(1));
        }
    }

    @Override
    public void deleteEntity(Investment i) {
        String sqlDelete = "DELETE FROM investment WHERE investment_id = ?";
        String sqlUpdateProject = "UPDATE project SET status = 'OPEN' WHERE project_id = ?";

        Connection cnx = MyConnection.getInstance().getCnx();

        try {
            // Check status BEFORE delete
            // Note: The passed object 'i' might not have the latest status if UI is stale,
            // but usually it comes from a refreshed table. Ideally we fetch it, but
            // trusting object for now or checking DB is better.
            // Let's trust 'i' for now as it comes from TableView which is refreshed.

            if ("ACCEPTED".equalsIgnoreCase(i.getStatus())) {
                cnx.setAutoCommit(false);

                // Revert Project to OPEN
                PreparedStatement pstProj = cnx.prepareStatement(sqlUpdateProject);
                pstProj.setInt(1, i.getProjectId());
                pstProj.executeUpdate();

                // Delete Investment
                PreparedStatement pstDel = cnx.prepareStatement(sqlDelete);
                pstDel.setInt(1, i.getInvestmentId());
                pstDel.executeUpdate();

                cnx.commit();
                cnx.setAutoCommit(true);
            } else {
                // Normal delete
                PreparedStatement pst = cnx.prepareStatement(sqlDelete);
                pst.setInt(1, i.getInvestmentId());
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            try {
                if (!cnx.getAutoCommit()) {
                    cnx.rollback();
                    cnx.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean update(int id, Investment i) {

        String sql = "UPDATE investment SET "
                + "totalAmount = ?, "
                + "durationMonths = ?, "
                + "amountPerPeriod = ?, "
                + "equityRequested = ?, "
                + "status = ? "
                + "WHERE investment_id = ?";

        try {
            PreparedStatement ps = MyConnection.getInstance()
                    .getCnx()
                    .prepareStatement(sql);

            ps.setDouble(1, i.getTotalAmount());
            ps.setInt(2, i.getDurationMonths());
            ps.setDouble(3, i.getAmountPerPeriod()); // VERY IMPORTANT
            ps.setDouble(4, i.getEquityRequested());
            ps.setString(5, i.getStatus());
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Investment> getData() {
        List<Investment> list = new ArrayList<>();
        String sql = "SELECT * FROM investment";

        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Investment i = new Investment();
                i.setInvestmentId(rs.getInt("investment_id"));
                i.setProjectId(rs.getInt("project_id"));
                i.setInvestorId(rs.getInt("investor_id"));
                i.setTotalAmount(rs.getDouble("totalAmount"));
                i.setDurationMonths(rs.getInt("durationMonths"));
                i.setAmountPerPeriod(rs.getDouble("amountPerPeriod"));
                i.setEquityRequested(rs.getDouble("equityRequested"));
                i.setStatus(rs.getString("status"));
                i.setInvestmentDate(rs.getDate("investment_date"));

                // Progress Tracking Fields
                i.setProgressPercentage(rs.getInt("progressPercentage"));
                i.setLatestProgressLog(rs.getString("latestProgressLog"));
                i.setPaymentMonthsCompleted(rs.getInt("paymentMonthsCompleted"));

                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Investment> getInvestmentsByStatus(String status) {
        List<Investment> list = new ArrayList<>();
        String sql = "SELECT * FROM investment WHERE status = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Investment i = new Investment();
                i.setInvestmentId(rs.getInt("investment_id"));
                i.setProjectId(rs.getInt("project_id"));
                i.setInvestorId(rs.getInt("investor_id"));
                i.setTotalAmount(rs.getDouble("totalAmount"));
                i.setDurationMonths(rs.getInt("durationMonths"));
                i.setAmountPerPeriod(rs.getDouble("amountPerPeriod"));
                i.setEquityRequested(rs.getDouble("equityRequested"));
                i.setStatus(rs.getString("status"));
                i.setInvestmentDate(rs.getDate("investment_date"));

                // Progress Tracking Fields
                i.setProgressPercentage(rs.getInt("progressPercentage"));
                i.setLatestProgressLog(rs.getString("latestProgressLog"));
                i.setPaymentMonthsCompleted(rs.getInt("paymentMonthsCompleted"));

                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Investment> getInvestmentsByProject(int projectId) {
        List<Investment> list = new ArrayList<>();
        String sql = "SELECT * FROM investment WHERE project_id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, projectId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Investment i = new Investment();
                i.setInvestmentId(rs.getInt("investment_id"));
                i.setProjectId(rs.getInt("project_id"));
                i.setInvestorId(rs.getInt("investor_id"));
                i.setTotalAmount(rs.getDouble("totalAmount"));
                i.setDurationMonths(rs.getInt("durationMonths"));
                i.setAmountPerPeriod(rs.getDouble("amountPerPeriod"));
                i.setEquityRequested(rs.getDouble("equityRequested"));
                i.setStatus(rs.getString("status"));
                i.setInvestmentDate(rs.getDate("investment_date"));

                // Progress Tracking Fields
                i.setProgressPercentage(rs.getInt("progressPercentage"));
                i.setLatestProgressLog(rs.getString("latestProgressLog"));
                i.setPaymentMonthsCompleted(rs.getInt("paymentMonthsCompleted"));

                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Investment> getInvestmentsByInvestor(int investorId) {
        List<Investment> list = new ArrayList<>();
        String sql = "SELECT * FROM investment WHERE investor_id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, investorId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Investment i = new Investment();
                i.setInvestmentId(rs.getInt("investment_id"));
                i.setProjectId(rs.getInt("project_id"));
                i.setInvestorId(rs.getInt("investor_id"));
                i.setTotalAmount(rs.getDouble("totalAmount"));
                i.setDurationMonths(rs.getInt("durationMonths"));
                i.setAmountPerPeriod(rs.getDouble("amountPerPeriod"));
                i.setEquityRequested(rs.getDouble("equityRequested"));
                i.setStatus(rs.getString("status"));
                i.setInvestmentDate(rs.getDate("investment_date"));

                // Progress Tracking Fields
                i.setProgressPercentage(rs.getInt("progressPercentage"));
                i.setLatestProgressLog(rs.getString("latestProgressLog"));
                i.setPaymentMonthsCompleted(rs.getInt("paymentMonthsCompleted"));

                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public boolean acceptInvestment(int investmentId, int projectId) {
        Connection cnx = MyConnection.getInstance().getCnx();
        try {
            cnx.setAutoCommit(false); // Start Transaction

            // 1. Mark this investment as ACCEPTED
            String sqlAccept = "UPDATE investment SET status = 'ACCEPTED' WHERE investment_id = ?";
            PreparedStatement pstAccept = cnx.prepareStatement(sqlAccept);
            pstAccept.setInt(1, investmentId);
            pstAccept.executeUpdate();

            // 2. Mark project as FUNDED
            String sqlFundProject = "UPDATE project SET status = 'FUNDED' WHERE project_id = ?";
            PreparedStatement pstProject = cnx.prepareStatement(sqlFundProject);
            pstProject.setInt(1, projectId);
            pstProject.executeUpdate();

            // 3. Mark all OTHER pending investments for this project as REFUSED
            String sqlRefuseOthers = "UPDATE investment SET status = 'REFUSED' WHERE project_id = ? AND investment_id != ? AND status = 'PENDING'";
            PreparedStatement pstRefuse = cnx.prepareStatement(sqlRefuseOthers);
            pstRefuse.setInt(1, projectId);
            pstRefuse.setInt(2, investmentId);
            pstRefuse.executeUpdate();

            cnx.commit();
            return true;

        } catch (Exception e) {
            try {
                cnx.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                cnx.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateProgress(int investmentId, int percentage, String log, int payments) {
        String sql = "UPDATE investment SET progressPercentage = ?, latestProgressLog = ?, paymentMonthsCompleted = ? WHERE investment_id = ?";
        try {
            PreparedStatement ps = MyConnection.getInstance().getCnx().prepareStatement(sql);
            ps.setInt(1, percentage);
            ps.setString(2, log);
            ps.setInt(3, payments);
            ps.setInt(4, investmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── NEW: Stats for Admin Dashboard ──────────────────────────────────────
    public double getTotalInvestedVolume() {
        try {
            ResultSet rs = MyConnection.getInstance().getCnx()
                    .createStatement()
                    .executeQuery("SELECT SUM(totalAmount) FROM investment WHERE status = 'ACCEPTED'");
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int getTotalInvestmentCount() {
        try {
            ResultSet rs = MyConnection.getInstance().getCnx()
                    .createStatement()
                    .executeQuery("SELECT COUNT(*) FROM investment");
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
