package com.example.update.entity;

public class NotificationOfTracking {
    private String message;

    private int point;

    private String title;

    private String name;

    private double price;

    public NotificationOfTracking(String message, int point, String title, String name, double price){
        this.message = message;
        this.point = point;
        this.title = title;
        this.name = name;
        this.price = price;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
