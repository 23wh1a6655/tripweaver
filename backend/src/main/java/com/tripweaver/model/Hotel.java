package com.tripweaver.model;

public class Hotel {

    private String name;
    private String address;
    private double rating;
    private double price;
    private String roomType;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}
