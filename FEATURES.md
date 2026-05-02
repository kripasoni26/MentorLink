# MentorLink Features

Detailed breakdown of the functionalities implemented in the MentorLink platform.

## 1. Authentication & Onboarding
- **Role Selection:** Users choose between being a "Mentor" or a "Student" during signup.
- **Persistent Login:** The app remembers the user session using Firebase Auth.
- **Smart Routing (RoleGate):** Upon opening, the app automatically routes Mentors to their Inbox and Students to the Mentor Discovery list.

## 2. Mentor Discovery (Student Side)
- **Live Mentor List:** A real-time scrolling list of all available mentors.
- **Expertise-Based Search:** Students can type into a search bar to find mentors by specific skills (e.g., searching "Java" filters out non-relevant profiles).
- **Rich Profiles:** View mentor names, bios, and specific areas of expertise before starting a chat.

## 3. Real-Time Messaging
- **Instant Chat:** Messages appear as they are sent without needing to refresh the screen.
- **Safe Conversation IDs:** System creates unique, sorted conversation paths (e.g., `uid1_uid2`) to ensure both parties always land in the same chat room.
- **Message History:** Full chat history is stored and loaded efficiently.
- **Human-Readable Timestamps:** Smart formatting (e.g., "10:30 AM" for today, "Oct 5" for older messages).

## 4. Inbox Management
- **Consolidated View:** Mentors and Students can see all their active conversations.
- **Smart Sorting:** Conversations with the newest messages automatically jump to the top of the list.
- **Contextual UI:** Mentors see Student names in their inbox; Students see Mentor names.

## 5. Profile & Customization
- **Self-Service Updates:** Users can update their name, bio, and expertise at any time.
- **Avatar Support:** Supports profile pictures via image URLs (powered by Glide).

## 6. Technical Hardening
- **Empty States:** Helpful illustrations and text when no mentors are found or the inbox is empty.
- **Loading Indicators:** Visual feedback (spinners) during network-heavy operations like signing in or saving data.
- **Network Resilience:** Local ID tracking prevents duplicate messages from appearing if the internet connection is unstable.
- **Data Security:** Server-side rules ensure users can only read their own private messages.
