package com.example.VTM.userAdministartion.services;

import com.example.VTM.server.JwtTokenConfig.JwtService;
import com.example.VTM.server.JwtTokenConfig.JwtTokenUtil;
import com.example.VTM.userAdministartion.dataOrModel.ForgotPasswordRequest;
import com.example.VTM.userAdministartion.dataOrModel.ResetPasswordRequest;
import com.example.VTM.userAdministartion.dataOrModel.UserData;
import com.example.VTM.userAdministartion.dataOrModel.UserLoginData;
import com.example.VTM.userAdministartion.entityOrDomain.User;
import com.example.VTM.userAdministartion.exceptions.InvalidContactNumberException;
import com.example.VTM.userAdministartion.exceptions.InvalidEmailException;
import com.example.VTM.userAdministartion.exceptions.InvalidUsernameException;
import com.example.VTM.userAdministartion.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class UserWritePlatformServiceImpl implements UserWritePlatformService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final SmsOtpService smsOtpService;

    @Autowired
    public UserWritePlatformServiceImpl(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        CustomUserDetailsService userDetailsService,
                                        JwtTokenUtil jwtTokenUtil,
                                        JwtService jwtService,
                                        OtpService otpService,
                                        SmsOtpService smsOtpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.smsOtpService = smsOtpService;
    }

    /** -------------------- Registration -------------------- */
    @Override
    public UserData addUser(UserData userData) {
        // Validate
        if (userData.getContactNumber() == null || userData.getContactNumber().isEmpty()) {
            throw new IllegalArgumentException("Contact number is required.");
        }
        if (userRepository.existsByContactNumber(userData.getContactNumber())) {
            throw new InvalidContactNumberException("Contact number already in use.");
        }
        if (userRepository.existsByEmail(userData.getEmail())) {
            throw new InvalidEmailException("Email already in use.");
        }
        if (userRepository.existsByUsername(userData.getUsername())) {
            throw new InvalidUsernameException("Username already exists.");
        }

        // Generate OTP
        String otp = generateOtp();

        // Save pending user
        otpService.savePendingUser(userData, otp);

        // Send SMS with hashKey (safe null check)
        sendOtpWithHashKey(userData.getContactNumber(), otp, userData.getHashKey());

        // Prepare response
        UserData response = new UserData();
        response.setUsername(userData.getUsername());
        response.setEmail(userData.getEmail());
        response.setContactNumber(userData.getContactNumber());
        response.setOtp(otp); // dev/testing only
        response.setErrorMessage("OTP sent to registered number");
        return response;
    }

    /** -------------------- Login -------------------- */
    @Override
    public ResponseEntity<?> userLogin(UserLoginData userLoginData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String input = userLoginData.getContactOrEmailOrUsername();
            String password = userLoginData.getPassword();

            if (input == null || input.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return errorResponse(response, "Invalid username or password");
            }

            User user = userRepository.findByUsernameOrEmailOrContactNumber(input, input, input);
            if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                return errorResponse(response, "Invalid username or password");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails, user.getId(), user.getEmail(), user.getContactNumber());

            response.put("status", "success");
            response.put("message", "User login successful");
            response.put("token", token);
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("contact", user.getContactNumber());

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Login failed: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(Map<String, Object> map, String message) {
        map.put("status", "error");
        map.put("message", message);
        return ResponseEntity.ok(map);
    }

    /** -------------------- Fetch User -------------------- */
    @Override
    public ResponseEntity<UserData> getUserMasterDataById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(user -> ResponseEntity.ok(convertUserToUserData(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UserData("User not found")));
    }

    @Override
    public ResponseEntity<UserData> getUserMasterDataFromToken(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = Long.valueOf(jwtService.getUserIdFromToken(token));
            Optional<User> optionalUser = userRepository.findById(userId);
            return optionalUser.map(user -> ResponseEntity.ok(convertUserToUserData(user)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new UserData("User not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserData("Invalid or expired token"));
        }
    }

    /** -------------------- Verify OTP -------------------- */
    @Override
    public UserData verifyOtp(String contactNumber, String otp) {
        if (!otpService.verifyOtp(contactNumber, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        UserData pendingUser = otpService.getPendingUserWithoutOtpCheck(contactNumber);
        if (pendingUser == null) throw new RuntimeException("No signup request found");
        if (userRepository.findByContactNumber(contactNumber) != null) throw new RuntimeException("Account exists");

        User user = new User();
        user.setUsername(pendingUser.getUsername());
        user.setEmail(pendingUser.getEmail());
        user.setContactNumber(pendingUser.getContactNumber());
        user.setPassword(passwordEncoder.encode(pendingUser.getPassword()));
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        otpService.removePendingUser(contactNumber);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails, savedUser.getId(), savedUser.getEmail(), savedUser.getContactNumber());

        UserData response = convertUserToUserData(savedUser);
        response.setToken(token);
        return response;
    }

    /** -------------------- Forgot Password -------------------- */
    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        if (request.getContactNumber() == null || request.getContactNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Contact number is required.");
        }

        User user = userRepository.findByContactNumber(request.getContactNumber());
        if (user != null) {
            String otp = generateOtp();
            otpService.saveOtp(user.getContactNumber(), otp);
            sendOtpWithHashKey(user.getContactNumber(), otp, request.getHashKey());
        }

        return ResponseEntity.ok("If the account exists, an OTP has been sent.");
    }

    /** -------------------- Reset Password -------------------- */
    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {
        String storedOtp = otpService.getStoredOtpForNumber(request.getContactNumber());
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        User user = userRepository.findByContactNumber(request.getContactNumber());
        if (user == null) return ResponseEntity.badRequest().body("Invalid contact number.");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpService.clearOtp(request.getContactNumber());

        return ResponseEntity.ok("Password has been reset successfully.");
    }

    /** -------------------- Google Login -------------------- */
    @Override
    public ResponseEntity<?> handleGoogleLogin(String email, String name, String picture) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                String baseUsername = (name != null && !name.isBlank()) ? name.trim() : "GoogleUser";
                String uniqueUsername = baseUsername;
                int counter = 1;
                while (userRepository.existsByUsername(uniqueUsername)) {
                    uniqueUsername = baseUsername + counter++;
                }

                user = new User();
                user.setUsername(uniqueUsername);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("GOOGLE_LOGIN_ONLY"));
                user.setContactNumber("GOOGLE_" + UUID.randomUUID());
                user.setStatus("ACTIVE");
                user.setSocialMedia("GOOGLE");
                user = userRepository.save(user);
            } else {
                user.setStatus("ACTIVE");
                user.setSocialMedia("GOOGLE");
                userRepository.save(user);
            }

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(new ArrayList<>())
                    .build();

            String token = jwtTokenUtil.generateToken(userDetails, user.getId(), user.getEmail(), user.getContactNumber(), user.getSocialMedia());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Google login successful");
            response.put("token", token);
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("contactNumber", user.getContactNumber());
            response.put("socialMedia", user.getSocialMedia());
            response.put("picture", picture);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Google login failed for email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** -------------------- Utility Methods -------------------- */
    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void sendOtpWithHashKey(String contactNumber, String otp, String hashKey) {
        if (hashKey == null) hashKey = ""; // prevent null issues
        smsOtpService.sendOtpSms(contactNumber, otp, hashKey);
    }

    private UserData convertUserToUserData(User user) {
        UserData ud = new UserData();
        ud.setUsername(user.getUsername());
        ud.setEmail(user.getEmail());
        ud.setPassword(user.getPassword());
        ud.setContactNumber(user.getContactNumber());
        return ud;
    }
}
