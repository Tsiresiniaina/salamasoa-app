package com.salamasoa.salamasoa_app.repository;

import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisiteRepository extends JpaRepository<Visite, String> {

    /**
     * Récupère toutes les visites d'un patient.
     */
    List<Visite> findByPatientCodepat(String codepat);

    /**
     * Récupère toutes les visites attribuées à un médecin.
     */
    List<Visite> findByMedecinCodemed(String codemed);

    /**
     * Filtre les visites par statut.
     */
    List<Visite> findByStatut(StatutVisite statut);

    /**
     * Retourne les visites comprises entre deux dates/heures.
     *
     * Cette méthode servira à afficher les visites d'une journée.
     */
    @EntityGraph(attributePaths = {"patient", "medecin"})
    List<Visite> findByDateheureBetweenOrderByDateheureAsc(
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Vérifie si un médecin possède déjà une visite
     * à la même date et heure.
     */
    boolean existsByMedecinCodemedAndDateheure(
            String codemed,
            LocalDateTime dateheure
    );

    /**
     * Récupère le dernier code visite de type :
     * VS-000001, VS-000002, etc.
     */
    Optional<Visite>
    findTopByCodevisiteStartingWithOrderByCodevisiteDesc(
            String prefix
    );
    /**
     * Charge toutes les visites avec leur patient et leur médecin.
     *
     * @EntityGraph évite les LazyInitializationException dans Swing.
     */
    @EntityGraph(attributePaths = {"patient", "medecin"})
    List<Visite> findAllByOrderByDateheureAsc();
}