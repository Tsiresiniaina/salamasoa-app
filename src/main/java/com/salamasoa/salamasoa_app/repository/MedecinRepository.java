package com.salamasoa.salamasoa_app.repository;

import com.salamasoa.salamasoa_app.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedecinRepository extends JpaRepository<Medecin,String> {
    // Recherche par spécialité
    List<Medecin> findByGradeContainingIgnoreCase(String grade);
    /**
     * Recherche le dernier code médecin de type MD-000001, MD-000002...
     * Cette méthode servira à générer le code suivant.
     */
    Optional<Medecin>
    findTopByCodemedStartingWithOrderByCodemedDesc(String prefix);
}
