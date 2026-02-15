package edu.collaboration.tests;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;

import java.sql.SQLException;

public class TestInvestment {

    public static void main(String[] args) {

        InvestmentService service = new InvestmentService();

        try {

            /* ==============================
               1️⃣ CREATE ➜ READ
               ============================== */
            Investment i1 = new Investment(
                    1,
                    2,
                    1000000,
                    12,
                    83333,
                    10,
                    "PENDING"
            );

            service.addEntity(i1);

            System.out.println("\n--- After creation (i1) ---");
            System.out.println(i1);


            /* ==============================
               2️⃣ UPDATE ➜ READ
               ============================== */
            i1.setStatus("ACCEPTED");
            service.update(i1.getInvestmentId(), i1);

            System.out.println("\n--- After update (i1) ---");
            System.out.println(i1);


            /* ==============================
               3️⃣ DELETE
               ============================== */
            service.deleteEntity(i1);

            System.out.println("\n--- After deletion ---");
            System.out.println("Investment with ID " + i1.getInvestmentId() + " deleted.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
