package com.tripweaver.service;

import com.tripweaver.model.TripResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    @Autowired
    private FlightService flightService;

    @Autowired
    private HotelService hotelService;

    public TripResponse getTripData(String destination, String date) {

        TripResponse response = new TripResponse();

        response.setFlights(flightService.searchFlights(destination, date));
        response.setHotels(hotelService.searchHotels(destination));

        return response;
    }
}
