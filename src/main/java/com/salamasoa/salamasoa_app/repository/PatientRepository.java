package com.salamasoa.salamasoa_app.repository;

import com.salamasoa.salamasoa_app.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient,String> {
    // Recherche de patients par nom (insensible à la casse)
    List<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
    //recherches du codepat du dernier patient
    Optional<Patient> findTopByCodepatStartingWithOrderByCodepatDesc(String prefix);
}
