package com.salamasoa.salamasoa_app.repository;

import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisiteRepository extends JpaRepository<Visite,String> {
    // Récupérer toutes les visites d'un patient
    List<Visite> findByPatientCodepat(String codepat);

    // Récupérer toutes les visites attribuées à un médecin
    List<Visite> findByMedecinCodemed(String codemed);

    // Filtrer les visites par statut (ex: PLANIFIEE, TERMINEE)
    List<Visite> findByStatut(StatutVisite statut);
}
