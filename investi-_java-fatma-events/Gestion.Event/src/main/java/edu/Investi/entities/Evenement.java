package edu.Investi.entities;

import java.time.LocalDateTime;

public class Evenement {
    private int idEvenement;
    private int idMentor;
    private String titre;
    private String contenu;
    private String lieu;
    private Double lieuLatitude;
    private Double lieuLongitude;
    private String imageUrl;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    public Evenement() {
    }

    public Evenement(int idMentor, String titre, String contenu, String lieu,
            LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.idMentor = idMentor;
        this.titre = titre;
        this.contenu = contenu;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Evenement(int idMentor, String titre, String contenu, String lieu,
            Double lieuLatitude, Double lieuLongitude, String imageUrl,
            LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.idMentor = idMentor;
        this.titre = titre;
        this.contenu = contenu;
        this.lieu = lieu;
        this.lieuLatitude = lieuLatitude;
        this.lieuLongitude = lieuLongitude;
        this.imageUrl = imageUrl;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }

    public int getIdMentor() {
        return idMentor;
    }

    public void setIdMentor(int idMentor) {
        this.idMentor = idMentor;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLieuLatitude() {
        return lieuLatitude;
    }

    public void setLieuLatitude(Double lieuLatitude) {
        this.lieuLatitude = lieuLatitude;
    }

    public Double getLieuLongitude() {
        return lieuLongitude;
    }

    public void setLieuLongitude(Double lieuLongitude) {
        this.lieuLongitude = lieuLongitude;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "idEvenement=" + idEvenement +
                ", titre='" + titre + '\'' +
                ", lieu='" + lieu + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                '}';
    }

}
