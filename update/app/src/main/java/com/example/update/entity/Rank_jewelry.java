package com.example.update.entity;

import android.graphics.Bitmap;

public class Rank_jewelry {
    private String jewelryName;

    private String c5ID;

    private String imageUrl;

    private Bitmap bitmap;

    private String color;

    private double price;

    private String balance;

    private int sell_number;

    private int buy_number;

    private String scale;



    public String getJewelryName() {
        return jewelryName;
    }

    public void setJewelryName(String jewelryName) {
        this.jewelryName = jewelryName;
    }

    public String getC5ID() {
        return c5ID;
    }

    public void setC5ID(String c5ID) {
        this.c5ID = c5ID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getSell_number() {
        return sell_number;
    }

    public void setSell_number(int sell_number) {
        this.sell_number = sell_number;
    }

    public int getBuy_number() {
        return buy_number;
    }

    public void setBuy_number(int buy_number) {
        this.buy_number = buy_number;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
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
}
