package com.tripweaver.service;

import com.tripweaver.model.Destination;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DestinationService {

    @Value("${geoapify.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Destination> searchDestinations(String query, String category) {
        try {
           
            String geoUrl = "https://api.geoapify.com/v1/geocode/search?text=" + query +
                            "&apiKey=" + apiKey;
            System.out.println("Geo URL: " + geoUrl);

           
            org.springframework.http.ResponseEntity<String> geoEntity =
                    restTemplate.getForEntity(geoUrl, String.class);

            if (!geoEntity.getStatusCode().is2xxSuccessful() || geoEntity.getBody() == null) {
                System.out.println("Geocode call failed: status=" + geoEntity.getStatusCode());
                return new ArrayList<>();
            }

            String geoResponse = geoEntity.getBody();
            System.out.println("Geo Response: " + geoResponse);

            JSONObject geoJson = new JSONObject(geoResponse);
            JSONArray results = geoJson.optJSONArray("features");

           
            if (results == null || results.length() == 0) {
                System.out.println("No geocoding results found for query: " + query);
                return new ArrayList<>();
            }

            JSONObject firstFeature = results.optJSONObject(0);
            if (firstFeature == null) {
                System.out.println("First geocoding feature is null for query: " + query);
                return new ArrayList<>();
            }

            JSONObject geometry = firstFeature.optJSONObject("geometry");
            JSONArray coordinates = (geometry != null) ? geometry.optJSONArray("coordinates") : null;
            if (coordinates == null || coordinates.length() < 2) {
                System.out.println("Invalid geometry in geocoding results for query: " + query);
                return new ArrayList<>();
            }

            double lon = coordinates.optDouble(0);
            double lat = coordinates.optDouble(1);

            System.out.println("Resolved coordinates: lon=" + lon + ", lat=" + lat);

            
            String placesUrl = "https://api.geoapify.com/v2/places?" +
                    "categories=" + category +
                    "&filter=circle:" + lon + "," + lat + ",5000" +
                    "&limit=20" +
                    "&apiKey=" + apiKey;

            System.out.println("Places URL: " + placesUrl);

            org.springframework.http.ResponseEntity<String> placesEntity =
                    restTemplate.getForEntity(placesUrl, String.class);

            if (!placesEntity.getStatusCode().is2xxSuccessful() || placesEntity.getBody() == null) {
                System.out.println("Places call failed: status=" + placesEntity.getStatusCode());
                return new ArrayList<>();
            }

            String response = placesEntity.getBody();
            System.out.println("Places Response: " + response);

            
            List<Destination> destinations = new ArrayList<>();
            JSONObject json = new JSONObject(response);
            JSONArray features = json.optJSONArray("features");
            if (features == null || features.length() == 0) {
                System.out.println("No places found for query: " + query + " and category: " + category);
                return new ArrayList<>();
            }

            for (int i = 0; i < features.length(); i++) {
                JSONObject prop = features.getJSONObject(i).getJSONObject("properties");

                JSONArray categoriesArray = prop.optJSONArray("categories");
                String categories = "";
                if (categoriesArray != null) {
                    List<String> list = new ArrayList<>();
                    for (int j = 0; j < categoriesArray.length(); j++) {
                        list.add(categoriesArray.getString(j));
                    }
                    categories = String.join(", ", list);
                }

                String name = prop.optString("name", "Unknown");
                String address = prop.optString("formatted", "Not Available");
                double latRes = prop.optDouble("lat", 0.0);
                double lonRes = prop.optDouble("lon", 0.0);
                String placeId = prop.optString("place_id", "");

                destinations.add(new Destination(name, address, latRes, lonRes, categories, placeId));
            }

            System.out.println("Parsed destinations count: " + destinations.size());
            return destinations;
        } catch (Exception e) {
            System.err.println("Geoapify API error: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getDestinationDetails(String placeId) {
        String url = "https://api.geoapify.com/v2/place-details?" +
                "id=" + placeId +
                "&apiKey=" + apiKey;
        System.out.println("Details URL: " + url);
        return restTemplate.getForObject(url, String.class);
    }
}