package com.tripweaver.controller;

import com.tripweaver.model.TripResponse;
import com.tripweaver.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin("*")
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping("/search")
    public TripResponse searchTrip(
            @RequestParam String destination,
            @RequestParam String date
    ) {
        return tripService.getTripData(destination, date);
    }
}
