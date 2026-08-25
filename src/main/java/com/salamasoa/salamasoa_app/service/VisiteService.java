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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisiteService {

    private static final String VISITE_CODE_PREFIX = "VS-";

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

        return visiteRepository
                .findByDateheureBetweenOrderByDateheureAsc(
                        start,
                        end
                );
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

        boolean doctorAlreadyBusy = visiteRepository
                .existsByMedecinCodemedAndDateheure(
                        codemed,
                        dateheure
                );

        if (doctorAlreadyBusy) {
            throw new IllegalArgumentException(
                    "Le médecin possède déjà une visite planifiée "
                            + "à cette date et cette heure."
            );
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

        visite.setStatut(statut);

        return visiteRepository.save(visite);
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