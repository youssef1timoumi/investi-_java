# User Management System

A Java-based terminal application for managing users in the 3a8 database.

## Database Setup

1. Make sure MySQL is running on localhost:3306
2. Create the database if it doesn't exist:
   ```sql
   CREATE DATABASE 3a8;
   ```
3. Run the schema.sql file to create the tables:
   ```sql
   USE 3a8;
   SOURCE schema.sql;
   ```

## Running the Application

### Option 1: Using Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="edu.connexion3a8.tests.UserManagementApp"
```

### Option 2: Using IDE
Run the `UserManagementApp.java` class directly from your IDE.

## Features

The application provides a terminal-based menu with the following options:

1. **Add New User** - Create a new user with email, password, name, and role
2. **View All Users** - Display all users in a formatted table
3. **Search User by Email** - Find a specific user by their email address
4. **View Users by Role** - Filter users by role (admin/investor/innovator)
5. **Update User** - Modify existing user information
6. **Delete User** - Remove a user from the database

## User Roles

- **admin** - Platform administrators
- **investor** - Users who invest in projects
- **innovator** - Users who create and propose projects

## Database Configuration

Update the connection settings in `MyConnection.java` if needed:
- URL: `jdbc:mysql://localhost:3306/3a8`
- Username: `root`
- Password: (empty by default)

## Notes

- Passwords are stored as plain text in this version. For production, implement proper password hashing (bcrypt, etc.)
- The application uses prepared statements to prevent SQL injection
- User profiles table is created but not yet integrated into the management app
