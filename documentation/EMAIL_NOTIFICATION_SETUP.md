# Email Notification System Setup

## Overview
Automated email notifications are sent to users when they earn new badges.

## Features
- Beautiful HTML email templates
- Badge achievement notifications
- Asynchronous sending (non-blocking)
- Gmail SMTP support

## Setup Instructions

### 1. Add Maven Dependency
Already added to `pom.xml`:
```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.6.2</version>
</dependency>
```

### 2. Configure Email Settings

#### For Gmail Users:

1. **Enable 2-Factor Authentication** on your Google Account
   - Go to: https://myaccount.google.com/security
   - Enable 2-Step Verification

2. **Generate App Password**
   - Go to: https://myaccount.google.com/apppasswords
   - Select "Mail" and your device
   - Copy the 16-character password

3. **Update EmailService.java**
   - Open `src/main/java/edu/connections3a8/services/EmailService.java`
   - Replace these constants:
   ```java
   private static final String FROM_EMAIL = "your-email@gmail.com";
   private static final String FROM_PASSWORD = "your-app-password";
   ```

#### For Other Email Providers:

Update SMTP settings in `EmailService.java`:
```java
private static final String SMTP_HOST = "smtp.your-provider.com";
private static final String SMTP_PORT = "587"; // or 465 for SSL
```

### 3. Add User Email to Database

Run this SQL to add email field to users table:

```sql
-- Add email column to users table (if not exists)
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(100);

-- Update existing users with email (example)
UPDATE users SET email = 'user@example.com', name = 'John Doe' WHERE id = 1;
```

### 4. Update QuizTakingController

Replace the placeholder email in `sendBadgeEmails()` method:

```java
// Get user email from database
String userEmail = gamificationService.getUserEmail(currentUserId);
String userName = gamificationService.getUserName(currentUserId);
```

## Email Template

The email includes:
- 🏆 Trophy header with gradient background
- Badge name and description
- Points required
- Beautiful HTML styling
- Responsive design

## Testing

Test email configuration:
```java
EmailService emailService = new EmailService();
boolean isValid = emailService.testConnection();
System.out.println("Email config valid: " + isValid);
```

## Troubleshooting

### "Authentication failed" Error
- Make sure you're using an App Password, not your regular password
- Check that 2FA is enabled on your Google account

### "Connection timeout" Error
- Check your internet connection
- Verify SMTP host and port are correct
- Check if your firewall is blocking port 587

### Emails not sending
- Check spam folder
- Verify recipient email is correct
- Check console for error messages

## Security Notes

⚠️ **IMPORTANT**: Never commit email credentials to version control!

- Keep `email.properties` in `.gitignore`
- Use environment variables for production
- Consider using a dedicated email service account

## Alternative: Using SendGrid

For production, consider using SendGrid API:

1. Sign up at https://sendgrid.com (free tier: 100 emails/day)
2. Get API key
3. Use SendGrid Java library instead of JavaMail

## Future Enhancements

- Email templates for quiz results
- Weekly progress reports
- Course completion certificates
- Customizable email preferences
- Email verification system
