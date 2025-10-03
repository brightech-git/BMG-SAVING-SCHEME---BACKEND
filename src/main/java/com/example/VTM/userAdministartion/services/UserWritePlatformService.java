package com.example.VTM.userAdministartion.services;


import com.example.VTM.userAdministartion.dataOrModel.ForgotPasswordRequest;
import com.example.VTM.userAdministartion.dataOrModel.ResetPasswordRequest;
import com.example.VTM.userAdministartion.dataOrModel.UserData;
import com.example.VTM.userAdministartion.dataOrModel.UserLoginData;
import org.springframework.http.ResponseEntity;

public interface UserWritePlatformService {

    UserData addUser(UserData userData);

    ResponseEntity<?> userLogin(UserLoginData userLoginData);

    ResponseEntity<UserData> getUserMasterDataById(Long id);

    ResponseEntity<UserData> getUserMasterDataFromToken(String authHeader);

    UserData verifyOtp(String contactNumber, String otp);

    ResponseEntity<?> forgotPassword(ForgotPasswordRequest request);

    ResponseEntity<?> resetPassword(ResetPasswordRequest request);

    ResponseEntity<?> handleGoogleLogin(String email, String name, String picture);
}
