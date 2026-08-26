package com.salamasoa.salamasoa_app.repository;

import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Recherche une visite du médecin qui chevauche le créneau demandé.
     *
     * Toutes les consultations ayant la même durée, deux visites se
     * chevauchent lorsque leurs horaires de début sont distants de moins
     * d'une durée. Les bornes sont donc calculées côté service :
     *   borneMin = debut - duree     borneMax = debut + duree
     * et l'on cherche une visite dont l'horaire tombe strictement entre
     * les deux. Ce calcul en Java évite toute arithmétique de date en SQL,
     * qui différerait entre MySQL et SQL Server.
     *
     * Les visites annulées sont exclues : elles libèrent leur créneau.
     *
     * Pour une modification, codevisiteAExclure porte le code de la visite
     * concernée, sinon elle se déclarerait elle-même en conflit. Pour une
     * création, passer une chaîne vide.
     */
    @EntityGraph(attributePaths = {"patient", "medecin"})
    @Query("""
            select v from Visite v
            where v.medecin.codemed = :codemed
              and v.statut <> :statutExclu
              and v.dateheure > :borneMin
              and v.dateheure < :borneMax
              and v.codevisite <> :codevisiteAExclure
            order by v.dateheure asc
            """)
    List<Visite> findConflitsPourMedecin(
            @Param("codemed") String codemed,
            @Param("statutExclu") StatutVisite statutExclu,
            @Param("borneMin") LocalDateTime borneMin,
            @Param("borneMax") LocalDateTime borneMax,
            @Param("codevisiteAExclure") String codevisiteAExclure
    );

    /**
     * Recherche la visite qu'un médecin a actuellement en cours.
     *
     * Un médecin ne peut consulter qu'un patient à la fois : cette méthode
     * sert à bloquer le démarrage d'une seconde consultation.
     *
     * L'EntityGraph charge le patient, afin de pouvoir citer son nom dans
     * le message d'erreur sans provoquer de LazyInitializationException.
     */
    @EntityGraph(attributePaths = {"patient", "medecin"})
    Optional<Visite> findFirstByMedecinCodemedAndStatut(
            String codemed,
            StatutVisite statut
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