# MentorLink Firebase Setup Guide

Follow these steps to configure the Firebase backend for the MentorLink application.

## 1. Create a Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add Project** and name it "MentorLink".

## 2. Register the Android App
1. Click the **Android icon** to add a new app.
2. **Android Package Name:** `com.mentorlink.app`
3. **App Nickname:** MentorLink
4. Download the `google-services.json` file.
5. Place the file in your project folder at: `MentorLink/app/google-services.json`

## 3. Enable Authentication
1. Go to **Build > Authentication** in the sidebar.
2. Click **Get Started**.
3. Enable the **Email/Password** provider.

## 4. Set Up Realtime Database
1. Go to **Build > Realtime Database**.
2. Click **Create Database**.
3. Choose a location near your users.
4. Start in **Locked Mode**.

## 5. Configure Database Rules & Indexes
This is critical for the "Search" feature and data security.
1. Go to the **Rules** tab in the Realtime Database section.
2. Copy and paste the contents of `firebase-realtime-database-rules.json` from this project.
3. Click **Publish**.

*Note: These rules include an `.indexOn: ["role"]` entry which allows the app to efficiently filter the mentor list.*

## 6. Realtime Database Structure (Reference)
The app automatically manages the following structure:

```json
{
  "users": {
    "uid": {
      "name": "User Name",
      "role": "Mentor or Student",
      "expertise": "Android, Java, etc.",
      "bio": "User bio text"
    }
  },
  "conversations": {
    "convId": {
      "lastMessage": "Hello!",
      "lastUpdated": 1728123456789,
      "mentorId": "...",
      "studentId": "..."
    }
  },
  "messages": {
    "convId": {
      "msgId": {
        "text": "Hello!",
        "senderId": "...",
        "timestamp": 1728123456789
      }
    }
  }
}
```

## 7. A Note on Notifications
The current version of the app has been simplified to run on the **Free Firebase Spark Plan**.
- Push notifications via Cloud Functions are disabled to avoid the requirement for a Blaze (Pay-as-you-go) plan.
- The app uses real-time listeners, so users will see new messages instantly while the app is open.
