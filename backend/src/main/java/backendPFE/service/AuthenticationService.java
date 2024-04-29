package backendPFE.service;

import backendPFE.Request.AuthenticationRequest;
import backendPFE.Request.RegisterRequest;
import backendPFE.Request.UpdateUserRequest;
import backendPFE.Exceptions.UserNotFoundException;
import backendPFE.response.AuthenticationResponse;
import backendPFE.security.config.JwtService;
import backendPFE.models.Role;
import backendPFE.models.User;
import backendPFE.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) throws Exception {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("Email already in use");
        }
        User user = createUserFromRequest(request);
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails.getUsername(), userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(((User) userDetails).getId())
                .email(userDetails.getUsername())
                .username(userDetails.getUsername())
                .role(((User) userDetails).getRole().name())
                .build();
    }

    private User createUserFromRequest(RegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.Technicien)  // Assuming you want all new users to be Technicien by default
                .build();
    }

    @PostConstruct
    public void initializeAdminAccount() {
        if (userRepository.count() == 0 || userRepository.findByRole(Role.ADMIN).isEmpty()) {
            // Create the default admin account
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("adminpassword"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(adminUser);
        }
    }

    public void updateUser(Integer userId, UpdateUserRequest updateUserRequest) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update username and email
        existingUser.setUsername(updateUserRequest.getUsername());
        existingUser.setEmail(updateUserRequest.getEmail());

        // Conditionally update password if it's not empty
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        userRepository.save(existingUser);
    }

    public void resetUserPassword(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(), "Your new password", "Your new password is: " + newPassword);
    }
    private String generateRandomPassword() {
        // Generate a random alphanumeric password
        int length = 10;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
