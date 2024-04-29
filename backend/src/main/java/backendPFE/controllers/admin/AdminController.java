package backendPFE.controllers.admin;


import backendPFE.Request.MatriculeRequest;
import backendPFE.models.Matricule;
import backendPFE.repository.MatriculeRepository;
import backendPFE.service.AuthenticationService;
import backendPFE.Request.UpdateUserRequest;
import backendPFE.models.User;
import backendPFE.repository.UserRepository;
import backendPFE.service.MatriculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService userService;

    @Autowired
    private MatriculeService matriculeService;
    @Autowired
    private MatriculeRepository matriculeRepository;


    @Autowired
    public AdminController(AuthenticationService userService, MatriculeService matriculeService) {
        this.userService = userService;
        this.matriculeService = matriculeService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allusers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{id}")
    public Optional<User> getUser(@PathVariable Integer id) {
        return userRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/delete-user/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        System.out.println("utilisateur was deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserRequest updateUserRequest) {
        // Check if the user service is properly autowired
        if (userService == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User service is not properly initialized");
        }

        userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok("User updated successfully");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-matricules")
    public List<Matricule> getAllMatricules() {
        return matriculeRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-matricule")
    public ResponseEntity<String> addMatricule(@RequestBody MatriculeRequest matriculeRequest) {
        System.out.println("Added eecutedé");
        String series = matriculeRequest.getSeries();
        String location = matriculeRequest.getLocation();
        String registrationNumber = matriculeRequest.getRegistrationNumber();
        System.out.println(matriculeRequest.getSeries());
        System.out.println(matriculeRequest.getLocation());
        System.out.println(matriculeRequest.getRegistrationNumber());

        // Check if any of the parts is null or empty
        if (series == null || series.isEmpty() ||
                location == null || location.isEmpty() ||
                registrationNumber == null || registrationNumber.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All matricule parts are required");
        }

        // Add the matricule using the matriculeService
        Matricule addedMatricule = matriculeService.addMatricule(series, location, registrationNumber);

        // Log the added matricule
        System.out.println("Added Matricule: " + addedMatricule);

        // Check if the matricule was successfully added
        if (addedMatricule != null) {
            return ResponseEntity.ok("Matricule added successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add matricule");
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-matricule/{id}")
    public void deleteMatricule(@PathVariable Integer id) {
        // Check if the matricule with the given ID exists
        Optional<Matricule> optionalMatricule = matriculeRepository.findById(id);
        if (optionalMatricule.isPresent()) {
            // If the matricule exists, delete it
            matriculeRepository.deleteById(id);
        } else {
            // If the matricule does not exist, you can throw an exception or handle it as needed
            // For simplicity, let's throw an IllegalArgumentException
            throw new IllegalArgumentException("Matricule not found with ID: " + id);
        }
    }
}


