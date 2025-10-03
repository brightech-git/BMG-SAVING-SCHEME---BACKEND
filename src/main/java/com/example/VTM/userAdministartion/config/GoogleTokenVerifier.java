package com.example.VTM.userAdministartion.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

public class GoogleTokenVerifier {

    private static final String CLIENT_ID = "836611490588-acls1466id475nq0c8bunen9e0b5ifdt.apps.googleusercontent.com";

    public static GoogleIdToken.Payload verifyToken(String idTokenString) throws Exception {
        if (idTokenString == null || idTokenString.isEmpty()) {
            throw new IllegalArgumentException("ID token must not be null or empty");
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            // Debug log the token
            System.out.println("Verified Google ID Token: " + idToken);

            return idToken.getPayload(); // return payload after logging
        } else {
            throw new SecurityException("Invalid Google ID token");
        }
    }
}
