package com.example.update.entity;

import android.graphics.Bitmap;

public class Jewelry {
    private String c5ID;

    private String csqaqID;

    private String jewelryName;

    private String imageUrl;

    private Bitmap bitmap;

    private String shortName;

    private double price;

    private int quantity;

    private String exteriorColor;

    private String exteriorName;

    private String qualityColor;

    private String qualityName;

    public Jewelry(){

    }

    public Jewelry(String c5ID, String csqaqID, String jewelryName, String imageUrl) {
        this.c5ID = c5ID;
        this.csqaqID = csqaqID;
        this.jewelryName = jewelryName;
        this.imageUrl = imageUrl;
    }

    public String getC5ID() {
        return c5ID;
    }

    public void setC5ID(String c5ID) {
        this.c5ID = c5ID;
    }

    public String getCsqaqID() {
        return csqaqID;
    }

    public void setCsqaqID(String csqaqID) {
        this.csqaqID = csqaqID;
    }

    public String getJewelryName() {
        return jewelryName;
    }

    public void setJewelryName(String jewelryName) {
        this.jewelryName = jewelryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getExteriorColor() {
        return exteriorColor;
    }

    public void setExteriorColor(String exteriorColor) {
        this.exteriorColor = exteriorColor;
    }

    public String getExteriorName() {
        return exteriorName;
    }

    public void setExteriorName(String exteriorName) {
        this.exteriorName = exteriorName;
    }

    public String getQualityColor() {
        return qualityColor;
    }

    public void setQualityColor(String qualityColor) {
        this.qualityColor = qualityColor;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
