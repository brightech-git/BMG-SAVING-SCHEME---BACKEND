package com.example.VTM.userAdministartion.services;


import com.example.VTM.userAdministartion.dataOrModel.UserData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, UserData> pendingUserStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiryMap = new ConcurrentHashMap<>();
    private final Map<String, Long> lastOtpSentTime = new ConcurrentHashMap<>();

    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 min
    private static final long RESEND_COOLDOWN = 60 * 1000; // 1 min

    // Save OTP for a mobile number
    public void saveOtp(String mobileNumber, String otp) {
        otpStorage.put(mobileNumber, otp);
        otpExpiryMap.put(mobileNumber, System.currentTimeMillis() + OTP_VALIDITY_DURATION);
        lastOtpSentTime.put(mobileNumber, System.currentTimeMillis());
    }

    // Save pending user with OTP and expiry
    public void savePendingUser(UserData userData, String otp, int expiryMinutes) {
        String mobileNumber = userData.getContactNumber();
        otpStorage.put(mobileNumber, otp);
        otpExpiryMap.put(mobileNumber, System.currentTimeMillis() + expiryMinutes * 60_000L);
        pendingUserStorage.put(mobileNumber, userData); // ✅ must save pending user
        lastOtpSentTime.put(mobileNumber, System.currentTimeMillis());
    }

    public void savePendingUser(UserData userData, String otp) {
        savePendingUser(userData, otp, 5); // default 5 min
    }

    // Get OTP for a number (without removing pending user)
    public String getStoredOtpForNumber(String mobileNumber) {
        if (isOtpExpired(mobileNumber)) {
            clearOtp(mobileNumber);
            return null;
        }
        return otpStorage.get(mobileNumber);
    }

    // Get OTP (alias)
    public String getOtp(String mobileNumber) {
        return getStoredOtpForNumber(mobileNumber);
    }

    // Get pending user with expiry check
    public UserData getPendingUser(String mobileNumber) {
        if (isOtpExpired(mobileNumber)) {
            removePendingUser(mobileNumber);
            return null;
        }
        return pendingUserStorage.get(mobileNumber);
    }

    // Get pending user without expiry check (for resend)
    public UserData getPendingUserWithoutOtpCheck(String mobileNumber) {
        return pendingUserStorage.get(mobileNumber);
    }

    // Check if OTP can be resent
    public boolean canResend(String mobileNumber) {
        Long lastSent = lastOtpSentTime.get(mobileNumber);
        return lastSent == null || (System.currentTimeMillis() - lastSent) >= RESEND_COOLDOWN;
    }

    public boolean verifyOtp(String mobileNumber, String enteredOtp) {
        if (isOtpExpired(mobileNumber)) {
            clearOtp(mobileNumber); // only remove OTP and expiry, keep pending user
            return false;
        }
        String storedOtp = otpStorage.get(mobileNumber);
        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
            clearOtp(mobileNumber); // ✅ only remove OTP
            return true;
        }
        return false;
    }

    // Clear only OTP
    public void clearOtp(String mobileNumber) {
        otpStorage.remove(mobileNumber);
        otpExpiryMap.remove(mobileNumber);
        lastOtpSentTime.remove(mobileNumber);
    }

    // Remove OTP + pending user (after signup success)
    public void removePendingUser(String mobileNumber) {
        otpStorage.remove(mobileNumber);
        otpExpiryMap.remove(mobileNumber);
        pendingUserStorage.remove(mobileNumber);
        lastOtpSentTime.remove(mobileNumber);
    }

    // Check if OTP expired
    private boolean isOtpExpired(String mobileNumber) {
        Long expiry = otpExpiryMap.get(mobileNumber);
        return expiry == null || System.currentTimeMillis() > expiry;
    }
}
