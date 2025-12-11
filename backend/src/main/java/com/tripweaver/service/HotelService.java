package com.tripweaver.service;

import com.tripweaver.model.Hotel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService {

    private final String apiKey = "YOUR_GEOAPIFY_KEY";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Hotel> searchHotels(String destination) {

        List<Hotel> hotels = new ArrayList<>();

        try {
            String url = "https://api.geoapify.com/v2/places?" +
                    "categories=accommodation.hotel" +
                    "&filter=city:" + destination +
                    "&apiKey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            JSONArray results = json.getJSONArray("features");

            for (int i = 0; i < results.length(); i++) {
                JSONObject prop = results.getJSONObject(i).getJSONObject("properties");

                Hotel hotel = new Hotel();
                hotel.setName(prop.optString("name"));
                hotel.setAddress(prop.optString("formatted"));
                hotel.setRating(prop.optDouble("rating", 4.0));

                hotels.add(hotel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hotels;
    }
}
