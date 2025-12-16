package com.tripweaver.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tripweaver.model.Flight;

@Service
public class FlightService {

    @Value("${aviationstack.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Flight> searchFlights(String origin, String destination, String date) {
        List<Flight> flights = new ArrayList<>();

        try {
            if (apiKey != null && !apiKey.isEmpty()) {
                String url = "http://api.aviationstack.com/v1/flights"
                        + "?access_key=" + apiKey
                        + "&dep_iata=" + origin
                        + "&arr_iata=" + destination
                        + "&flight_date=" + date;

                String response = restTemplate.getForObject(url, String.class);
                JSONObject json = new JSONObject(response);
                JSONArray data = json.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject f = data.getJSONObject(i);
                    Flight flight = new Flight();
                    flight.setAirline(f.getJSONObject("airline").optString("name"));
                    flight.setFlightNumber(f.getJSONObject("flight").optString("number"));
                    flight.setDepartureAirport(f.getJSONObject("departure").optString("iata"));
                    flight.setArrivalAirport(f.getJSONObject("arrival").optString("iata"));
                    flight.setDepartureTime(f.getJSONObject("departure").optString("scheduled"));
                    flight.setArrivalTime(f.getJSONObject("arrival").optString("scheduled"));
                    flights.add(flight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback mock if API fails
        if (flights.isEmpty()) {
            flights = mockFlights(origin, destination, date);
        }

        return flights;
    }

    private List<Flight> mockFlights(String origin, String destination, String date) {
        List<Flight> demo = new ArrayList<>();
        String[] airlines = {"IndiGo", "Air India", "Vistara", "SpiceJet"};
        String[] times = {"08:25", "11:10", "15:40", "21:05"};

        for (int i = 0; i < airlines.length; i++) {
            Flight f = new Flight();
            f.setAirline(airlines[i]);
            f.setFlightNumber(airlines[i].substring(0, 2).toUpperCase() + (100 + i));
            f.setDepartureAirport(origin);
            f.setArrivalAirport(destination);
            f.setDepartureTime(date + " " + times[i]);
            f.setArrivalTime(date + " " + (i < 2 ? "10:25" : "23:05"));
            demo.add(f);
        }
        return demo;
    }
}
