package com.pavchishin.skladhelper;

public class SparePart {

    int id;
    String barcodePart;
    String artikulPart;
    String namePart;
    String placePart;
    int quantityDocPart;
    int quantityRealPart;

    public SparePart() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcodePart() {
        return barcodePart;
    }

    public void setBarcodePart(String barcodePart) {
        this.barcodePart = barcodePart;
    }

    public String getArtikulPart() {
        return artikulPart;
    }

    public void setArtikulPart(String artikulPart) {
        this.artikulPart = artikulPart;
    }

    public String getNamePart() {
        return namePart;
    }

    public void setNamePart(String namePart) {
        this.namePart = namePart;
    }

    public String getPlacePart() {
        return placePart;
    }

    public void setPlacePart(String placePart) {
        this.placePart = placePart;
    }

    public int getQuantityDocPart() {
        return quantityDocPart;
    }

    public void setQuantityDocPart(int quantityDocPart) {
        this.quantityDocPart = quantityDocPart;
    }

    public int getQuantityRealPart() {
        return quantityRealPart;
    }

    public void setQuantityRealPart(int quantityRealPart) {
        this.quantityRealPart = quantityRealPart;
    }
}
