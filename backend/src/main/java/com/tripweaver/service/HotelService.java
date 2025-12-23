package com.tripweaver.service;

import com.tripweaver.model.Hotel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final List<Hotel> allHotels = new ArrayList<>();

    public HotelService() {
        loadHotels();
    }

    private void loadHotels() {
        try {
            ClassPathResource resource = new ClassPathResource("data/booking_hotel.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                // Regex to split by comma ignoring quotes
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                if (parts.length >= 9) {
                    Hotel hotel = new Hotel();
                    hotel.setName(clean(parts[0]));
                    hotel.setAddress(clean(parts[1]));
                    
                    try {
                        hotel.setRating(Double.parseDouble(clean(parts[2])));
                    } catch (NumberFormatException e) {
                        hotel.setRating(0.0);
                    }

                    hotel.setRoomType(clean(parts[6]));
                    
                    // Parse price: Remove non-numeric characters except dot
                    // Also handle potential encoding issues or spaces
                    String rawPrice = clean(parts[8]);
                    String priceStr = rawPrice.replaceAll("[^0-9.]", "");
                    try {
                        if (!priceStr.isEmpty()) {
                             hotel.setPrice(Double.parseDouble(priceStr));
                        }
                    } catch (NumberFormatException e) {
                        hotel.setPrice(0.0);
                    }
                    
                    allHotels.add(hotel);
                }
            }
            reader.close();
            System.out.println("Loaded " + allHotels.size() + " hotels from CSV.");
            if (!allHotels.isEmpty()) {
                System.out.println("Sample Hotel: " + allHotels.get(0).getName() + " | Price: " + allHotels.get(0).getPrice());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load hotels from CSV: " + e.getMessage());
        }
    }

    private String clean(String input) {
        if (input == null) return "";
        String s = input.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    public List<Hotel> searchHotels(String destination, Double maxPrice) {
        String destLower = destination.toLowerCase().trim();
        System.out.println("Searching hotels for: " + destLower + " with budget: " + maxPrice);
        
        List<Hotel> filtered = allHotels.stream()
                .filter(h -> {
                    boolean matches = h.getAddress().toLowerCase().contains(destLower) || 
                                      h.getName().toLowerCase().contains(destLower);
                    return matches;
                })
                .collect(Collectors.toList());

        System.out.println("Found " + filtered.size() + " hotels matching location: " + destLower);

        if (maxPrice != null && maxPrice > 0) {
            filtered = filtered.stream()
                    .filter(h -> h.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
            System.out.println("After budget filter (" + maxPrice + "): " + filtered.size() + " hotels remain.");
        }
        
        return filtered;
    }
}
