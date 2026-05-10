# Investi — Java Desktop Application

> A JavaFX desktop platform for investment, entrepreneurship, events, collaboration, gamification, and community management.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Modules](#modules)
  - [Authentication & Users](#authentication--users)
  - [Events & Inscriptions](#events--inscriptions)
  - [Collaboration (Projects & Investments)](#collaboration-projects--investments)
  - [Product Management](#product-management)
  - [Forum](#forum)
  - [Gamification (Courses, Quizzes & Badges)](#gamification-courses-quizzes--badges)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Build & Run](#build--run)
- [Key Tools & Utilities](#key-tools--utilities)
- [Role-Based Access Control](#role-based-access-control)
- [Credits](#credits)

---

## Overview

**Investi** is a multi-module JavaFX desktop application built for the Tunisian investment ecosystem. It connects entrepreneurs, investors, mentors, and admins on a single platform with features ranging from project funding and event management to gamified learning and community forums.

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 17                             |
| UI Framework | JavaFX 17.0.2 (FXML)               |
| Build Tool   | Maven                               |
| Database     | MySQL (`3a8` schema)                |
| HTTP Client  | OkHttp 4.12.0                       |
| PDF          | iTextPDF 5.5.13 + OpenPDF 1.3.30   |
| Excel        | Apache POI 5.2.5                    |
| OCR          | Tesseract (tess4j 5.9.0)            |
| TTS          | FreeTTS 1.2.2                       |
| Testing      | JUnit Jupiter 5.10.1                |

---

## Prerequisites

- Java 17 JDK
- Maven 3.8+
- MySQL 8.x running locally
- A database named `3a8` (see [Database Setup](#database-setup))

---

## Getting Started

```bash
# 1. Clone the repository
git clone <repo-url>
cd investi-_java

# 2. Set up the database
mysql -u root -p 3a8 < sql/investi.sql
mysql -u root -p 3a8 < sql/product_integration.sql
mysql -u root -p 3a8 < sql/collaboration_integration.sql

# 3. Configure credentials
cp src/main/resources/config.properties.example src/main/resources/config.properties
# Edit config.properties with your DB credentials and API keys

# 4. Build and run
mvn clean javafx:run
```

---

## Project Structure

```
investi-_java/
├── src/
│   └── main/
│       ├── java/edu/connexion3a8/
│       │   ├── InvestiApp.java                  # Main entry point
│       │   ├── controllers/                     # JavaFX controllers
│       │   │   ├── collaboration/               # Admin, Entrepreneur, Investor, Products
│       │   │   ├── events/                      # Event & inscription controllers
│       │   │   └── gamification/                # Courses, quizzes, badges
│       │   ├── entities/                        # Data models
│       │   │   ├── collaboration/
│       │   │   └── gamification/
│       │   ├── services/                        # Business logic layer
│       │   │   ├── collaboration/
│       │   │   └── gamification/
│       │   ├── tools/                           # Utilities (DB, email, geocoding, etc.)
│       │   └── utils/                           # Helpers (confetti engine, etc.)
│       └── resources/
│           ├── *.fxml                           # Root-level screens
│           ├── collaboration/                   # Collaboration FXML + CSS
│           ├── gamification/                    # Gamification FXML + CSS
│           └── css/                             # Global stylesheets
├── sql/                                         # Database migration scripts
├── docs/                                        # Integration guides
├── events/                                      # Uploaded event images
├── uploads/                                     # User file uploads (KYC, etc.)
└── pom.xml
```

---

## Modules

### Authentication & Users

Handles login, registration, and session management.

- **Entities**: `User`, `UserProfile`
- **Services**: `UserAuthService`, `UserService`
- **Screens**: `Login.fxml`, `User.fxml`
- **Features**:
  - Login with role detection (admin / entrepreneur / investor / mentor)
  - User profile management
  - KYC document upload (`uploads/kyc/`)

---

### Events & Inscriptions

Manage platform events and user registrations.

- **Entities**: `Evenement`, `Inscription`
- **Services**: `EvenementService`, `InscriptionService`, `EmailService`
- **Screens**: `EventsPage.fxml`, `AddEvenement.fxml`, `ShowEvenement.fxml`, `AddInscription.fxml`, `ShowInscription.fxml`, `EventManagement.fxml`
- **Features**:
  - Create, edit, delete events
  - Register/unregister for events
  - Email notifications via `EmailService`
  - AI-generated event images via `EventImageGenerator`
  - Map integration (`map.html` + `GeocodingService`)

---

### Collaboration (Projects & Investments)

Core investment platform connecting entrepreneurs and investors.

- **Entities**: `collaboration/` package
- **Services**: `collaboration/` package
- **Screens**: `EntrepreneurDashboard.fxml`, `InvestorDashboard.fxml`, `AdminDashboard.fxml`, `DealRoom.fxml`, `AddProject.fxml`, `ShowProject.fxml`, `UpdateProject.fxml`, `AddInvestment.fxml`, `ShowInvestment.fxml`, `UpdateInvestment.fxml`
- **Features**:
  - Entrepreneurs submit and manage projects
  - Investors browse and fund projects
  - Deal Room for negotiation
  - Admin validation queue
  - Platform statistics dashboard

---

### Product Management

Marketplace module for selling digital and physical products.

- **Entity**: `Product`
- **Service**: `ProductService`
- **Controller**: `ProductManagementController`
- **Screen**: `collaboration/ProductManagement.fxml`
- **Database tables**: `product`, `sale`
- **Features**:
  - Full CRUD (admin only)
  - Multi-currency: TND, USD, EUR, GBP
  - Product statuses: `draft`, `published`, `archived`
  - Discount (remise) percentage
  - Digital product support
  - Real-time search by name, category, or ID
  - Product statistics: total, published, views, sales

> See [`docs/PRODUCT_SETUP_GUIDE.md`](docs/PRODUCT_SETUP_GUIDE.md) and [`docs/PRODUCT_INTEGRATION.md`](docs/PRODUCT_INTEGRATION.md) for full details.

---

### Forum

Community discussion board with voting and moderation.

- **Entities**: `ForumPost`, `ForumComment`, `ForumPostVote`, `ForumCommentVote`
- **Service**: `ForumPostService`
- **Screen**: `Forum.fxml`
- **Features**:
  - Create and reply to posts
  - Upvote / downvote posts and comments
  - Bad words filter via `BadWordsFilter`
  - Mention parsing via `MentionParser`
  - Light/dark theme support (`forum-styles.css`, `forum-styles-light.css`)

---

### Gamification (Courses, Quizzes & Badges)

Learning and reward system to engage platform users.

- **Entities**: `Course`, `CourseHistory`, `CourseInteraction`, `CourseReport`, `Quiz`, `Question`, `QuestionOption`, `UserQuiz`, `Badge`, `UserBadge`, `UserPoints`, `PointTransaction`
- **Services**: `gamification/` package
- **Screens**: `CourseCatalogView.fxml`, `CourseContentView.fxml`, `CourseForm.fxml`, `QuizForm.fxml`, `QuizTakingView.fxml`, `BadgeForm.fxml`, `MainMenu.fxml`
- **Features**:
  - Course catalog with video content (YouTube API integration)
  - Quiz system with multiple-choice questions
  - Badge awards and point transactions
  - PDF text extraction via Apache PDFBox
  - OCR support via Tesseract
  - Text-to-Speech via FreeTTS
  - Confetti animation on achievements (`ConfettiEngine`)
  - Excel export via Apache POI

---

## Database Setup

The main database is named `3a8`. Run the scripts in order:

```bash
# Core schema (users, events, forum, etc.)
mysql -u root -p 3a8 < sql/investi.sql

# Product management tables
mysql -u root -p 3a8 < sql/product_integration.sql

# Collaboration tables (projects, investments)
mysql -u root -p 3a8 < sql/collaboration_integration.sql

# Optional: add mentor role
mysql -u root -p 3a8 < sql/add_mentor_role.sql
```

Verify:

```sql
USE 3a8;
SHOW TABLES;
```

---

## Configuration

Copy the example config and fill in your values:

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/3a8
DB_USER=root
DB_PASSWORD=your_password

# Email (for event notifications)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USER=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Google APIs (YouTube, Geocoding)
YOUTUBE_API_KEY=your_youtube_api_key
GEOCODING_API_KEY=your_geocoding_api_key

# AI / Summarization
AI_API_KEY=your_ai_api_key
```

> **Never commit `config.properties`** — it is listed in `.gitignore`.

---

## Build & Run

```bash
# Compile
mvn clean compile

# Run the application
mvn javafx:run

# Run tests
mvn test

# Package as JAR
mvn clean package
```

---

## Key Tools & Utilities

| Class                  | Purpose                                      |
|------------------------|----------------------------------------------|
| `MyConnection`         | Singleton MySQL connection                   |
| `EnvConfig`            | Loads `config.properties`                    |
| `DBPatcher`            | Applies incremental DB patches               |
| `EmailService`         | Sends email notifications (JavaMail)         |
| `GeocodingService`     | Converts addresses to coordinates            |
| `EventImageGenerator`  | AI-generated event cover images              |
| `SummarizationService` | AI text summarization                        |
| `TranslationService`   | Text translation via external API            |
| `BadWordsFilter`       | Filters inappropriate content in forum       |
| `MentionParser`        | Parses @mentions in forum posts              |
| `ThemeManager`         | Manages light/dark theme switching           |
| `ConfettiEngine`       | Animated confetti for gamification rewards   |

---

## Role-Based Access Control

| Role          | Access                                                        |
|---------------|---------------------------------------------------------------|
| `admin`       | Full access — validate projects, manage products, view stats  |
| `entrepreneur`| Submit and manage own projects, access deal room              |
| `investor`    | Browse projects, make investments, access deal room           |
| `mentor`      | Mentor dashboard, course management                           |
| (any user)    | Browse events, forum, course catalog                          |

Security is enforced at the service layer:

```java
private boolean isAdmin(User user) {
    return user != null && "admin".equalsIgnoreCase(user.getRole());
}
```

---

## Credits

- **Platform**: Investi — Connexion 3A8
- **Product Module**: Adapted from Moez's gestion product system
- **Collaboration Module**: Projects & investments integration
- **Gamification Module**: Courses, quizzes, and badge system
- **Events Module**: Event management with email and map support
- **Forum Module**: Community discussion with voting and moderation

---

> For detailed integration documentation, see the [`docs/`](docs/) folder.
