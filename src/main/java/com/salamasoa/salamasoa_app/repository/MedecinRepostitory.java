package com.salamasoa.salamasoa_app.repository;

import com.salamasoa.salamasoa_app.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedecinRepostitory extends JpaRepository<Medecin,String> {
    // Recherche par spécialité
    List<Medecin> findByGradeContainingIgnoreCase(String grade);
}
