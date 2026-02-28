package edu.Investi.tests;

import edu.Investi.entities.Evenement;
import edu.Investi.entities.Inscription;
import edu.Investi.interfaces.statut;
import edu.Investi.services.EvenementService;
import edu.Investi.services.InscriptionService;
import edu.Investi.tools.MyConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class MainClass {
    public static void main(String[] args) throws SQLException {
        EvenementService evenementService = new EvenementService();
        InscriptionService inscriptionService = new InscriptionService();

        try {
          /* System.out.println("\n========== CRÉER UN ÉVÉNEMENT ==========");
            Evenement event = new Evenement(
                    1,
                    "test",
                    "Atest okhra",
                    "test a9wa",
                    LocalDateTime.of(2025, 3, 15, 14, 0),
                    LocalDateTime.of(2025, 3, 15, 17, 0)
            );
            Evenement event2 = new Evenement(
                    1,
                    "tdszbhvfsdh",
                    "Atest scfq<vcjks",
                    "test a9wa",
                    LocalDateTime.of(2025, 3, 15, 14, 0),
                    LocalDateTime.of(2025, 3, 15, 17, 0)
            );

            //evenementService.addEntity(event);
            //evenementService.addEntity(event2);


         System.out.println("\n========== PAR ID==========");
         Evenement e = evenementService.getById(10);
        if (e != null) {
            System.out.println(e);
        }

       System.out.println("\n========== TOUS LES ÉVÉNEMENTS ==========");
        List<Evenement> evenements = evenementService.getData();
        evenements.forEach(System.out::println);

        System.out.println("\n========== SUPPRIMER EVENTS ==========");
        evenementService.deleteEntity(9);

        System.out.println("\n========== TOUS LES ÉVÉNEMENTS ==========");
        List<Evenement> events = evenementService.getData();
        events.forEach(System.out::println);

        System.out.println("\n========== GET BY ID  ==========")
        Evenement event3 = evenementService.getById(10);

        System.out.println("\n========== MODIFIER EVENT ==========");
            event3.setLieu("WELYEEEEEEEEEEEEEEY");
         evenementService.updateEntity(event3);    */

        System.out.println("\n========== S'INSCRIRE ==========");
        Inscription inscription = new Inscription(1, 11, statut.ANNULE);

       // inscriptionService.addEntity(inscription);

        System.out.println("\n========== MODIFIER STATUT ==========");
        Inscription trouvee = inscriptionService.getById(18);
        trouvee.setStatut("confirme");
        inscriptionService.updateEntity(trouvee);
        System.out.println("Nouveau statut : " + trouvee.getStatut());

        inscriptionService.deleteEntity(14);

        } catch (SQLException e) {
            System.out.println(" Erreur : " + e.getMessage());
        }

        System.out.println("\n========== TOUS LES INSCRIPS ==========");
        List<Inscription> inscriptions = inscriptionService.getData();
        inscriptions.forEach(System.out::println);



    }
}
