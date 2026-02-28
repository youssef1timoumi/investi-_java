package edu.Investi.entities;

import edu.Investi.interfaces.statut;

import java.time.LocalDateTime;

public class Inscription {
    private int id_inscription;
    private int id_user;
    private int id_evenement;
    private LocalDateTime date_inscription;
    private String statut; // confirme, en_attente, annule

    public Inscription() {
    }

    public Inscription(int idUtilisateur, int idEvenement, statut statut) {
        this.id_user = idUtilisateur;
        this.id_evenement = idEvenement;
        this.statut = statut.name();
    }

    public int getIdInscription() {
        return id_inscription;
    }

    public void setIdInscription(int idInscription) {
        this.id_inscription = idInscription;
    }

    public int getIdUtilisateur() {
        return id_user;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.id_user = idUtilisateur;
    }

    public int getIdEvenement() {
        return id_evenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.id_evenement = idEvenement;
    }

    public LocalDateTime getDateInscription() {
        return date_inscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.date_inscription = dateInscription;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Inscription{" +
                "idInscription=" + id_inscription +
                ", idUtilisateur=" + id_user +
                ", idEvenement=" + id_evenement +
                ", statut='" + statut + '\'' +
                ", dateInscription=" + date_inscription +
                '}';
    }
}
