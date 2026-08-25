package com.salamasoa.salamasoa_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visite")
@AllArgsConstructor
@NoArgsConstructor
public class Visite {
    @Id
    @Column(name = "codevisite",length = 10)
    private String codevisite;
    @Column(name = "statut",length = 30)
    private String statut;
    @Column(name = "dateheure")
    private LocalDateTime dateheure;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codemed",nullable = false)
    private  Medecin medecin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codepat",nullable = false)
    private  Patient patient;
}
