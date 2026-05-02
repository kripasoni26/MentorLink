const admin = require("firebase-admin");
const {onValueCreated} = require("firebase-functions/v2/database");

admin.initializeApp();

exports.sendChatNotification = onValueCreated(
  {
    ref: "/notificationQueue/{notificationId}",
    region: "us-central1",
  },
  async (event) => {
    const payload = event.data.val();
    if (!payload || !payload.receiverToken) {
      return null;
    }

    try {
      await admin.messaging().send({
        token: payload.receiverToken,
        notification: {
          title: payload.title || "New message",
          body: payload.body || "You have a new message.",
        },
        data: {
          senderUid: payload.senderUid || "",
          receiverUid: payload.receiverUid || "",
        },
        android: {
          priority: "high",
        },
      });
    } catch (error) {
      console.error("Error sending notification:", error);
    }

    return event.data.ref.remove();
  }
);