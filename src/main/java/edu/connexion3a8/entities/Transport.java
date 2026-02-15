package edu.connexion3a8.entities;

public class Transport {
    private int idTransport;
    private int idType;
    private String typeLibelle;

    public Transport() {
    }

    public Transport(int idType) {
        this.idType = idType;
    }

    public int getIdTransport() {
        return idTransport;
    }

    public void setIdTransport(int idTransport) {
        this.idTransport = idTransport;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getTypeLibelle() {
        return typeLibelle;
    }

    public void setTypeLibelle(String typeLibelle) {
        this.typeLibelle = typeLibelle;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "idTransport=" + idTransport +
                ", idType=" + idType +
                ", typeLibelle='" + typeLibelle + '\'' +
                '}';
    }
}
