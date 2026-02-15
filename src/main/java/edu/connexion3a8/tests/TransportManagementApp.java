package edu.connexion3a8.tests;

import edu.connexion3a8.entities.Transport;
import edu.connexion3a8.entities.TypeTransport;
import edu.connexion3a8.services.TransportService;
import edu.connexion3a8.services.TypeTransportService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TransportManagementApp {
    private static TransportService transportService = new TransportService();
    private static TypeTransportService typeTransportService = new TypeTransportService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        manageTypeTransport();
                        break;
                    case 2:
                        manageTransport();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n========================================");
        System.out.println("    TRANSPORT MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Manage Type Transport");
        System.out.println("2. Manage Transport");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private static void manageTypeTransport() throws SQLException {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Type Transport Management ---");
            System.out.println("1. Add Type Transport");
            System.out.println("2. View All Type Transports");
            System.out.println("3. Update Type Transport");
            System.out.println("4. Delete Type Transport");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addTypeTransport();
                    break;
                case 2:
                    viewAllTypeTransports();
                    break;
                case 3:
                    updateTypeTransport();
                    break;
                case 4:
                    deleteTypeTransport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void manageTransport() throws SQLException {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Transport Management ---");
            System.out.println("1. Add Transport");
            System.out.println("2. View All Transports");
            System.out.println("3. View Transports by Type");
            System.out.println("4. Update Transport");
            System.out.println("5. Delete Transport");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addTransport();
                    break;
                case 2:
                    viewAllTransports();
                    break;
                case 3:
                    viewTransportsByType();
                    break;
                case 4:
                    updateTransport();
                    break;
                case 5:
                    deleteTransport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addTypeTransport() throws SQLException {
        System.out.println("\n--- Add Type Transport ---");
        
        String libelle;
        while (true) {
            libelle = getStringInput("Libelle: ").trim();
            
            if (libelle.isEmpty()) {
                System.out.println("Error: Libelle cannot be empty. Please try again.");
                continue;
            }
            
            if (libelle.length() > 50) {
                System.out.println("Error: Libelle cannot exceed 50 characters. Please try again.");
                continue;
            }
            
            if (!libelle.matches("^[a-zA-Z0-9\\s\\-_]+$")) {
                System.out.println("Error: Libelle can only contain letters, numbers, spaces, hyphens and underscores.");
                continue;
            }
            
            break;
        }
        
        TypeTransport typeTransport = new TypeTransport(libelle);
        typeTransportService.addTypeTransport(typeTransport);
    }

    private static void viewAllTypeTransports() throws SQLException {
        System.out.println("\n--- All Type Transports ---");
        List<TypeTransport> typeTransports = typeTransportService.getAllTypeTransports();
        
        if (typeTransports.isEmpty()) {
            System.out.println("No type transports found.");
        } else {
            System.out.println(String.format("%-10s %-50s", "ID", "Libelle"));
            System.out.println("=".repeat(60));
            
            for (TypeTransport tt : typeTransports) {
                System.out.println(String.format("%-10d %-50s", tt.getIdType(), tt.getLibelle()));
            }
            System.out.println("\nTotal: " + typeTransports.size());
        }
    }

    private static void updateTypeTransport() throws SQLException {
        System.out.println("\n--- Update Type Transport ---");
        int idType = getIntInput("Enter Type Transport ID: ");
        
        TypeTransport typeTransport = typeTransportService.getTypeTransportById(idType);
        if (typeTransport == null) {
            System.out.println("Type Transport not found.");
            return;
        }
        
        System.out.println("Current: " + typeTransport);
        
        String newLibelle;
        while (true) {
            newLibelle = getStringInput("New Libelle [" + typeTransport.getLibelle() + "]: ").trim();
            
            if (newLibelle.isEmpty()) {
                break;
            }
            
            if (newLibelle.length() > 50) {
                System.out.println("Error: Libelle cannot exceed 50 characters. Please try again.");
                continue;
            }
            
            if (!newLibelle.matches("^[a-zA-Z0-9\\s\\-_]+$")) {
                System.out.println("Error: Libelle can only contain letters, numbers, spaces, hyphens and underscores.");
                continue;
            }
            
            typeTransport.setLibelle(newLibelle);
            break;
        }
        
        typeTransportService.updateTypeTransport(idType, typeTransport);
    }

    private static void deleteTypeTransport() throws SQLException {
        System.out.println("\n--- Delete Type Transport ---");
        int idType = getIntInput("Enter Type Transport ID: ");
        
        TypeTransport typeTransport = typeTransportService.getTypeTransportById(idType);
        if (typeTransport == null) {
            System.out.println("Type Transport not found.");
            return;
        }
        
        System.out.println("Type Transport: " + typeTransport);
        System.out.println("WARNING: This will also delete all associated transports (CASCADE).");
        
        String confirm;
        while (true) {
            confirm = getStringInput("Are you sure? (yes/no): ").trim().toLowerCase();
            
            if (confirm.equals("yes") || confirm.equals("no")) {
                break;
            }
            
            System.out.println("Error: Please enter 'yes' or 'no'.");
        }
        
        if (confirm.equals("yes")) {
            typeTransportService.deleteTypeTransport(idType);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void addTransport() throws SQLException {
        System.out.println("\n--- Add Transport ---");
        
        List<TypeTransport> typeTransports = typeTransportService.getAllTypeTransports();
        
        if (typeTransports.isEmpty()) {
            System.out.println("Error: No Type Transport available. Please add a Type Transport first.");
            return;
        }
        
        viewAllTypeTransports();
        
        int idType;
        while (true) {
            idType = getIntInput("\nEnter Type Transport ID: ");
            
            if (idType <= 0) {
                System.out.println("Error: ID must be a positive number. Please try again.");
                continue;
            }
            
            TypeTransport typeTransport = typeTransportService.getTypeTransportById(idType);
            if (typeTransport == null) {
                System.out.println("Error: Type Transport ID not found. Please enter a valid ID.");
                continue;
            }
            
            break;
        }
        
        Transport transport = new Transport(idType);
        transportService.addTransport(transport);
    }

    private static void viewAllTransports() throws SQLException {
        System.out.println("\n--- All Transports ---");
        List<Transport> transports = transportService.getAllTransports();
        
        if (transports.isEmpty()) {
            System.out.println("No transports found.");
        } else {
            System.out.println(String.format("%-15s %-15s %-50s", "Transport ID", "Type ID", "Type Libelle"));
            System.out.println("=".repeat(80));
            
            for (Transport t : transports) {
                System.out.println(String.format("%-15d %-15d %-50s", 
                    t.getIdTransport(), 
                    t.getIdType(), 
                    t.getTypeLibelle() != null ? t.getTypeLibelle() : "N/A"));
            }
            System.out.println("\nTotal: " + transports.size());
        }
    }

    private static void viewTransportsByType() throws SQLException {
        System.out.println("\n--- View Transports by Type ---");
        
        viewAllTypeTransports();
        int idType = getIntInput("\nEnter Type Transport ID: ");
        
        List<Transport> transports = transportService.getTransportsByType(idType);
        if (transports.isEmpty()) {
            System.out.println("No transports found for this type.");
        } else {
            System.out.println(String.format("%-15s %-15s %-50s", "Transport ID", "Type ID", "Type Libelle"));
            System.out.println("=".repeat(80));
            
            for (Transport t : transports) {
                System.out.println(String.format("%-15d %-15d %-50s", 
                    t.getIdTransport(), 
                    t.getIdType(), 
                    t.getTypeLibelle()));
            }
            System.out.println("\nTotal: " + transports.size());
        }
    }

    private static void updateTransport() throws SQLException {
        System.out.println("\n--- Update Transport ---");
        int idTransport = getIntInput("Enter Transport ID: ");
        
        Transport transport = transportService.getTransportById(idTransport);
        if (transport == null) {
            System.out.println("Transport not found.");
            return;
        }
        
        System.out.println("Current: " + transport);
        
        viewAllTypeTransports();
        
        while (true) {
            String newIdTypeStr = getStringInput("\nNew Type ID [" + transport.getIdType() + "] (press Enter to keep current): ").trim();
            
            if (newIdTypeStr.isEmpty()) {
                break;
            }
            
            try {
                int newIdType = Integer.parseInt(newIdTypeStr);
                
                if (newIdType <= 0) {
                    System.out.println("Error: ID must be a positive number. Please try again.");
                    continue;
                }
                
                TypeTransport typeTransport = typeTransportService.getTypeTransportById(newIdType);
                if (typeTransport == null) {
                    System.out.println("Error: Type Transport ID not found. Please enter a valid ID.");
                    continue;
                }
                
                transport.setIdType(newIdType);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
        
        transportService.updateTransport(idTransport, transport);
    }

    private static void deleteTransport() throws SQLException {
        System.out.println("\n--- Delete Transport ---");
        int idTransport = getIntInput("Enter Transport ID: ");
        
        Transport transport = transportService.getTransportById(idTransport);
        if (transport == null) {
            System.out.println("Transport not found.");
            return;
        }
        
        System.out.println("Transport: " + transport);
        
        String confirm;
        while (true) {
            confirm = getStringInput("Are you sure? (yes/no): ").trim().toLowerCase();
            
            if (confirm.equals("yes") || confirm.equals("no")) {
                break;
            }
            
            System.out.println("Error: Please enter 'yes' or 'no'.");
        }
        
        if (confirm.equals("yes")) {
            transportService.deleteTransport(idTransport);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. " + prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
}
