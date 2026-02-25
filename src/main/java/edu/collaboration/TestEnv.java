package edu.collaboration;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;

public class TestEnv {
    public static void main(String[] args) {
        try {
            System.out.println("Testing InvestmentService.addEntity()...");
            InvestmentService is = new InvestmentService();
            Investment inv = new Investment(1, 2, 1000.0, 12, 1000.0 / 12, 10.0, "UNDER_REVIEW");
            is.addEntity(inv);
            System.out.println("Added successfully!");
            System.out.println("New ID: " + inv.getInvestmentId());
        } catch (Exception e) {
            System.out.println("Exception Message: " + e.getMessage());
        }
    }
}
