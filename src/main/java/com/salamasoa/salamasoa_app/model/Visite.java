package com.salamasoa.salamasoa_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "visite")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Visite {

    @Id
    @Column(name = "codevisite", length = 10)
    private String codevisite;

    @Column(name = "statut", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StatutVisite statut;

    @Column(name = "dateheure", nullable = false)
    private LocalDateTime dateheure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codemed", nullable = false)
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codepat", nullable = false)
    private Patient patient;
}