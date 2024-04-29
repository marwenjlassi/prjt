package backendPFE.repository;

import backendPFE.models.Matricule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatriculeRepository extends JpaRepository<Matricule,Integer> {
    List<Matricule> findAll(); // Method to fetch all matricules
}
