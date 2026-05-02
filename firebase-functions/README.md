# Firebase Functions

This folder contains the notification trigger used by the Android app.

## Deploy

1. Install Firebase CLI.
2. Run `firebase login`
3. Run `firebase init functions`
4. Replace the generated `functions/index.js` with [index.js](/D:/Dhruv%20Soni/Documents/New%20project/firebase-functions/index.js).
5. Deploy with `firebase deploy --only functions`

## What it does

Every time the app writes a record into `notificationQueue`, the function sends an FCM push notification to the target device token and then deletes the queue item.
