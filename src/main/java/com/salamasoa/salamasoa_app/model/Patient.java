package com.salamasoa.salamasoa_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patient")
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @Column(name = "codepat",length = 10)
    private String codepat;
    @Column(name = "nom",nullable = false,length = 100)
    private String nom;
    @Column(name = "prenom",nullable = true,length = 100)
    private String prenom;
    @Column(name = "sexe",nullable = false)
    private char sexe;
    @Column(name = "adresse",nullable = false,length = 255)
    private String adresse;
    @Column(name = "actif")
    private boolean actif;
}
