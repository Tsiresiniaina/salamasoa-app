package com.salamasoa.salamasoa_app.service;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.repository.MedecinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedecinService {

    private static final String MEDECIN_CODE_PREFIX = "MD-";

    private final MedecinRepository medecinRepository;

    @Autowired
    public MedecinService(MedecinRepository medecinRepository) {
        this.medecinRepository = medecinRepository;
    }

    public List<Medecin> getAllMedecins() {
        return medecinRepository.findAll();
    }

    public Optional<Medecin> getMedecinByCodeMed(String codemed) {
        return medecinRepository.findById(codemed);
    }

    public Medecin saveMedecin(Medecin medecin) {
        return medecinRepository.save(medecin);
    }

    public void deleteMedecin(String codemed) {
        medecinRepository.deleteById(codemed);
    }

    public List<Medecin> searchByGrade(String grade) {
        return medecinRepository.findByGradeContainingIgnoreCase(grade);
    }

    /**
     * Crée un médecin avec un code automatique :
     * MD-000001, MD-000002, etc.
     */
    public Medecin createMedecin(
            String fullName,
            String grade
    ) {
        validateMedecinData(fullName, grade);

        Medecin medecin = new Medecin();

        medecin.setCodemed(generateMedecinCode());

        /*
         * Le formulaire contient actuellement un seul champ
         * « Nom complet du docteur ».
         */
        medecin.setNom(fullName.trim());
        medecin.setPrenom(null);

        medecin.setGrade(grade);
        medecin.setActif(true);

        return medecinRepository.save(medecin);
    }

    /**
     * Modifie les informations du médecin sans toucher
     * au code médecin ni à son statut actif/inactif.
     */
    public Medecin updateMedecin(
            String codemed,
            String fullName,
            String grade
    ) {
        validateMedecinData(fullName, grade);

        Medecin medecin = medecinRepository.findById(codemed)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin introuvable : " + codemed
                ));

        medecin.setNom(fullName.trim());
        medecin.setPrenom(null);
        medecin.setGrade(grade);

        return medecinRepository.save(medecin);
    }

    /**
     * Active un médecin inactif ou désactive un médecin actif.
     */
    public Medecin toggleMedecinStatus(String codemed) {
        Medecin medecin = medecinRepository.findById(codemed)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médecin introuvable : " + codemed
                ));

        medecin.setActif(!medecin.isActif());

        return medecinRepository.save(medecin);
    }

    private void validateMedecinData(
            String fullName,
            String grade
    ) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom complet du médecin est obligatoire."
            );
        }

        if (grade == null || grade.isBlank()) {
            throw new IllegalArgumentException(
                    "Le grade du médecin est obligatoire."
            );
        }
    }

    /**
     * Génère MD-000001, MD-000002, etc.
     */
    private synchronized String generateMedecinCode() {
        Optional<Medecin> lastMedecinOptional =
                medecinRepository
                        .findTopByCodemedStartingWithOrderByCodemedDesc(
                                MEDECIN_CODE_PREFIX
                        );

        int nextNumber = 1;

        if (lastMedecinOptional.isPresent()) {
            String lastCode = lastMedecinOptional.get().getCodemed();

            String numericPart =
                    lastCode.substring(MEDECIN_CODE_PREFIX.length());

            nextNumber = Integer.parseInt(numericPart) + 1;
        }

        return MEDECIN_CODE_PREFIX + String.format("%06d", nextNumber);
    }
}