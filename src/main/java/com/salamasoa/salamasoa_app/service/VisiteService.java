package com.salamasoa.salamasoa_app.service;

import com.salamasoa.salamasoa_app.model.StatutVisite;
import com.salamasoa.salamasoa_app.model.Visite;
import com.salamasoa.salamasoa_app.repository.VisiteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class VisiteService {
    private final VisiteRepository visiteRepository;

    @Autowired
    public VisiteService(VisiteRepository visiteRepository) {
        this.visiteRepository = visiteRepository;

    }
    public List<Visite> getAllVisites() {
        return visiteRepository.findAll();
    }

    public Optional<Visite> getVisiteByCodevisite(String codevisite) {
        return visiteRepository.findById(codevisite);
    }

    public Visite saveVisite(Visite visite) {
        return visiteRepository.save(visite);
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
}
