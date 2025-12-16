package com.tripweaver.model;

import java.util.List;

public class TripResponse {
    private List<Flight> flights;
    private List<Hotel> hotels;

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }
    public List<Hotel> getHotels() { return hotels; }
    public void setHotels(List<Hotel> hotels) { this.hotels = hotels; }
}
