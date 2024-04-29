package backendPFE.controllers;

import backendPFE.models.Matricule;
import backendPFE.models.Role;
import backendPFE.repository.MatriculeRepository;
import backendPFE.repository.UserRepository;
import backendPFE.service.MatriculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/technicien/")
public class TechnicienController {
    @Autowired
    private MatriculeService matriculeService;
    @Autowired
    private MatriculeRepository matriculeRepository;
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-matricules")
    public List<Matricule> getAllMatricules() {
        return matriculeRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getTechnicienProfile(@PathVariable Integer id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole() == Role.Technicien)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

}
