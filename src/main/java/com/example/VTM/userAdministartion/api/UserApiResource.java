package com.example.VTM.userAdministartion.api;


import com.example.VTM.userAdministartion.config.GoogleTokenVerifier;
import com.example.VTM.userAdministartion.dataOrModel.ForgotPasswordRequest;
import com.example.VTM.userAdministartion.dataOrModel.ResetPasswordRequest;
import com.example.VTM.userAdministartion.dataOrModel.UserData;
import com.example.VTM.userAdministartion.dataOrModel.UserLoginData;
import com.example.VTM.userAdministartion.exceptions.InvalidContactNumberException;
import com.example.VTM.userAdministartion.exceptions.InvalidCredentialsException;
import com.example.VTM.userAdministartion.exceptions.InvalidEmailException;
import com.example.VTM.userAdministartion.exceptions.InvalidUsernameException;
import com.example.VTM.userAdministartion.services.UserWritePlatformService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserApiResource {


    private final UserWritePlatformService userWritePlatformService;

    @Autowired
    public UserApiResource(UserWritePlatformService userWritePlatformService) {
        this.userWritePlatformService = userWritePlatformService;
    }


    @PostMapping("/user/register")
    public ResponseEntity<?> addUser(@RequestBody UserData userData) {
        try {
            UserData user = userWritePlatformService.addUser(userData);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (InvalidContactNumberException e) {
            // 200 OK with message
            return ResponseEntity.ok(Map.of("message", "Contact number already exists"));
        } catch (InvalidEmailException e) {
            // 200 OK with message
            return ResponseEntity.ok(Map.of("message", "Email already exists"));
        } catch (InvalidUsernameException e) {
            return ResponseEntity.ok(Map.of("message", "Username already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String contactNumber, @RequestParam String otp) {
        try {
            UserData user = userWritePlatformService.verifyOtp(contactNumber, otp);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginData userLoginData) {
        try {
            return userWritePlatformService.userLogin(userLoginData);

        } catch (InvalidCredentialsException e) {
            // Custom exception for wrong username or password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/user/getUserMasterDataById/{id}")
    public ResponseEntity<UserData> getUserMasterDataById(@PathVariable("id") Long id) {
        try {
            return userWritePlatformService.getUserMasterDataById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/profile")
    public ResponseEntity<UserData> getUserMasterDataFromToken(@RequestHeader("Authorization") String authHeader) {
        try{
            return userWritePlatformService.getUserMasterDataFromToken(authHeader);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/user/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            return userWritePlatformService.forgotPassword(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process forgot password: " + e.getMessage());
        }
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            return userWritePlatformService.resetPassword(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password: " + e.getMessage());
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            if (idToken == null || idToken.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing Google ID token"));

            }



            // Verify token
            GoogleIdToken.Payload payload = GoogleTokenVerifier.verifyToken(idToken);

            if (payload == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid Google ID token"));
            }

            // Extract user info
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            // Delegate login/signup handling to your service
            return userWritePlatformService.handleGoogleLogin(email, name, picture);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Google login failed: " + e.getMessage()));
        }
    }


    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", oAuth2User.getAttribute("name"));
        response.put("email", oAuth2User.getAttribute("email"));
        response.put("loginType", "google");
        return ResponseEntity.ok(response);
    }





}
