package com.tripweaver.service;

import com.tripweaver.model.Flight;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    private final String apiKey = "YOUR_AVIATIONSTACK_API_KEY";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Flight> searchFlights(String destination, String date) {
        List<Flight> list = new ArrayList<>();

        try {
            String url =
                    "http://api.aviationstack.com/v1/flights?access_key=" + apiKey +
                    "&arr_iata=" + destination +
                    "&flight_date=" + date;

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);
            JSONArray flights = json.getJSONArray("data");

            for (int i = 0; i < flights.length(); i++) {
                JSONObject f = flights.getJSONObject(i);

                Flight flight = new Flight();
                flight.setAirline(f.getJSONObject("airline").optString("name"));
                flight.setFlightNumber(f.getJSONObject("flight").optString("number"));
                flight.setDepartureTime(
                        f.getJSONObject("departure").optString("scheduled")
                );
                flight.setArrivalTime(
                        f.getJSONObject("arrival").optString("scheduled")
                );

                list.add(flight);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
