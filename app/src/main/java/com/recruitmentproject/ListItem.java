package com.recruitmentproject;

import java.util.ArrayList;

public class ListItem {

    private String name;
    private int id;
    private double latitude;
    private double longitude;

    public ListItem(int id, String name, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static class List extends ArrayList<ListItem> {
    }

}
