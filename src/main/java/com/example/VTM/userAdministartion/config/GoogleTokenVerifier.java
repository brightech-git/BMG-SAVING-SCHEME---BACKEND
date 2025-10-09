package com.example.VTM.userAdministartion.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Arrays;
import java.util.List;

public class GoogleTokenVerifier {

    // Add all your client IDs here
    private static final List<String> CLIENT_IDS = Arrays.asList(
            "657047091285-57kkictc0pkfjldtf0u133m82huit6rg.apps.googleusercontent.com", // iOS
            "657047091285-hetgcscq8hvli59d0c6oqvg9aoat8850.apps.googleusercontent.com", // Web
            "657047091285-blcbc2l96oqb7dndntifoggq5ovluel3.apps.googleusercontent.com"  // Android
    );

    public static GoogleIdToken.Payload verifyToken(String idTokenString) throws Exception {
        if (idTokenString == null || idTokenString.isEmpty()) {
            throw new IllegalArgumentException("ID token must not be null or empty");
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(CLIENT_IDS)  // verify against all clients
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            System.out.println("Verified Google ID Token: " + idToken);
            return idToken.getPayload();
        } else {
            throw new SecurityException("Invalid Google ID token");
        }
    }
}
