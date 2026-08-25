package com.salamasoa.salamasoa_app.model;

public enum StatutVisite {
    PLANIFIE("Planifiée"),
    En_COURS("En cours"),
    TERMINEE("termniée"),
    ANNULEE("Annulée");

    private final String libelle;
    StatutVisite(String _libelle){
        this.libelle = _libelle;
    }
    public String getLibelle(){
        return  libelle;
    }
}
