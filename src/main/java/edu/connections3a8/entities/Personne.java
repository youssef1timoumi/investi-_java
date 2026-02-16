package edu.connections3a8.entities;

import java.util.Objects;

public class Personne {
    private String nom;
    private String prenom;
    private int id ;

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return id == personne.id && Objects.equals(nom, personne.nom) && Objects.equals(prenom, personne.prenom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, prenom, id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public Personne(){}
    public Personne(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getPrenom() {
        return prenom;
    }

    public int getId() {
        return id;
    }
}


