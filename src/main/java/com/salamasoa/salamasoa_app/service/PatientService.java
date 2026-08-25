package com.salamasoa.salamasoa_app.service;

import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private static final String PATIENT_CODE_PREFIX = "PT-";

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientByCodepat(String codepat) {
        return patientRepository.findById(codepat);
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public void deletePatient(String codepat) {
        patientRepository.deleteById(codepat);
    }

    /**
     * Inverse le statut actif/inactif d'un patient.
     * <p>
     * Actif   → Inactif
     * Inactif → Actif
     */
    public Patient togglePatientStatus(String codepat) {
        Patient patient = patientRepository.findById(codepat)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient introuvable : " + codepat
                ));

        patient.setActif(!patient.isActif());

        return patientRepository.save(patient);
    }

    public List<Patient> searchPatients(String keyword) {
        return patientRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                        keyword,
                        keyword
                );
    }

    /**
     * Crée et enregistre un nouveau patient.
     *
     * @param fullName nom complet saisi dans le formulaire
     * @param sexe     HOMME ou FEMME
     * @param adresse  adresse saisie dans le formulaire
     * @return le patient sauvegardé en base de données
     */
    public Patient createPatient(
            String fullName,
            String sexe,
            String adresse
    ) {
        validatePatientData(fullName, sexe, adresse);

        Patient patient = new Patient();

        patient.setCodepat(generatePatientCode());
        patient.setNom(fullName.trim());

        /*
         * Le formulaire possède pour le moment un seul champ
         * « Nom complet ». On conserve donc le nom entier dans nom
         * et prenom reste vide.
         */
        patient.setPrenom(null);

        patient.setSexe(convertSexe(sexe));
        patient.setAdresse(adresse.trim());
        patient.setActif(true);

        return patientRepository.save(patient);
    }

    /**
     * Met à jour les informations modifiables d'un patient existant.
     * <p>
     * Le code patient et le statut actif/inactif ne sont pas modifiés ici.
     */
    public Patient updatePatient(
            String codepat,
            String fullName,
            String sexe,
            String adresse
    ) {
        validatePatientData(fullName, sexe, adresse);

        Patient patient = patientRepository.findById(codepat)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient introuvable : " + codepat
                ));

        patient.setNom(fullName.trim());

        /*
         * Le formulaire actuel possède un champ « Nom complet » unique.
         * Nous conservons donc le prénom à null pour le moment.
         */
        patient.setPrenom(null);

        patient.setSexe(convertSexe(sexe));
        patient.setAdresse(adresse.trim());

        /*
         * patient.setActif(...) n'est volontairement pas présent :
         * une modification de profil ne doit pas changer le statut
         * Actif / Inactif du patient.
         */
        return patientRepository.save(patient);
    }

    private void validatePatientData(
            String fullName,
            String sexe,
            String adresse
    ) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom complet du patient est obligatoire."
            );
        }

        if (sexe == null || sexe.isBlank()) {
            throw new IllegalArgumentException(
                    "Le sexe du patient est obligatoire."
            );
        }

        if (adresse == null || adresse.isBlank()) {
            throw new IllegalArgumentException(
                    "L'adresse du patient est obligatoire."
            );
        }
    }

    private char convertSexe(String sexe) {
        return switch (sexe.trim().toUpperCase()) {
            case "HOMME", "H" -> 'H';
            case "FEMME", "F" -> 'F';

            default -> throw new IllegalArgumentException(
                    "Le sexe doit être HOMME ou FEMME."
            );
        };
    }

    /**
     * Génère PT-000001, PT-000002, etc.
     * <p>
     * synchronized évite que deux demandes simultanées dans la même
     * application reçoivent le même code.
     */
    private synchronized String generatePatientCode() {
        Optional<Patient> lastPatientOptional =
                patientRepository
                        .findTopByCodepatStartingWithOrderByCodepatDesc(
                                PATIENT_CODE_PREFIX
                        );

        int nextNumber = 1;

        if (lastPatientOptional.isPresent()) {
            String lastCode = lastPatientOptional.get().getCodepat();

            String numericPart =
                    lastCode.substring(PATIENT_CODE_PREFIX.length());

            nextNumber = Integer.parseInt(numericPart) + 1;
        }

        return PATIENT_CODE_PREFIX + String.format("%06d", nextNumber);
    }
}