  package com.tripweaver.controller;

	import com.tripweaver.model.Flight;
	import com.tripweaver.model.Hotel;
	import com.tripweaver.model.Destination;
	import com.tripweaver.service.FlightService;
	import com.tripweaver.service.HotelService;
	import com.tripweaver.service.DestinationService;

	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.web.bind.annotation.*;

	import java.util.*;
	import java.util.stream.Collectors;

	@RestController
	@RequestMapping("/api/budget")
	public class BudgetController {

	    @Autowired
	    private FlightService flightService;

	    @Autowired
	    private HotelService hotelService;

	    @Autowired
	    private DestinationService destinationService;

	    @PostMapping("/plan")
	    public Map<String, Object> searchBudgetTravel(@RequestBody Map<String, Object> request) {

	        String origin = (String) request.get("origin");
	        String destination = (String) request.get("destination");
	        String date = (String) request.get("date");
	        double totalBudget = Double.parseDouble(request.get("budget").toString());
	        int nights = Optional.ofNullable(request.get("nights"))
	                .map(n -> Integer.parseInt(n.toString()))
	                .orElse(1);


	        // 1️⃣ Budget split
	        double flightBudget = totalBudget * 0.4;
	        double hotelBudget = totalBudget * 0.5;
	        double activitiesBudget = totalBudget * 0.1;

	        // 2️⃣ Get flights and filter by budget
	        List<Flight> flights = flightService.searchFlights(origin, destination, date)
	                .stream()
	                .filter(f -> f.getPrice() <= flightBudget)
	                .collect(Collectors.toList());

	        // 3️⃣ Get hotels and filter by total stay budget
	        List<Hotel> hotels = hotelService.searchHotels(destination)
	                .stream()
	                .filter(h -> (h.getPricePerNight() * nights) <= hotelBudget)
	                .collect(Collectors.toList());

	        // 4️⃣ Get tourist places (top-rated only)
	        List<Destination> places = destinationService.searchDestinationsGoogle(destination, "tourist_attraction")
	                .stream()
	                .filter(p -> p.getRating() >= 4.0)
	                .collect(Collectors.toList());

	        Map<String, Object> response = new HashMap<>();
	        response.put("flights", flights);
	        response.put("hotels", hotels);
	        response.put("places", places);
	        response.put("budgetAllocation", Map.of(
	                "flights", flightBudget,
	                "hotels", hotelBudget,
	                "activities", activitiesBudget
	        ));

	        return response;
	    }
	}
