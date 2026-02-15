package edu.connexion3a8.entities;

public class TypeTransport {
    private int idType;
    private String libelle;

    public TypeTransport() {
    }

    public TypeTransport(String libelle) {
        this.libelle = libelle;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return "TypeTransport{" +
                "idType=" + idType +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
