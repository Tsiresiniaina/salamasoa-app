package com.salamasoa.salamasoa_app.service;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import com.salamasoa.salamasoa_app.repository.MedecinRepository;
import com.salamasoa.salamasoa_app.repository.PatientRepository;
import com.salamasoa.salamasoa_app.repository.VisiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class VisiteService {

    private static final String VISITE_CODE_PREFIX = "VS-";

    /**
     * Durée standard d'une consultation.
     *
     * Deux visites d'un même médecin doivent être espacées d'au moins
     * cette durée. Elle n'est pas stockée en base : toutes les
     * consultations sont considérées de même longueur.
     */
    public static final Duration DUREE_CONSULTATION = Duration.ofMinutes(30);

    private static final DateTimeFormatter HEURE_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final VisiteRepository visiteRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Autowired
    public VisiteService(
            VisiteRepository visiteRepository,
            PatientRepository patientRepository,
            MedecinRepository medecinRepository
    ) {
        this.visiteRepository = visiteRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
    }

    public List<Visite> getAllVisites() {
        return visiteRepository.findAllByOrderByDateheureAsc();
    }

    public Optional<Visite> getVisiteByCodevisite(String codevisite) {
        return visiteRepository.findById(codevisite);
    }

    public void deleteVisite(String codevisite) {
        visiteRepository.deleteById(codevisite);
    }

    public List<Visite> getVisitesByPatient(String codepat) {
        return visiteRepository.findByPatientCodepat(codepat);
    }

    public List<Visite> getVisitesByMedecin(String codemed) {
        return visiteRepository.findByMedecinCodemed(codemed);
    }

    public List<Visite> getVisitesByStatut(StatutVisite statut) {
        return visiteRepository.findByStatut(statut);
    }

    /**
     * Retourne les visites d'une journée entière,
     * triées par date et heure.
     */
    public List<Visite> getVisitesByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        System.out.println(">>> getVisitesByDate : date demandée = " + date
                + " | intervalle = [" + start + " -> " + end + "]");

        List<Visite> resultats = visiteRepository
                .findByDateheureBetweenOrderByDateheureAsc(start, end);

        System.out.println(">>> Résultat : " + resultats.size() + " visite(s)");
        return resultats;
    }

    /**
     * Crée une visite avec le statut PLANIFIEE.
     *
     * Vérifie :
     * - patient existant et actif ;
     * - médecin existant et actif ;
     * - date/heure renseignée ;
     * - absence de conflit pour le médecin.
     */
    public Visite createVisite(
            String codepat,
            String codemed,
            LocalDateTime dateheure
    ) {
        validateVisitData(codepat, codemed, dateheure);

        Patient patient = patientRepository.findById(codepat)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient introuvable : " + codepat
                ));

        if (!patient.isActif()) {
            throw new IllegalArgumentException(
                    "Ce patient est inactif et ne peut pas recevoir "
                            + "une nouvelle visite."
            );
        }

        Medecin medecin = medecinRepository.findById(codemed)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin introuvable : " + codemed
                ));

        if (!medecin.isActif()) {
            throw new IllegalArgumentException(
                    "Ce médecin est inactif et ne peut pas recevoir "
                            + "de nouvelle visite."
            );
        }

        verifierDisponibiliteMedecin(codemed, dateheure, "");

        // Règle métier : une consultation ne peut pas être planifiée dans le passé.
        if (dateheure.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Impossible de planifier une visite à une date ou heure "
                            + "antérieure à maintenant.");
        }

        Visite visite = new Visite();

        visite.setCodevisite(generateVisiteCode());
        visite.setPatient(patient);
        visite.setMedecin(medecin);
        visite.setDateheure(dateheure);
        visite.setStatut(StatutVisite.PLANIFIEE);

        return visiteRepository.save(visite);
    }

    /**
     * Modifie le patient, le médecin et la date/heure d'une visite existante.
     *
     * Applique les mêmes règles que createVisite :
     * - patient existant et actif ;
     * - médecin existant et actif ;
     * - date/heure renseignée et non passée ;
     * - absence de conflit pour le médecin (la visite modifiée est exclue
     *   de la recherche de conflit, sinon elle se bloquerait elle-même).
     *
     * Le code visite et le statut ne sont pas modifiés ici : le statut se
     * change via updateVisiteStatus.
     */
    public Visite updateVisite(
            String codevisite,
            String codepat,
            String codemed,
            LocalDateTime dateheure
    ) {
        validateVisitData(codepat, codemed, dateheure);

        Visite visite = visiteRepository.findById(codevisite)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visite introuvable : " + codevisite
                ));

        /*
         * Une consultation commencée ne se modifie plus : le patient est
         * déjà avec le médecin. Changer l'un des deux, ou l'horaire,
         * contredirait ce qui est en train de se passer.
         */
        if (visite.getStatut() == StatutVisite.EN_COURS) {
            throw new IllegalArgumentException(
                    "Cette consultation a déjà commencé : elle ne peut "
                            + "plus être modifiée.\n\nTerminez ou annulez "
                            + "la visite avant toute correction."
            );
        }

        /*
         * Une visite déjà terminée ou annulée appartient à l'historique :
         * elle ne se modifie plus non plus.
         */
        if (visite.getStatut() == StatutVisite.TERMINEE
                || visite.getStatut() == StatutVisite.ANNULEE) {
            throw new IllegalArgumentException(
                    "Une visite "
                            + visite.getStatut().getLibelle().toLowerCase()
                            + " ne peut plus être modifiée."
            );
        }

        Patient patient = patientRepository.findById(codepat)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient introuvable : " + codepat
                ));

        if (!patient.isActif()) {
            throw new IllegalArgumentException(
                    "Ce patient est inactif et ne peut pas recevoir "
                            + "une visite."
            );
        }

        Medecin medecin = medecinRepository.findById(codemed)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin introuvable : " + codemed
                ));

        if (!medecin.isActif()) {
            throw new IllegalArgumentException(
                    "Ce médecin est inactif et ne peut pas recevoir "
                            + "de visite."
            );
        }

        /*
         * On ne réapplique la règle « pas de visite dans le passé » que si
         * l'horaire change réellement. Sans ce test, il deviendrait
         * impossible de corriger le patient d'une visite du matin
         * l'après-midi même.
         */
        boolean horaireModifie = !dateheure.equals(visite.getDateheure());

        if (horaireModifie && dateheure.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Impossible de déplacer une visite à une date ou heure "
                            + "antérieure à maintenant."
            );
        }

        verifierDisponibiliteMedecin(codemed, dateheure, codevisite);

        visite.setPatient(patient);
        visite.setMedecin(medecin);
        visite.setDateheure(dateheure);

        return visiteRepository.save(visite);
    }

    /**
     * Met à jour le statut d'une visite.
     *
     * Exemple :
     * PLANIFIEE → EN_COURS → TERMINEE
     */
    public Visite updateVisiteStatus(
            String codevisite,
            StatutVisite statut
    ) {
        if (statut == null) {
            throw new IllegalArgumentException(
                    "Le statut de la visite est obligatoire."
            );
        }

        Visite visite = visiteRepository.findById(codevisite)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visite introuvable : " + codevisite
                ));

        /*
         * Règle métier : un médecin ne peut consulter qu'un seul patient
         * à la fois. Avant de démarrer une consultation, on vérifie qu'il
         * n'en a pas déjà une en cours.
         */
        if (statut == StatutVisite.EN_COURS) {
            String codemed = visite.getMedecin().getCodemed();

            Optional<Visite> consultationEnCours = visiteRepository
                    .findFirstByMedecinCodemedAndStatut(
                            codemed,
                            StatutVisite.EN_COURS
                    );

            /*
             * Si la visite trouvée est celle qu'on met à jour, il n'y a pas
             * de conflit : le statut est simplement réappliqué.
             */
            if (consultationEnCours.isPresent()
                    && !consultationEnCours.get().getCodevisite()
                    .equals(codevisite)) {

                throw new IllegalArgumentException(
                        "Ce médecin a déjà une consultation en cours avec "
                                + formatPatientName(
                                consultationEnCours.get())
                                + ".\n\nTerminez ou annulez cette "
                                + "consultation avant d'en démarrer "
                                + "une nouvelle."
                );
            }
        }

        visite.setStatut(statut);

        return visiteRepository.save(visite);
    }

    /**
     * Vérifie qu'aucune autre visite du médecin ne chevauche le créneau.
     *
     * Une consultation occupant DUREE_CONSULTATION, deux visites entrent en
     * conflit dès que leurs débuts sont distants de moins d'une durée :
     * 10h00 et 10h15 se chevauchent, 10h00 et 10h30 s'enchaînent.
     *
     * Les visites annulées sont ignorées : elles libèrent leur créneau.
     *
     * @param codevisiteAExclure code de la visite en cours de modification,
     *                           ou chaîne vide lors d'une création
     */
    private void verifierDisponibiliteMedecin(
            String codemed,
            LocalDateTime dateheure,
            String codevisiteAExclure
    ) {
        LocalDateTime borneMin = dateheure.minus(DUREE_CONSULTATION);
        LocalDateTime borneMax = dateheure.plus(DUREE_CONSULTATION);

        List<Visite> conflits = visiteRepository.findConflitsPourMedecin(
                codemed,
                StatutVisite.ANNULEE,
                borneMin,
                borneMax,
                codevisiteAExclure == null ? "" : codevisiteAExclure
        );

        if (conflits.isEmpty()) {
            return;
        }

        Visite conflit = conflits.get(0);

        LocalDateTime finConflit =
                conflit.getDateheure().plus(DUREE_CONSULTATION);

        throw new IllegalArgumentException(
                "Le médecin est déjà occupé de "
                        + conflit.getDateheure().format(HEURE_FORMATTER)
                        + " à "
                        + finConflit.format(HEURE_FORMATTER)
                        + " avec "
                        + formatPatientName(conflit)
                        + ".\n\nUne consultation dure "
                        + DUREE_CONSULTATION.toMinutes()
                        + " minutes : choisissez un autre créneau."
        );
    }

    /**
     * Nom lisible du patient d'une visite, pour les messages d'erreur.
     */
    private String formatPatientName(Visite visite) {
        if (visite.getPatient() == null) {
            return visite.getCodevisite();
        }

        String nom = visite.getPatient().getNom() == null
                ? ""
                : visite.getPatient().getNom();

        String prenom = visite.getPatient().getPrenom() == null
                ? ""
                : visite.getPatient().getPrenom();

        String fullName = (nom + " " + prenom).trim();

        return fullName.isBlank() ? visite.getCodevisite() : fullName;
    }

    private void validateVisitData(
            String codepat,
            String codemed,
            LocalDateTime dateheure
    ) {
        if (codepat == null || codepat.isBlank()) {
            throw new IllegalArgumentException(
                    "Le patient est obligatoire."
            );
        }

        if (codemed == null || codemed.isBlank()) {
            throw new IllegalArgumentException(
                    "Le médecin est obligatoire."
            );
        }

        if (dateheure == null) {
            throw new IllegalArgumentException(
                    "La date et l'heure de la visite sont obligatoires."
            );
        }
    }

    /**
     * Génère VS-000001, VS-000002, etc.
     */
    private synchronized String generateVisiteCode() {
        Optional<Visite> lastVisiteOptional =
                visiteRepository
                        .findTopByCodevisiteStartingWithOrderByCodevisiteDesc(
                                VISITE_CODE_PREFIX
                        );

        int nextNumber = 1;

        if (lastVisiteOptional.isPresent()) {
            String lastCode =
                    lastVisiteOptional.get().getCodevisite();

            String numericPart =
                    lastCode.substring(
                            VISITE_CODE_PREFIX.length()
                    );

            nextNumber = Integer.parseInt(numericPart) + 1;
        }

        return VISITE_CODE_PREFIX
                + String.format("%06d", nextNumber);
    }
}