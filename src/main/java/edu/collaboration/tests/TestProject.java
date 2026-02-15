package edu.collaboration.tests;

import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;

import java.sql.SQLException;

public class TestProject {

    public static void main(String[] args) {

        ProjectService service = new ProjectService();

        try {

            /* ==============================
               1️⃣ CREATE ➜ READ
               ============================== */
            Project p1 = new Project(
                    1,
                    "AI Startup",
                    "AI-powered business solution",
                    500000,
                    15,
                    "OPEN"
            );

            service.addEntity(p1);

            System.out.println("\n--- After creation (p1) ---");
            System.out.println(p1);


            /* ==============================
               2️⃣ UPDATE ➜ READ
               ============================== */
            p1.setTitle("AI Startup - Updated");
            p1.setDescription("Updated project description");
            p1.setStatus("FUNDED");

            service.update(p1.getProjectId(), p1);

            System.out.println("\n--- After update (p1) ---");
            System.out.println(p1);


            /* ==============================
               3️⃣ DELETE
               ============================== */
            service.deleteEntity(p1);

            System.out.println("\n--- After deletion ---");
            System.out.println("Project with ID " + p1.getProjectId() + " deleted.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
