package com.salamasoa.salamasoa_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medecin")
@AllArgsConstructor
@NoArgsConstructor
public class Medecin {
    @Id
    @Column(name = "codemed", length = 10)
    private String codemed;
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    @Column(name = "prenom", nullable = true, length = 100)
    private String prenom;
    @Column(name = "grade", nullable = false)
    private String grade;
    @Column(name = "actif")
    private boolean actif;
}
