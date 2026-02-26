# Sprint Backlog - Gestion des Utilisateurs

## Sprint Goal
Implement complete user management system with secure authentication (SMTP OTP), KYC identity verification workflow, admin dashboard with advanced filtering, and PDF export functionality.

---

## Sprint Backlog Items

### 1. Create User Entity and CRUD Service ✅
**Story Points:** 3  
**Status:** DONE  
**Tasks:**
- Create User entity with all fields (id, email, passwordHash, name, role, bio, points, level, isActive, emailVerified, idImageUrl)
- Create UserService with full CRUD operations (add, update, delete, getById, getByEmail, getAllUsers, getUsersByRole)
- Create MyConnection database utility class
- Create database schema (schema.sql) with users and user_profiles tables

### 2. Build Login and Registration UI ✅
**Story Points:** 5  
**Status:** DONE  
**Tasks:**
- Create Login.fxml with tab-based login/register forms
- Implement LoginController with form validation (email, name, password strength)
- Add password visibility toggle (show/hide)
- Add role selection (Innovator/Investor) with styled toggle buttons
- Add real-time input validation with visual feedback
- Add password strength indicator (Weak/Medium/Strong)

### 3. Integrate SMTP Email Verification with OTP ✅
**Story Points:** 5  
**Status:** DONE  
**Tasks:**
- Add javax.mail dependency to pom.xml
- Create EmailService with Gmail SMTP configuration
- Implement 6-digit OTP generation and verification
- Design HTML email template with INVESTI branding
- Update registration flow: form → send OTP → verify code → create account
- Add OTP input section in Login.fxml with verify and resend buttons
- Send OTP in background thread to avoid UI freeze

### 4. Implement KYC Identity Verification Flow ✅
**Story Points:** 5  
**Status:** DONE  
**Tasks:**
- Add idImageUrl field to User entity
- Add id_image_url column to database schema
- Create KYC migration SQL script (kyc_migration.sql)
- Set is_active=false on new user registration
- Add KYC banner on Home page (gold banner for upload, blue banner for pending review)
- Implement ID image upload with FileChooser (png, jpg, jpeg, bmp)
- Save uploaded files to uploads/kyc/ directory
- Add UserService methods: updateIdImageUrl, setUserActive, getPendingKycUsers

### 5. Build Admin Dashboard with User Management ✅
**Story Points:** 5  
**Status:** DONE  
**Tasks:**
- Create AdminDashboard.fxml with modern card-based layout
- Implement user CRUD form (add/edit with validation)
- Create users table with columns (Name, Email, Role, Points, Level, Actions)
- Add Edit and Delete buttons per user row with confirmation dialogs
- Add search functionality (by name, email, role)
- Implement KYC verification queue (View ID, Approve, Reject)
- Show ID image in dialog for admin review

### 6. Externalize Credentials and Security ✅
**Story Points:** 2  
**Status:** DONE  
**Tasks:**
- Create config.properties for SMTP and database credentials
- Create config.properties.example as template for other developers
- Refactor EmailService to load credentials from config.properties
- Refactor MyConnection to load database credentials from config.properties
- Add config.properties to .gitignore (public repo protection)
- Remove all hardcoded credentials from source code

### 7. Enhance Admin Dashboard with Filtering, Stats, and PDF Export ✅
**Story Points:** 8  
**Status:** DONE  
**Tasks:**
- Add stats bar with colored cards (Total Users, Verified, Pending KYC, Unverified)
- Add filter ComboBox (All Users, Verified/Active, Pending KYC, Unverified/No ID)
- Combine search and filter with predicate-based filtering
- Add Status column with colored badges (green Active, yellow Pending, red Unverified)
- Add Joined date column with formatted timestamps
- Add user count label (Showing X of Y users)
- Add iText PDF dependency
- Implement per-user PDF export with full profile info and ID image attached
- Add Clear button to reset form
- Clean up unused imports and polish UI

### 8. Code Cleanup ✅
**Story Points:** 1  
**Status:** DONE  
**Tasks:**
- Remove Transport entity, TypeTransport entity
- Remove TransportService, TypeTransportService
- Remove TransportManagementApp test class
- Verify no remaining references to transport code

---

## Sprint Summary

**Total Story Points:** 34  
**Completed Story Points:** 34  
**Sprint Velocity:** 34 points  

**Sprint Duration:** 5 working sessions  
**Team Size:** 1 developer + AI assistant  

**Key Achievements:**
- Complete user authentication system with email OTP verification
- KYC identity verification workflow (upload → admin review → approve/reject)
- Production-ready admin dashboard with search, filter, stats, and PDF export
- Secure credential management for public repository
- Clean codebase with no unused modules

**Technologies Used:**
- JavaFX for UI
- CSS for styling
- javax.mail for SMTP/OTP email verification
- iText for PDF generation
- MySQL for database
- JUnit 5 for testing
- Maven for dependency management
