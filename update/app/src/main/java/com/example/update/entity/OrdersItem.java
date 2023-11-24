package com.example.update.entity;

import java.util.ArrayList;
import java.util.List;

public class OrdersItem {
    private String jewelryName;

    private double average;

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
}
