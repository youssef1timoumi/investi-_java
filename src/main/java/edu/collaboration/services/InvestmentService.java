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
        String sql = "DELETE FROM investment WHERE investment_id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, i.getInvestmentId());
            pst.executeUpdate();
        } catch (SQLException e) {
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
                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
