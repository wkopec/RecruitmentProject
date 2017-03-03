package com.recruitmentproject;

public class LocationList {

    private int id;
    private String name;
    private String avatar;
    private double lng;
    private double lat;

    public LocationList(int id, String name, String avatar, double lng, double lat) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.lng = lng;
        this.lat = lat;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }



}
