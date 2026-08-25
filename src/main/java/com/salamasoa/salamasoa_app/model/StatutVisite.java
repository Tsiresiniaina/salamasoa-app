package com.salamasoa.salamasoa_app.model;

public enum StatutVisite {

    PLANIFIEE("Planifiée"),
    EN_COURS("En cours"),
    TERMINEE("Terminée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutVisite(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}