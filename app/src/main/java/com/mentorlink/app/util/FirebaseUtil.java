package com.mentorlink.app.util;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseUtil {

    private FirebaseUtil() {
    }

    @NonNull
    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    @NonNull
    public static DatabaseReference db() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseUser currentUser() {
        return auth().getCurrentUser();
    }

    public static String conversationId(String firstUid, String secondUid) {
        return firstUid.compareTo(secondUid) < 0
                ? firstUid + "_" + secondUid
                : secondUid + "_" + firstUid;
    }
}
