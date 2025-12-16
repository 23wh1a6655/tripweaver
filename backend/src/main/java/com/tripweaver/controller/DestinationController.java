package com.tripweaver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripweaver.model.Destination;
import com.tripweaver.service.DestinationService;

@RestController
@RequestMapping("/api/destination")

public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @GetMapping("/search/google")
    public List<Destination> searchGoogle(
            @RequestParam String query,
            @RequestParam(defaultValue = "accommodation") String category
    ) {
        return destinationService.searchDestinationsGoogle(query, category);
    }

    @GetMapping("/search/all")
    public List<Destination> searchAll(@RequestParam String query) {
        return destinationService.searchAllGoogle(query);
    }

    @GetMapping("/photos/{placeId}")
    public List<String> getPhotos(@PathVariable String placeId) {
        return destinationService.getPlacePhotosLegacy(placeId);
    }
}
