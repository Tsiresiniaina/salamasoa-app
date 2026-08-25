package com.salamasoa.salamasoa_app.service;

import com.salamasoa.salamasoa_app.model.Medecin;
import com.salamasoa.salamasoa_app.repository.MedecinRepostitory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class MedecinService {
    private  final MedecinRepostitory medecinRepository;

    @Autowired
    public MedecinService(MedecinRepostitory medecinRepository) {
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
    public List<Medecin> searchBySpecialite(String grade) {
        return medecinRepository.findByGradeContainingIgnoreCase(grade);
    }
}
