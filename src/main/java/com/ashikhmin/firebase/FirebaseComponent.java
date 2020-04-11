package com.ashikhmin.firebase;

import com.ashikhmin.controller.ActorController;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import java.util.logging.Logger;
import java.util.logging.Level;

@Component
public class FirebaseComponent {
    Logger logger = Logger.getLogger(ActorController.class.getName());

    public FirebaseComponent() {
        try {
            initializeApp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty())
            return;
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials.fromStream(new FileInputStream("server_key.json")))
                .setProjectId("ismobileapp")
                .build();
        FirebaseApp.initializeApp(options);
    }

    public void sendNotificationToTokens(
            Notification notification,
            Collection<String> tokens
    ) throws FirebaseMessagingException {
        if (tokens == null || tokens.isEmpty())
            return;

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(tokens)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
        logger.log(Level.INFO, String.format(
                "Sent %s firebase messages. Successful: %s, Unsuccessful: %s.",
                response.getResponses().size(),
                response.getSuccessCount(),
                response.getFailureCount())
        );
    }
}