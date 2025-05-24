package com.users.app.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.users.app.dto.ApiResponse;
import com.users.app.dto.SignInRequest;
import com.users.app.dto.UpdatePasswordDto;
import com.users.app.dto.UserDto;
import com.users.app.model.User;
import com.users.app.service.UserService;

@RestController
@Validated
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signUp(@Valid @RequestBody UserDto user) {
        try {
            userService.signUp(user);
            return ResponseEntity.ok(new ApiResponse<>(true, "User signed up successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody SignInRequest signInRequest) {
        try {
            String user = userService.verify(signInRequest.getIdentifier(), signInRequest.getPassword(), signInRequest.getRole());
            return ResponseEntity.ok(new ApiResponse<>(true, "Login Successful", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable String id) {
        try {
            Optional<User> user = userService.getUserById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "User Details", user.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "User Deleted Successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    @PostMapping("/change-password/{id}")
    public ResponseEntity<ApiResponse<String>> updatePassword(@PathVariable String id,@RequestBody UpdatePasswordDto passwordDto){
    	try {
            userService.updatePassword(id,passwordDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "User Password updated Successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@PathVariable String email){
    	try {
            userService.sendForgotPasswordEmail(email, "Health Care Online Doctor Website");
            return ResponseEntity.ok(new ApiResponse<>(true, "New Password has been sent to Your EmailId", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}