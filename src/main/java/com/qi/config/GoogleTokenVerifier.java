package com.qi.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.qi.dto.GoogleUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    @Value("${google.client.id}")
    private String clientId;



    public GoogleUserDTO verify(String token){
        try{
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(Collections.singletonList(clientId)).build();
            GoogleIdToken idToken = verifier.verify(token);
            if(idToken!=null){
                var payload = idToken.getPayload();
                if (payload.getEmail() == null) {
                    throw new RuntimeException("Email not found in token");
                }
                String name = (String)payload.get("name");
                String picture = (String)payload.get("picture");
                return new GoogleUserDTO(payload.getEmail(), name, picture);
            }else {
                throw new RuntimeException("Invalid Google token");
            }

        } catch (Exception e) {
            throw new RuntimeException("Google token verification failed: " + e.getMessage());
        }
    }
}
