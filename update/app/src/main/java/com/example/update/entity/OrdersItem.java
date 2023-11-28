package com.example.update.entity;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class OrdersItem {
    private String jewelryName;

    private double average;

    private String image;

    private Bitmap bitmap;

    private String exteriorColor;

    private String exteriorName;

    private String qualityColor;

    private String qualityName;

    private List<OrdersTimeItem> list;

    public OrdersItem(){
        list = new ArrayList<>();
        average = 0;
    }

    public String getJewelryName() {
        return jewelryName;
    }

    public void setJewelryName(String jewelryName) {
        this.jewelryName = jewelryName;
    }

    public List<OrdersTimeItem> getList() {
        return list;
    }

    public void setList(List<OrdersTimeItem> list) {
        this.list = list;
    }

    public void add(OrdersTimeItem ordersTimeItem){
        average = (average * list.size() + ordersTimeItem.getPrice())/(list.size() + 1);
        list.add(ordersTimeItem);
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
}
