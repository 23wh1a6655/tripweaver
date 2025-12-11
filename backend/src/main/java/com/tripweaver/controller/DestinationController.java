package com.tripweaver.controller;

import com.tripweaver.model.Destination;
import com.tripweaver.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destination")
@CrossOrigin(origins = "*")
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @GetMapping("/search")
    public List<Destination> searchDestinations(
            @RequestParam String query,
            @RequestParam(defaultValue = "tourism") String category
    ) {
        return destinationService.searchDestinations(query, category);
    }

    @GetMapping("/details/{placeId}")
    public String getDestinationDetails(@PathVariable String placeId) {
        return destinationService.getDestinationDetails(placeId);
    }
}