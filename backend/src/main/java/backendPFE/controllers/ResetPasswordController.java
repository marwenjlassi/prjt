package backendPFE.controllers;

import backendPFE.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/reset-password")
public class ResetPasswordController {
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        try {
            authenticationService.resetUserPassword(email);
            return ResponseEntity.ok("Password reset successfully. Check your email for the new password.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resetting password: " + e.getMessage());
        }
    }
}
