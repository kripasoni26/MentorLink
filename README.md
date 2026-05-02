# MentorLink

MentorLink is a real-time Android application designed to bridge the gap between mentors and students. It provides a seamless platform for discovery, connection, and private communication.

## 🚀 Key Features

- **Role-Based Authentication:** Dedicated flows for Mentors and Students.
- **Real-Time Discovery:** Students can browse a live list of mentors.
- **Smart Search & Filtering:** Instantly filter mentors by name or expertise (e.g., "Java", "Android", "UI/UX").
- **Live Chat System:** Real-time private messaging powered by Firebase Realtime Database.
- **Smart Inbox:** Track all your ongoing conversations in one place, sorted by the latest activity.
- **Profile Management:** Customize your bio, expertise, and profile picture.
- **No-Refresh UI:** Lists and chats update automatically without manual refreshing.

## 🛠 Tech Stack

- **Language:** Java
- **UI Framework:** Android ViewBinding, ConstraintLayout, Material Components
- **Backend:** Firebase (Authentication, Realtime Database)
- **Image Loading:** Glide
- **Architecture:** Simplified MVVM-style with centralized Firebase utilities.

## 📦 Project Structure

```text
app/src/main/java/com/mentorlink/app/
├── adapter/     # RecyclerView adapters for lists
├── model/       # Data classes (User, Message, Conversation)
├── service/     # (Optional) Background services
├── ui/          # Activities and UI logic
│   ├── auth/    # Login and Signup
│   ├── chat/    # Messaging screen
│   ├── home/    # Mentor List, Inbox, and Role Routing
│   └── profile/ # User profile editing
└── util/        # Helpers (Firebase, Dates, Image Loading)
```

## ⚙️ Setup Instructions

### 1. Prerequisites
- Android Studio Iguana or newer.
- A Firebase project.

### 2. Firebase Configuration
MentorLink requires a `google-services.json` file to communicate with the backend.
1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android App with package name `com.mentorlink.app`.
3. Download `google-services.json` and place it in the `app/` directory.
4. Enable **Email/Password Auth** and **Realtime Database**.

### 3. Database Rules
Apply the rules found in `firebase-realtime-database-rules.json` to your Firebase console to ensure data security and proper indexing for search.

## 📝 License
This project is for educational and professional development purposes.
