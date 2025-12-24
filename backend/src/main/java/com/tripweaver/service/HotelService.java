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
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

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

                    String rawPrice = clean(parts[8]).replaceAll("[^0-9.]", "");
                    try {
                        hotel.setPricePerNight(rawPrice.isEmpty() ? 0.0 : Double.parseDouble(rawPrice));
                    } catch (NumberFormatException e) {
                        hotel.setPricePerNight(0.0);
                    }

                    allHotels.add(hotel);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
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

        List<Hotel> filtered = allHotels.stream()
                .filter(h -> h.getName().toLowerCase().contains(destLower)
                        || h.getAddress().toLowerCase().contains(destLower))
                .collect(Collectors.toList());

        if (maxPrice != null && maxPrice > 0) {
            filtered = filtered.stream()
                    .filter(h -> h.getPricePerNight() <= maxPrice)
                    .collect(Collectors.toList());
        }

        return filtered;
    }
}
