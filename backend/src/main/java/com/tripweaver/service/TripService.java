package com.tripweaver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripweaver.model.TripResponse;

@Service
public class TripService {

    @Autowired
    private FlightService flightService;

    @Autowired
    private HotelService hotelService;

    public TripResponse getTripData(String origin, String destination, String date, Double budget) {
        TripResponse response = new TripResponse();
        response.setFlights(flightService.searchFlights(origin, destination, date));
        response.setHotels(hotelService.searchHotels(destination, budget));
        return response;
    }
}
