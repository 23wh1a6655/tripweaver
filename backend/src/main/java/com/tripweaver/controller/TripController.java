package com.tripweaver.controller;

import com.tripweaver.model.TripResponse;
import com.tripweaver.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping("/search")
    public TripResponse searchTrip(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date,
            @RequestParam(required = false) Double budget
    ) {
        // This mapping requires origin, destination, and date.
        return tripService.getTripData(origin, destination, date, budget);
    }
}