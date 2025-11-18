package com.example.ticketapp.domain.model;

import java.util.List;

public class Cinema {
    private String id;
    private  String name;
    private String logoUrl;
    private double rating;
   private String info;
   private  String address;
   private String city;
   private List<Room> rooms;
    public Cinema(String id,String name, String logoUrl, double rating, String info, String address, String city, List<Room> rooms) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.rating = rating;
        this.info = info;
        this.address = address;
        this.city = city;
        this.rooms = rooms;
        this.id = id;
    }

    public String getUid() {
        return id;
    }

    public void setUid(String uid) {
        this.id = uid;
    }

    public String getName() {
        return name;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public double getRating() {
        return rating;
    }

    public String getInfo() {
        return info;
    }
}
