# Email Notifications - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Get Gmail App Password (2 minutes)

1. Go to https://myaccount.google.com/apppasswords
2. Sign in to your Google account
3. Click "Select app" → Choose "Mail"
4. Click "Select device" → Choose "Other" → Type "Learning Platform"
5. Click "Generate"
6. **Copy the 16-character password** (example: `abcd efgh ijkl mnop`)

### Step 2: Configure Email Service (1 minute)

Open `src/main/java/edu/connections3a8/services/EmailService.java` and update:

```java
private static final String FROM_EMAIL = "your-email@gmail.com"; // Your Gmail address
private static final String FROM_PASSWORD = "abcd efgh ijkl mnop"; // The app password you just copied
```

### Step 3: Add Email to Personne Table (1 minute)

Run this SQL command:

```sql
-- Add email column to personne table
ALTER TABLE personne 
ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE;

-- Update your test user (replace with actual email)
UPDATE personne SET email = 'your-test-email@gmail.com' WHERE id = 1;

-- Verify
SELECT id, nom, prenom, email FROM personne;
```

### Step 4: Test It! ✅

1. Run your application
2. Complete a quiz and earn a badge
3. Check your email inbox (and spam folder)
4. You should receive a beautiful HTML email! 🎉

## 📧 What the Email Looks Like

- **Subject**: 🏆 Congratulations! You've Earned a New Badge!
- **Content**:
  - Golden trophy header
  - Badge name in large text
  - Badge description
  - Points required
  - "View Your Badges" button
  - Professional footer

## ⚠️ Troubleshooting

**Email not received?**
- Check spam/junk folder
- Verify email address in personne table is correct
- Check console for error messages

**"Authentication failed"?**
- Make sure you're using the App Password, not your regular Gmail password
- Verify 2-Factor Authentication is enabled on your Google account

**"No email address found for user"?**
- Run: `SELECT id, email FROM personne WHERE id = 1;`
- Make sure the email column exists and has a value

**Still not working?**
- Check `documentation/EMAIL_NOTIFICATION_SETUP.md` for detailed troubleshooting

## 🔒 Security Reminder

Never commit your email password to Git! Consider using environment variables for production.

## 🎯 Next Steps

- Add email preferences for users
- Send quiz result emails
- Weekly progress reports
- Course completion certificates
