package edu.connexion3a8.tests;

import edu.connexion3a8.entities.User;
import edu.connexion3a8.services.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserManagementApp {
    private static UserService userService = new UserService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        addUser();
                        break;
                    case 2:
                        viewAllUsers();
                        break;
                    case 3:
                        viewUserByEmail();
                        break;
                    case 4:
                        viewUsersByRole();
                        break;
                    case 5:
                        updateUser();
                        break;
                    case 6:
                        deleteUser();
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
        System.out.println("       USER MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Add New User");
        System.out.println("2. View All Users");
        System.out.println("3. Search User by Email");
        System.out.println("4. View Users by Role");
        System.out.println("5. Update User");
        System.out.println("6. Delete User");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private static void addUser() throws SQLException {
        System.out.println("\n--- Add New User ---");
        
        String email = getStringInput("Email: ");
        String password = getStringInput("Password: ");
        String name = getStringInput("Name: ");
        
        System.out.println("Role (admin/investor/innovator): ");
        String role = scanner.nextLine();
        
        User user = new User(email, password, name, role);
        
        String avatarUrl = getStringInput("Avatar URL (optional, press Enter to skip): ");
        if (!avatarUrl.isEmpty()) {
            user.setAvatarUrl(avatarUrl);
        }
        
        String bio = getStringInput("Bio (optional, press Enter to skip): ");
        if (!bio.isEmpty()) {
            user.setBio(bio);
        }
        
        userService.addUser(user);
    }

    private static void viewAllUsers() throws SQLException {
        System.out.println("\n--- All Users ---");
        List<User> users = userService.getAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println(String.format("%-36s %-30s %-25s %-12s %-7s %-6s", 
                "ID", "Name", "Email", "Role", "Points", "Level"));
            System.out.println("=".repeat(120));
            
            for (User user : users) {
                System.out.println(String.format("%-36s %-30s %-25s %-12s %-7d %-6d",
                    user.getId(),
                    truncate(user.getName(), 30),
                    truncate(user.getEmail(), 25),
                    user.getRole(),
                    user.getPoints(),
                    user.getLevel()));
            }
            System.out.println("\nTotal users: " + users.size());
        }
    }

    private static void viewUserByEmail() throws SQLException {
        System.out.println("\n--- Search User by Email ---");
        String email = getStringInput("Enter email: ");
        
        User user = userService.getUserByEmail(email);
        if (user != null) {
            displayUserDetails(user);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void viewUsersByRole() throws SQLException {
        System.out.println("\n--- View Users by Role ---");
        String role = getStringInput("Enter role (admin/investor/innovator): ");
        
        List<User> users = userService.getUsersByRole(role);
        if (users.isEmpty()) {
            System.out.println("No users found with role: " + role);
        } else {
            System.out.println(String.format("%-36s %-30s %-25s %-7s %-6s", 
                "ID", "Name", "Email", "Points", "Level"));
            System.out.println("=".repeat(110));
            
            for (User user : users) {
                System.out.println(String.format("%-36s %-30s %-25s %-7d %-6d",
                    user.getId(),
                    truncate(user.getName(), 30),
                    truncate(user.getEmail(), 25),
                    user.getPoints(),
                    user.getLevel()));
            }
            System.out.println("\nTotal users: " + users.size());
        }
    }

    private static void updateUser() throws SQLException {
        System.out.println("\n--- Update User ---");
        String email = getStringInput("Enter user email: ");
        
        User user = userService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        
        displayUserDetails(user);
        System.out.println("\nEnter new values (press Enter to keep current value):");
        
        String newName = getStringInput("Name [" + user.getName() + "]: ");
        if (!newName.isEmpty()) {
            user.setName(newName);
        }
        
        String newRole = getStringInput("Role [" + user.getRole() + "]: ");
        if (!newRole.isEmpty()) {
            user.setRole(newRole);
        }
        
        String newBio = getStringInput("Bio [" + (user.getBio() != null ? user.getBio() : "none") + "]: ");
        if (!newBio.isEmpty()) {
            user.setBio(newBio);
        }
        
        String pointsStr = getStringInput("Points [" + user.getPoints() + "]: ");
        if (!pointsStr.isEmpty()) {
            user.setPoints(Integer.parseInt(pointsStr));
        }
        
        String levelStr = getStringInput("Level [" + user.getLevel() + "]: ");
        if (!levelStr.isEmpty()) {
            user.setLevel(Integer.parseInt(levelStr));
        }
        
        userService.updateUser(user.getId(), user);
    }

    private static void deleteUser() throws SQLException {
        System.out.println("\n--- Delete User ---");
        String email = getStringInput("Enter user email: ");
        
        User user = userService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        
        displayUserDetails(user);
        String confirm = getStringInput("\nAre you sure you want to delete this user? (yes/no): ");
        
        if (confirm.equalsIgnoreCase("yes")) {
            userService.deleteUser(user.getId());
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void displayUserDetails(User user) {
        System.out.println("\n--- User Details ---");
        System.out.println("ID: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Role: " + user.getRole());
        System.out.println("Bio: " + (user.getBio() != null ? user.getBio() : "N/A"));
        System.out.println("Avatar URL: " + (user.getAvatarUrl() != null ? user.getAvatarUrl() : "N/A"));
        System.out.println("Points: " + user.getPoints());
        System.out.println("Level: " + user.getLevel());
        System.out.println("Active: " + user.isActive());
        System.out.println("Email Verified: " + user.isEmailVerified());
        System.out.println("Created At: " + user.getCreatedAt());
        System.out.println("Last Login: " + (user.getLastLogin() != null ? user.getLastLogin() : "Never"));
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

    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}
