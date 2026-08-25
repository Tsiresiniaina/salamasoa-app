package com.salamasoa.salamasoa_app.service;

import com.salamasoa.salamasoa_app.model.Patient;
import com.salamasoa.salamasoa_app.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private  final PatientRepository patientRepository;

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

    public List<Patient> searchPatients(String keyword) {
        return patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword);
    }
}
